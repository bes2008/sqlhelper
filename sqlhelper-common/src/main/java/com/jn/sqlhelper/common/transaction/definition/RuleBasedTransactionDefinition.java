/*
 * Copyright 2021 the original author or authors.
 *
 * Licensed under the Apache, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at  http://www.gnu.org/licenses/lgpl-2.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jn.sqlhelper.common.transaction.definition;


import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * TransactionAttribute implementation that works out whether a given exception
 * should cause transaction rollback by applying a number of rollback rules,
 * both positive and negative. If no rules are relevant to the exception, it
 * behaves like DefaultTransactionAttribute (rolling back on runtime exceptions).
 * <p>
 * 这部分是从Spring迁移过来的，为了兼容Spring 事务定义的配置
 */
public class RuleBasedTransactionDefinition extends DefaultTransactionDefinition implements Serializable {

    /**
     * Prefix for rollback-on-exception rules in description strings
     */
    public static final String PREFIX_ROLLBACK_RULE = "-";

    /**
     * Prefix for commit-on-exception rules in description strings
     */
    public static final String PREFIX_COMMIT_RULE = "+";


    /**
     * Static for optimal serializability
     */
    private static final Logger logger = LoggerFactory.getLogger(RuleBasedTransactionDefinition.class);

    private List<RollbackRuleAttribute> rollbackRules;


    /**
     * Create a new RuleBasedTransactionAttribute, with default settings.
     * Can be modified through bean property setters.
     *
     * @see #setName
     * @see #setRollbackRules
     */
    public RuleBasedTransactionDefinition() {
        super();
    }


    /**
     * Create a new DefaultTransactionAttribute with the given
     * propagation behavior. Can be modified through bean property setters.
     * TransactionDefinition interface
     *
     * @param rollbackRules the list of RollbackRuleAttributes to apply
     */
    public RuleBasedTransactionDefinition(List<RollbackRuleAttribute> rollbackRules) {
        this.rollbackRules = rollbackRules;
    }


    /**
     * Set the list of {@code RollbackRuleAttribute} objects
     * (and/or {@code NoRollbackRuleAttribute} objects) to apply.
     *
     * @see RollbackRuleAttribute
     * @see NoRollbackRuleAttribute
     */
    public void setRollbackRules(List<RollbackRuleAttribute> rollbackRules) {
        this.rollbackRules = rollbackRules;
    }

    /**
     * Return the list of {@code RollbackRuleAttribute} objects
     * (never {@code null}).
     */
    public List<RollbackRuleAttribute> getRollbackRules() {
        if (this.rollbackRules == null) {
            this.rollbackRules = new LinkedList<RollbackRuleAttribute>();
        }
        return this.rollbackRules;
    }


    /**
     * Winning rule is the shallowest rule (that is, the closest in the
     * inheritance hierarchy to the exception). If no rule applies (-1),
     * return false.
     */
    @Override
    public boolean rollbackOn(Throwable ex) {
        if (logger.isTraceEnabled()) {
            logger.trace("Applying rules to determine whether transaction should rollback on " + ex);
        }

        RollbackRuleAttribute winner = null;
        int deepest = Integer.MAX_VALUE;

        if (this.rollbackRules != null) {
            for (RollbackRuleAttribute rule : this.rollbackRules) {
                int depth = rule.getDepth(ex);
                if (depth >= 0 && depth < deepest) {
                    deepest = depth;
                    winner = rule;
                }
            }
        }

        if (logger.isTraceEnabled()) {
            logger.trace("Winning rollback rule is: " + winner);
        }

        // User superclass behavior (rollback on unchecked) if no rule matches.
        if (winner == null) {
            logger.trace("No relevant rollback rule found: applying default rules");
            return super.rollbackOn(ex);
        }

        return !(winner instanceof NoRollbackRuleAttribute);
    }


    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(256);
        if (this.rollbackRules != null) {
            for (RollbackRuleAttribute rule : this.rollbackRules) {
                String sign = (rule instanceof NoRollbackRuleAttribute ? PREFIX_COMMIT_RULE : PREFIX_ROLLBACK_RULE);
                result.append(',').append(sign).append(rule.getExceptionName());
            }
        }
        return result.toString();
    }

    /**
     * Rule determining whether or not a given exception (and any subclasses)
     * should cause a rollback.
     *
     * <p>Multiple such rules can be applied to determine whether a transaction
     * should commit or rollback after an exception has been thrown.
     *
     * @see NoRollbackRuleAttribute
     */
    public static class RollbackRuleAttribute implements Serializable {

        /**
         * The {@link RollbackRuleAttribute rollback rule} for
         * {@link RuntimeException RuntimeExceptions}.
         */
        public static final RollbackRuleAttribute ROLLBACK_ON_RUNTIME_EXCEPTIONS = new RollbackRuleAttribute(RuntimeException.class);


        /**
         * Could hold exception, resolving class name but would always require FQN.
         * This way does multiple string comparisons, but how often do we decide
         * whether to roll back a transaction following an exception?
         */
        private final String exceptionName;


        /**
         * Create a new instance of the {@code RollbackRuleAttribute} class.
         * <p>This is the preferred way to construct a rollback rule that matches
         * the supplied {@link Exception} class (and subclasses).
         *
         * @param clazz throwable class; must be {@link Throwable} or a subclass
         *              of {@code Throwable}
         * @throws IllegalArgumentException if the supplied {@code clazz} is
         *                                  not a {@code Throwable} type or is {@code null}
         */
        public RollbackRuleAttribute(Class<?> clazz) {
            Preconditions.checkNotNull(clazz, "'clazz' cannot be null");
            if (!Throwable.class.isAssignableFrom(clazz)) {
                throw new IllegalArgumentException(
                        "Cannot construct rollback rule from [" + clazz.getName() + "]: it's not a Throwable");
            }
            this.exceptionName = clazz.getName();
        }

        /**
         * Create a new instance of the {@code RollbackRuleAttribute} class
         * for the given {@code exceptionName}.
         * <p>This can be a substring, with no wildcard support at present. A value
         * of "ServletException" would match
         * {@code javax.servlet.ServletException} and subclasses, for example.
         * <p><b>NB:</b> Consider carefully how specific the pattern is, and
         * whether to include package information (which is not mandatory). For
         * example, "Exception" will match nearly anything, and will probably hide
         * other rules. "java.lang.Exception" would be correct if "Exception" was
         * meant to define a rule for all checked exceptions. With more unusual
         * exception names such as "BaseBusinessException" there's no need to use a
         * fully package-qualified name.
         *
         * @param exceptionName the exception name pattern; can also be a fully
         *                      package-qualified class name
         * @throws IllegalArgumentException if the supplied
         *                                  {@code exceptionName} is {@code null} or empty
         */
        public RollbackRuleAttribute(String exceptionName) {
            Preconditions.checkNotEmpty(exceptionName, "'exceptionName' cannot be null or empty");
            this.exceptionName = exceptionName;
        }


        /**
         * Return the pattern for the exception name.
         */
        public String getExceptionName() {
            return exceptionName;
        }

        /**
         * Return the depth of the superclass matching.
         * <p>{@code 0} means {@code ex} matches exactly. Returns
         * {@code -1} if there is no match. Otherwise, returns depth with the
         * lowest depth winning.
         */
        public int getDepth(Throwable ex) {
            return getDepth(ex.getClass(), 0);
        }


        private int getDepth(Class<?> exceptionClass, int depth) {
            if (exceptionClass.getName().contains(this.exceptionName)) {
                // Found it!
                return depth;
            }
            // If we've gone as far as we can go and haven't found it...
            if (exceptionClass == Throwable.class) {
                return -1;
            }
            return getDepth(exceptionClass.getSuperclass(), depth + 1);
        }


        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof RollbackRuleAttribute)) {
                return false;
            }
            RollbackRuleAttribute rhs = (RollbackRuleAttribute) other;
            return this.exceptionName.equals(rhs.exceptionName);
        }

        @Override
        public int hashCode() {
            return this.exceptionName.hashCode();
        }

        @Override
        public String toString() {
            return "RollbackRuleAttribute with pattern [" + this.exceptionName + "]";
        }

    }


    /**
     * Tag subclass of {@link RollbackRuleAttribute} that has the opposite behavior
     * to the {@code RollbackRuleAttribute} superclass.
     */
    public static class NoRollbackRuleAttribute extends RollbackRuleAttribute {

        /**
         * Create a new instance of the {@code NoRollbackRuleAttribute} class
         * for the supplied {@link Throwable} class.
         *
         * @param clazz the {@code Throwable} class
         * @see RollbackRuleAttribute#RollbackRuleAttribute(Class)
         */
        public NoRollbackRuleAttribute(Class<?> clazz) {
            super(clazz);
        }

        /**
         * Create a new instance of the {@code NoRollbackRuleAttribute} class
         * for the supplied {@code exceptionName}.
         *
         * @param exceptionName the exception name pattern
         * @see RollbackRuleAttribute#RollbackRuleAttribute(String)
         */
        public NoRollbackRuleAttribute(String exceptionName) {
            super(exceptionName);
        }

        @Override
        public String toString() {
            return "No" + super.toString();
        }

    }


    public static final List<RollbackRuleAttribute> buildRules(Class[] rollbackFor, Class[] noRollbackFor) {
        final List<RuleBasedTransactionDefinition.RollbackRuleAttribute> rules = Collects.emptyArrayList();
        Collects.forEach(noRollbackFor, new Consumer<Class>() {
            @Override
            public void accept(Class aClass) {
                rules.add(new RuleBasedTransactionDefinition.NoRollbackRuleAttribute(aClass));
            }
        });

        Collects.forEach(rollbackFor, new Consumer<Class>() {
            @Override
            public void accept(Class aClass) {
                rules.add(new RuleBasedTransactionDefinition.RollbackRuleAttribute(aClass));
            }
        });
        return rules;
    }
}

package com.jn.sqlhelper.tests;

import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.comparator.ParallelingComparator;
import com.jn.langx.util.comparator.ReverseComparator;
import com.jn.langx.util.comparator.StringComparator;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.reflect.FieldComparator;
import com.jn.sqlhelper.dialect.orderby.OrderBy;
import com.jn.sqlhelper.dialect.orderby.SymbolStyleOrderByBuilder;
import com.jn.sqlhelper.dialect.pagination.PagingRequest;
import com.jn.sqlhelper.dialect.pagination.MemoryPaginations;
import org.junit.Test;

import java.util.*;

public class MemoryPaginationsTests {

    @Test
    public void test0() {
        List<Person> persons = new LinkedList<Person>();
        Random random = new Random(10000);

        persons = new LinkedList<Person>();
        for (int i = 0; i < 100; i++) {
            Person person = new Person();
            person.setId("id_" + Math.abs(random.nextInt()));
            person.setName("name_" + Math.abs(random.nextInt()));
            person.setAge(Math.abs(random.nextInt(200)));
            persons.add(person);
        }
        PagingRequest pagingRequest = new PagingRequest();
        pagingRequest.limit(2, 10);

        SymbolStyleOrderByBuilder builder = SymbolStyleOrderByBuilder.MATH_SYMBOL_ORDER_BY_BUILDER;
        OrderBy orderBy = builder.build("+name, -age");
        System.out.println(orderBy.hashCode());
        pagingRequest.setOrderBy(orderBy);


        final List<Person> paged = MemoryPaginations.paging(persons, pagingRequest, new Predicate<Person>() {
            @Override
            public boolean test(Person person) {
                return person.getAge() > 30;
            }
        }, new Predicate<Person>() {
            @Override
            public boolean test(Person person) {
                return person.getAge() < 100;
            }
        });


        Collects.forEach(paged, new Consumer<Person>() {
            @Override
            public void accept(Person person) {
                System.out.println(person);
            }
        });
    }

    //@Test
    public void test2() {
        System.out.println("===================test2==================");
        List<Person> persons;
        Random random = new Random(10);

        persons = new LinkedList<Person>();
        for (int i = 0; i < 100; i++) {
            Person person = new Person();
            person.setId("id_" + Math.abs(random.nextInt(10)));
            person.setName("name_" + Math.abs(random.nextInt(10)));
            person.setAge(Math.abs(random.nextInt(100)));
            persons.add(person);
        }

        ParallelingComparator comparator = new ParallelingComparator();
        comparator.addComparator(new FieldComparator(Person.class, "id", null));
        comparator.addComparator(new FieldComparator(Person.class, "name", null));
        comparator.addComparator(new FieldComparator(Person.class, "age", null));
        Set<Person> pset = new TreeSet<Person>(comparator);

        pset.addAll(persons);

        Collects.forEach(pset, new Consumer<Person>() {
            @Override
            public void accept(Person person) {
                System.out.println(person);
            }
        });
    }

    //@Test
    public void test3() {
        System.out.println("===================test3==================");
        List<Person> persons;
        Random random = new Random(10);

        persons = new LinkedList<Person>();
        for (int i = 0; i < 100; i++) {
            Person person = new Person();
            person.setId("id_" + 0);
            person.setName("name_" + Math.abs(random.nextInt(10)));
            person.setAge(Math.abs(random.nextInt(100)));
            persons.add(person);
        }

        ParallelingComparator comparator = new ParallelingComparator();
        comparator.addComparator(new FieldComparator(Person.class, "id", null));
        comparator.addComparator(new FieldComparator(Person.class, "name", null));
        comparator.addComparator(new FieldComparator(Person.class, "age", null));
        Set<Person> pset = new TreeSet<Person>(comparator);

        pset.addAll(persons);

        Collects.forEach(pset, new Consumer<Person>() {
            @Override
            public void accept(Person person) {
                System.out.println(person);
            }
        });
    }

    //@Test
    public void test4() {
        System.out.println("===================test4==================");
        List<Person> persons;
        Random random = new Random(10);

        persons = new LinkedList<Person>();
        for (int i = 0; i < 100; i++) {
            Person person = new Person();
            person.setId("id_" + 0);
            person.setName("name_" + Math.abs(random.nextInt(10)));
            person.setAge(Math.abs(random.nextInt(100)));
            persons.add(person);
        }

        ParallelingComparator comparator = new ParallelingComparator();
        comparator.addComparator(new FieldComparator(Person.class, "id", null));
        comparator.addComparator(new FieldComparator(Person.class, "name", new ReverseComparator(new StringComparator())));
        comparator.addComparator(new FieldComparator(Person.class, "age", null));
        Set<Person> pset = new TreeSet<Person>(comparator);

        pset.addAll(persons);

        Collects.forEach(pset, new Consumer<Person>() {
            @Override
            public void accept(Person person) {
                System.out.println(person);
            }
        });
    }
}

REM 使用之前，先将 pom 文件中的 构建目标版本设置为 1.7

set JAVA_HOME=%JAVA_17_HOME%
set Path=%JAVA_HOME%/bin;%Path%
REM mvn clean package -DskipTests
mvn clean verify sonar:sonar -DskipTests -Dsonar.login=admin -Dsonar.password=admin123 -Dsonar.projectKey=sqlhelper -Dsonar.projectName=sqlhelper -Dsonar.host.url=http://192.168.137.133:9000
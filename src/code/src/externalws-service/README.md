# External Web Services service
External web services connectivity microservice.

### Prerequisites:
* [Java SDK 17 or later](https://www.oracle.com/java/technologies/downloads/) or [AdoptopenJDK 17 or later](https://adoptopenjdk.net/)
* [Maven 3.9.9](https://maven.apache.org/download.cgi)
* Any Java source code editor such as [Visual Studio Code](https://code.visualstudio.com/download), [NetBeans](https://netbeans.apache.org/download/index.html), [Eclipse](https://www.eclipse.org/etrice/downloads/), or [IntelliJ](https://www.jetbrains.com/es-es/idea/download/#section=windows), among others
* [MariaDb or later](https://mariadb.org/download/?t=mariadb&p=mariadb&r=12.3.2)

### Compilation:
```bash
// Enter the folder
$ cd src/externalws-service

// Compiling and clening...
$ mvn clean install -U && mvn clean compile -DskipTests && mvn clean compile

// Local environment.
$ mvn spring-boot:run -P local

// QA environment.
$ mvn spring-boot:run -P uat

// Release or Production environment.
$ mvn spring-boot:run -P release
```

### Execution
Run the following URL, depending on your runtime environment:

* [Local and UAT](http://localhost:8082/swagger-ui/index.html)

Enjoy!
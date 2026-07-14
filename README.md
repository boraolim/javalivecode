# Java Microservices Exercise
Microservices project in Java 17 with connectivity of external services and among microservices with functional programming and resilience with Spring Boot. Spring Boot version is 3.5.16.

### Prerequisites:
* [Java SDK 17 or later](https://www.oracle.com/java/technologies/downloads/) or [AdoptopenJDK 17 or later](https://adoptopenjdk.net/)
* [Maven 3.9.9](https://maven.apache.org/download.cgi)
* Any Java source code editor such as [Visual Studio Code](https://code.visualstudio.com/download), [NetBeans](https://netbeans.apache.org/download/index.html), [Eclipse](https://www.eclipse.org/etrice/downloads/), or [IntelliJ](https://www.jetbrains.com/es-es/idea/download/#section=windows), among others
* [MariaDb 10.2 or later](https://mariadb.org/download/?t=mariadb&p=mariadb&r=12.3.2)

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/4.0.7/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/4.0.7/maven-plugin/build-image.html)

### Creating environment variables with secret values
It is important to create the environment variables for the correct execution of this pair of microservices. Check the README for each project. Apply the same approach to Docker containers and secrets in AWS and Azure.

| Environment Var | Detail |
| --------- | --------- |
| ${APP_DB_HOST} | Local host name |
| ${APP_DB_PORT} | Database Port |
| ${APP_DB_USERNAME} | Database User name |
| ${APP_DB_PASSWORD} | Database Password name |
| ${INVENTORY_SERVICE_URL} | Inventory service URL |

### Database Configuration.
Once the MariaDB database server is installed, it is necessary to execute the [SQL script](src/docs/schema.sql) before starting each of the microservices.

### Compilation:
```bash
// Enter the folder
$ cd src/products-service
$ cd src/inventory-service

// Compiling and clening...
$ mvn clean install -U && mvn clean compile -DskipTests && mvn clean compile
$ mvn clean compile test-compile -U

// Testing:
$ mvn clean verify
$ mvn clean compile test-compile -U
$ mvn clean verify | tee build-output.log
$ mvn clean verify > build-output.log 2>&1

// Local environment.
$ mvn spring-boot:run -P local

// QA environment.
$ mvn spring-boot:run -P uat

// Release or Production environment.
$ mvn spring-boot:run -P release

// Package and run:
$ mvn clean package
$ java -jar target/product-service-1.0.0.jar

// Automatically update all dependencies:
$ mvn versions:use-latest-releases

// Update the Spring Boot version (if applicable):
$ mvn versions:update-parent && mvn versions:commit || mvn versions:revert
```

Enjoy!

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/4.0.6/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/4.0.6/maven-plugin/build-image.html)
* [Spring Boot Testcontainers support](https://docs.spring.io/spring-boot/4.0.6/reference/testing/testcontainers.html#testing.testcontainers)
* [Testcontainers MariaDB Module Reference Guide](https://java.testcontainers.org/modules/databases/mariadb/)
* [Spring Reactive Web](https://docs.spring.io/spring-boot/4.0.6/reference/web/reactive.html)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/4.0.6/reference/using/devtools.html)
* [Spring Data JPA](https://docs.spring.io/spring-boot/4.0.6/reference/data/sql.html#data.sql.jpa-and-spring-data)
* [Validation](https://docs.spring.io/spring-boot/4.0.6/reference/io/validation.html)
* [Spring Configuration Processor](https://docs.spring.io/spring-boot/4.0.6/specification/configuration-metadata/annotation-processor.html)
* [codecentric's Spring Boot Admin (Server)](https://codecentric.github.io/spring-boot-admin/current/#getting-started)
* [Testcontainers](https://java.testcontainers.org/)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a Reactive RESTful Web Service](https://spring.io/guides/gs/reactive-rest-service/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Validation](https://spring.io/guides/gs/validating-form-input/)
# UOCNS simulator
Java web service providing API for simulation of networks on a chip and generation of configuration xml-files.

Links:
- [MIEM UOCNS](http://miem-uocns.ru)
- [UOCNS API](http://miem-uocns.ru/swagger-ui.html)
- [JavaDoc](http://miem-uocns.ru/doc/index.html)



Dependencies:
- PostgreSQL
- Java 8 (or higher)


Used frameworks and tools:
- Spring Framework
- Apache Cayenne
- Lombok
- Swagger 2


# Steps for deployment
1. Database preparing:

   Run sql script for database mapping `src/main/resources/database.sql`

   Change credentials for jdbc connection at `src/main/resources/application.properties`

2. Run jar-file from `target/uocns-0.0.1.jar`
   
   Start java application with command ```java -jar uocns-1.0.0.jar``` or run sh-scrip -

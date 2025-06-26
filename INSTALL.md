Prerequisites

Java 17 or higher
Maven 3.6 or higher
Docker and Docker Compose
Git

Installation

1. Clone the repository

git clone https://github.com/HoldenLem/Scraping.git
cd your-repo

2. Set up the database (via Docker Compose)
The database configuration is already provided in docker-compose.yml:

Database name: scraping_job
Username: postgres
Password: postgres
Port: 5432

!!! If you need to change the data volume path, modify the volumes section:

services:
db:
...
volumes:
- ./pgdata:/var/lib/postgresql/data

./pgdata is the local folder where the PostgreSQL data will be stored.

3. Start the database container

docker-compose up -d

4. Configure application.properties

Open src/main/resources/application.properties and set the database connection parameters:

spring.datasource.url=jdbc:postgresql://localhost:5432/scraping_job
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update

5. Build the project

mvn clean install

6. Run the application

mvn spring-boot:run




# Online Movie Website

Online Movie Website is a Spring Boot-based web application that allows users to watch movies and TV shows. The application supports user authentication (form login and Google OAuth2), video uploads, email notifications.

## Features

- Stream movies and TV episodes
- User registration and login (standard and Google OAuth2)
- Email verification and notifications
- Upload and manage media content
- Responsive frontend with Thymeleaf

## Prerequisites

- Java 17 or higher
- Maven
- SQL Server or MySQL
- Gmail account for email service (optional)

The following is the complete `application.properties` configuration used in this project. You can copy and modify it to work on your device.

# Application Name
spring.application.name=OnlineMovieWeb

# Thymeleaf Configuration
spring.thymeleaf.enabled=true
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html

# === DATABASE CONFIGURATION ===

# Option 1: SQL Server (default)
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=MovieWebsite;encrypt=true;trustServerCertificate=true
spring.datasource.username=YOUR_SQLSERVER_USERNAME
spring.datasource.password=YOUR_SQLSERVER_PASSWORD
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver

# Option 2: MySQL (uncomment to use)
spring.datasource.url=jdbc:mysql://localhost:3306/MovieWebsite?useSSL=false&serverTimezone=UTC
spring.datasource.username=YOUR_MYSQL_USERNAME
spring.datasource.password=YOUR_MYSQL_PASSWORD
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
And remember to add dependency


# JPA and Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# === GOOGLE OAUTH2 LOGIN CONFIGURATION ===
spring.security.oauth2.client.registration.google.client-id=YOUR_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_CLIENT_SECRET
spring.security.oauth2.client.registration.google.scope=openid,profile,email
spring.security.oauth2.client.registration.google.redirect-uri={baseUrl}/login/oauth2/code/{registrationId}
spring.security.oauth2.client.client-name=Google

# === EMAIL CONFIGURATION ===
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=YOUR_EMAIL@gmail.com
spring.mail.password=YOUR_APP_PASSWORD
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.properties.mail.smtp.ssl.trust=smtp.gmail.com

# === FILE UPLOAD LIMITS ===
spring.servlet.multipart.max-file-size=100MB
spring.servlet.multipart.max-request-size=100MB
spring.servlet.multipart.file-size-threshold=1MB
server.tomcat.max-part-count=80
server.tomcat.max-http-form-post-size=100MB

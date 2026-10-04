# SAMS — Student Achievement Management System

A web-based application for managing, submitting, and evaluating student achievements.

## Features

- Student and evaluator login with role-based access
- Student achievement submission
- Evidence file uploads
- Evaluator scoring system with a score out of 100
- Dashboard with achievement statistics and activity charts
- Top achievement rankings
- Session-based authentication and logout
- MySQL database integration

## Technology Stack

- **Frontend:** HTML, CSS, JavaScript
- **Backend:** Java Servlets
- **Server:** Apache Tomcat 10
- **Database:** MySQL 8
- **Build Tool:** Apache Maven
- **Java:** Java 17

## Requirements

- JDK 17 or later
- Apache Maven
- Apache Tomcat 10.1+
- MySQL 8.0+

## Local Setup

1. Clone this repository.
2. Create the `sams_db` database in MySQL.
3. Set up the database tables using the SQL schema provided in this repository.
4. Configure the database connection using the `SAMS_DB_PASSWORD` environment variable.
5. Build the application:

   ```bash
   mvn clean package
   ```

6. Deploy `target/sams.war` to Apache Tomcat.
7. Start Tomcat and open:

   `http://localhost:8080/sams/`

## Security Notes

- Never commit database passwords, private credentials, or uploaded evidence.
- Configure database credentials through environment variables.
- Use secure, separate credentials for any public demonstration.

## Project Status

Developed as a student software project. Deployment instructions and the live demo link will be added when available.

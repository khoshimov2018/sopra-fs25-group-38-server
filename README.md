# StudyBuddy

**Find your ideal study partner — based on courses, goals, and learning style.**

![image](https://github.com/user-attachments/assets/aa6850c6-6670-4dd8-9db2-ce59352a3419)

*Your smart companion for collaborative learning at university.*

[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Backend](https://img.shields.io/badge/Backend-Spring--Boot-brightgreen)](https://spring.io/projects/spring-boot)
[![Build](https://img.shields.io/badge/Build-Gradle-blue)](https://gradle.org)

---

## Table of Contents

- [Introduction](#introduction)
- [Technologies Used](#technologies-used)
- [High-Level Components](#high-level-components)
- [Launch & Deployment](#launch--deployment)
- [Roadmap](#roadmap)
- [Authors and Acknowledgment](#authors-and-acknowledgment)
- [License](#license)

---

## Introduction

**StudyBuddy** is a web application designed to connect university students with compatible study partners based on shared courses, availability, and study goals. The platform features a swipe-based matching system, real-time chat, profile management, and AI-based study assistance.  
This backend service provides the RESTful API and database integration supporting all core functionalities.

---

## Technologies Used

**Backend**
- Spring Boot (Java 17)
- RESTful API
- JPA/Hibernate
- PostgreSQL

**DevOps & Tooling**
- Gradle (Build system)
- Docker (Containerization)
- SonarCloud (Code quality)

## High-Level Components

1.⁠ ⁠*Authentication*
   - Secure login and registration using token-based authentication.

2.⁠ ⁠*User Profile Management*
   - Bio, courses, study goals, availability, and knowledge levels.
   - Course selections managed through ⁠ UserCourse ⁠ and ⁠ CourseSelectionDTO ⁠.

3.⁠ ⁠*Matchmaking System*
   - Logic to match users based on shared interests and mutual likes.

4.⁠ ⁠*Chat & Interaction*
   - Basic real-time messaging infrastructure.

5.⁠ ⁠*User Moderation*
   - Blocking and reporting system for user safety.

---

## Launch & Deployment

⁠bash
cd server
./gradlew bootRun
 ⁠

Backend will start on ⁠ http://localhost:8080 ⁠.

### Environment

- ⁠Ensure PostgreSQL is running.
- ⁠Set your database credentials in ⁠ application.properties ⁠.
- ⁠Use included SQL to seed the database (if required).

### Testing

⁠bash
./gradlew test
 ⁠

---

## Roadmap

- Enable message deletion in chat system.
- ⁠Refine matching algorithm with better recommendation system.
- ⁠Enable calendar integration for availability sync.


---

## Authors and Acknowledgment

**Team – Group 38:**

- Kai Koepchen – 24-738-189  
- Daria Kazmina – 22-898-118  
- Khoshimov Rakhmatillokhon – 23-060-361  
- Ajeong Shin – 24-742-405  
- Zhidian Huang – 24-745-655  
- Yanyang Luo – 24-742-165

Special thanks to:  
- SoPra Teaching Team  
- Course mates for feedback and user testing

---

## License

This project is licensed under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0).  
© 2020 University of Zurich

# StudyBuddy – Backend

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
- [Core Features](#core-features)
- [Launch & Deployment](#launch--deployment)
- [Roadmap](#roadmap)
- [Authors and Acknowledgment](#authors-and-acknowledgment)
- [License](#license)

---

## Introduction

**StudyBuddy** is a web application designed to connect university students with compatible study partners based on shared courses, availability, and study goals.

This backend provides the RESTful API for [StudyBuddy frontend](https://github.com/khoshimov2018/sopra-fs25-group-38-client), including user authentication, matching logic, chat handling, moderation and AI-based study assistance. 

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

## Core Features

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

### Local Setup

```bash
cd server
./gradlew bootRun
```

Visit: [http://localhost:8080](http://localhost:8080)

---

### Environment Configuration

- Ensure PostgreSQL is running
- Set credentials in `application.properties`
- Use provided SQL for test data if needed

---

### Testing

```bash
./gradlew test
```

---

## Roadmap

- Enable message deletion in chat system.
- ⁠Refine matchmaking logic with improved recommendation techniques.
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

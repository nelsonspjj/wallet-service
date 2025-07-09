# Wallet Service

## Overview
This microservice provides wallet management for users, allowing operations such as creating wallets, depositing, withdrawing, and transferring funds. It also includes user authentication and authorization.

## Technologies Used
- Java 21
- Spring Boot
- MongoDB
- Redis
- Docker
- Kafka
- Lombok
- Spring Security
- Swagger (springdoc-openapi)
- JWT

## Setup Instructions
1. Clone the repository.
2. Navigate to the project directory.
3. Build the application:
   ```bash
   mvn clean install
   ```
4. Run the application using Docker:
   ```bash
   docker-compose up --build
   ```
5. Access the API at http://localhost:8080/api/wallets
6. Access the Redis at http://localhost:8081/
7. Access the Kafka at http://localhost:19000/
8. Access the Swagger UI at http://localhost:8080/swagger-ui.html

## API Documentation
The API documentation is available at the `/swagger-ui.html` endpoint.

## API Endpoints
**Authentication Endpoints**
- POST `/api/auth/register`: Register a new user.
- POST `/api/auth/login`: Log in a user.

**Wallet Endpoints**
- POST `/api/wallets/create`: Create a new wallet.
- GET `/api/wallets/balance`: Get the current wallet balance.
- GET `/api/wallets/balance/history`: Get historical wallet balance.
- POST `/api/wallets/{userId}/deposit`: Deposit money into a wallet.
- POST `/api/wallets/{userId}/withdraw`: Withdraw money from a wallet.
- POST `/api/wallets/transfer`: Transfer money between wallets.

## License
This project is licensed under the MIT License.
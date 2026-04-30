# Cloud-Native E-Commerce Microservices Platform

A fully containerized, distributed e-commerce backend architecture built with **Java 21** and **Spring Boot 3**. This
platform demonstrates production-level system design, featuring centralized routing, synchronous and asynchronous
communication, distributed observability, and fault tolerance.

---

## Architecture Overview

![Cloud-Native E-Commerce Architecture Diagram](./architecture-diagram.png)
---

## Key Technical Features

* **Asynchronous Event-Driven Messaging:** Utilizes **Apache Kafka** to decouple the Order and Payment domains. Orders
  are placed immediately, while payment processing is handled in the background, drastically increasing system fault
  tolerance.
* **Distributed Tracing & Observability:** Integrated **Micrometer** and **Zipkin** to assign unique Trace IDs to every
  HTTP request and Kafka message. This allows full visual tracing of requests as they propagate through the distributed
  network.
* **Fault Tolerance:** Implemented **Resilience4j** Circuit Breakers on internal microservice calls to prevent cascading
  failures when downstream dependencies become unavailable.
* **Cloud-Native Deployment:** Fully containerized architecture using a master **Docker Compose** orchestrator.

---

## Tech Stack

* **Language:** Java 21
* **Framework:** Spring Boot 3.5.13, Spring WebFlux
* **Infrastructure:** Spring Cloud (Eureka, Config Server, API Gateway, OpenFeign)
* **Messaging Broker:** Apache Kafka
* **Database:** MySQL / Spring Data JPA / Hibernate
* **Security:** Spring Security, BCrypt, JWT
* **Observability:** Micrometer, Zipkin
* **Containerization:** Docker, Docker Compose

---

## Quick Start (Local Run)

### Prerequisites

* Docker & Docker Compose installed
* Ports `8080`, `8761`, `8888`, `9092`, `9411` available

### Run the System

```bash
git clone https://github.com/vanshbatham/ecommerce-microservices-platform.git
cd ecommerce-microservices-platform
docker compose up -d --build
```

Check Eureka:

```
http://localhost:8761
```

Stop:

```bash
docker compose down
```

---

## API Testing Flow (End-to-End)

### 1. Register User

POST `/api/v1/users/register`

```json
{
  "firstName": "Ezio",
  "lastName": "Auditore",
  "email": "ezioauditore@gmail.com",
  "phoneNumber": "+91 9999999999",
  "password": "ezio123"
}
```

---

### 2. Login → Get JWT

POST `/api/v1/users/login`

```json
{
  "email": "ezioauditore@gmail.com",
  "password": "ezio123"
}
```

---

### 3. Create Product

POST `/api/v1/products`

```json
{
  "sku": "PHONE-IPHONE-17",
  "name": "Apple IPhone 17 Pro Max",
  "price": 101.00,
  "stockQuantity": 20
}
```

---

### 4. Place Order (Core Flow)

POST `/api/v1/orders`

Header:

```
Authorization: Bearer <JWT>
```

```json
{
  "userId": 1,
  "orderLineItemsDtoList": [
    {
      "sku": "PHONE-IPHONE-17",
      "price": 1.00,
      "quantity": 1
    }
  ]
}
```

---

### 5. Verify Async Processing

* The Order Service instantly returned a response to Postman, but it also fired a message to Kafka. Check the Payment
  Service logs to confirm the background processor caught the event:

```bash
docker logs payment-service
```

* **Expected Log Output**: Consumed OrderCreatedEvent for Order ID: ... Processing Payment...

---

### 6. Distributed Observability (Zipkin)

* Visualize the entire lifecycle of the HTTP requests and Kafka messages.

* Open your browser and access:

```
http://localhost:9411
```

* Click Run Query.

* Select the most recent trace to view the waterfall latency graph showing the exact milliseconds spent inside the API
  Gateway, Order Service, Feign Client, Kafka broker, and Payment Service.

---

*Developed by Vansh Batham*

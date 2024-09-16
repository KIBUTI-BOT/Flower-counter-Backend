
# Flower Counter Backend API

This is the backend API for a flower-counting application. The API processes batches of flower images, counts the number of flowers using a pre-trained model hosted on Hugging Face, and exposes endpoints for uploading images and retrieving the count.

---

## Table of Contents
- [Project Overview](#project-overview)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [API Endpoints](#api-endpoints)
- [Running the Application](#running-the-application)
- [Additional Information](#additional-information)

---

## Project Overview
This backend system:
- Processes flower images using a pre-trained model from Hugging Face.
- Allows batch processing of images and returns the count of flowers detected.
- Provides REST API endpoints for uploading images and retrieving results.

---

## Prerequisites
Make sure the following are installed on your machine:
1. Java 17 or higher
2. Maven
3. PostgreSQL
4. Git

---

## Installation

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/KIBUTI-SOFTWARE/flower-counter-Backend.git
   cd flower-counter-backend
   ```

2. **Configure Database:**
   - Make sure PostgreSQL is running.
   - Update the `spring.datasource.password` in `application.properties` with your PostgreSQL password.
   - You can change the database name and port if needed by uncommenting and updating the `spring.datasource.url`.

3. **Install Dependencies:**
   Run Maven to install all dependencies.
   ```bash
   mvn clean install
   ```

4. **Create Image Upload Directory:**
   - Create a directory on your local machine where uploaded images will be stored.
   - Update `file.upload-dir` in the `application.properties` file with the path to this directory.

---

## Configuration

### Application Properties
The key configuration options in `application.properties`:
- **Database Configuration**: Configured to connect to a PostgreSQL database.
  ```properties
  spring.datasource.url=jdbc:postgresql://localhost:5000/flowercounter_db
  spring.datasource.username=postgres
  spring.datasource.password=YOUR_DATABASE_PASSWORD
  ```

- **Image Upload Directory**: The directory to store uploaded images.
  ```properties
  file.upload-dir=YOUR_LOCAL_DIRECTORY_PATH
  ```

- **Hugging Face Model URL**: The URL of the pre-trained flower-count model.
  ```properties
  api.model-url=https://api-inference.huggingface.co/models/smutuvi/flower_count_model
  ```

- **Multipart File Upload Limits**: You can change the max file upload size here.
  ```properties
  spring.servlet.multipart.max-file-size=10MB
  spring.servlet.multipart.max-request-size=10MB
  ```

---

## API Endpoints

### 1. **Upload Image Batch (POST)**
   Endpoint to upload images for flower counting.

   - **URL**: `/api/images/upload`
   - **Method**: `POST`
   - **Headers**: 
     - `Content-Type: multipart/form-data`
   - **Request**: Multipart file upload of flower images.
   - **Example Curl**:
     ```bash
     curl --request POST \
       --url http://localhost:8080/api/images/upload \
       --header 'Content-Type: multipart/form-data' \
       --form 'files=@C:\path\to\your\image.jpg'
     ```

   - **Response**: 
     Returns a JSON object with the results of the flower count.

### 2. **Get All Processed Images (GET)**
   Retrieve all uploaded images and their flower counts.

   - **URL**: `/api/images/all`
   - **Method**: `GET`
   - **Example**:
     ```bash
     curl --request GET \
       --url http://localhost:8080/api/images/all
     ```

---

## Running the Application

1. **Run the Application:**
   Start the Spring Boot application using Maven.
   ```bash
   mvn spring-boot:run
   ```

2. **Access the API:**
   By default, the application runs on `http://localhost:8080`. You can access the API endpoints listed above.

---

## Additional Information

- **PostgreSQL Setup**:
   Make sure the `flowercounter_db` database is created in PostgreSQL. If not, you can create it by running:
   ```sql
   CREATE DATABASE flowercounter_db;
   ```

- **Logs**: 
   Logs can be checked in the console for more detailed information while running the API.

- **Error Handling**: 
   Ensure that proper error handling is in place for invalid image formats, missing files, etc.

- **Batch Processing**:
   The backend supports asynchronous batch image processing for improved performance.




---


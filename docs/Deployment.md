# Deployment Steps

This document explains how to deploy the artifacts produced by the CI workflows for both the backend and frontend.

## Backend JAR (.jar)
1. Download the backend JAR artifact from the CI run. The filename includes the project version and build tag.
2. Copy the JAR to the target server where Java is installed.
3. Ensure Java 21 or compatible is available on the server.
4. Run the application:
   ```bash
   java -jar <artifact>.jar
   ```
5. Set any required environment variables (e.g., database credentials, JWT secret) before launching.

## Frontend Build (.zip)
1. Download the frontend ZIP artifact from the CI run.
2. Unzip the archive to the directory served by your web server:
   ```bash
   unzip <artifact>.zip -d /var/www/app
   ```
3. Serve the extracted `dist/` directory using Nginx, Apache, or another static file host.

## Backend Docker Image (.tar)
1. Download the backend Docker image tarball from the CI run.
2. Load the image into your local Docker registry:
   ```bash
   docker load -i <artifact>.tar
   ```
3. Run the container, mapping host port 8080 to the app's internal port 9000 and providing required environment variables:
   ```bash
   docker run -d -p 8080:9000 \
     -e JWT_SECRET=<jwt-secret> \
     -e GOOGLE_CLIENT_ID=<client-id> \
     -e GOOGLE_CLIENT_SECRET=<client-secret> \
     <image-name>
   ```

## Frontend Docker Image (.tar)
1. Download the frontend Docker image tarball from the CI run.
2. Load the image into Docker:
   ```bash
   docker load -i <artifact>.tar
   ```
3. Run the container, serving the app on port 80:
   ```bash
   docker run -d -p 80:80 <image-name>
   ```

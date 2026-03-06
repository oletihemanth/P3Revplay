# 1. Start with a brand new box that has Java 17 already installed!
FROM eclipse-temurin:17-jdk-alpine

# 2. Create a folder inside the box called /app
WORKDIR /app

# 3. Copy the .jar file we just built into the box and rename it to app.jar
COPY target/*.jar app.jar

# 4. Tell the box to run the Java app when it turns on!
ENTRYPOINT ["java", "-jar", "app.jar"]
FROM openjdk:22-jdk
ADD target/ToDoApp.jar ToDoApp.jar
ENTRYPOINT ["java", "-jar", "/ToDoApp.jar"]
EXPOSE 8081
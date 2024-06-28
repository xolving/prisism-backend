FROM azul/zulu-openjdk:21-latest
ARG JAR_FILE=/build/libs/prisism-0.0.1-SNAPSHOT.jar
COPY /${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]

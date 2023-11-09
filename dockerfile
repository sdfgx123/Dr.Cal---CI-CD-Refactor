FROM openjdk:11-jdk
LABEL maintainer="paul245"
ARG JAR_FILE=build/libs/mini3-server-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} drcal-was.jar
ENTRYPOINT ["java","-jar","drcal-was.jar"]

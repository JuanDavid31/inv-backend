FROM openjdk:8
COPY . /usr/src/invapp
WORKDIR /usr/src/invapp

EXPOSE 8080

#RUN ["java", "--version"]
CMD ["java", "-jar", "target/com-unibague-inv-1.0-SNAPSHOT.jar", "server", "conf.yml"]
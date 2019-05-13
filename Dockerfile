FROM openjdk:8
COPY . /usr/src/invapp
WORKDIR /usr/src/invapp

ARG ADMIN_EMAIL_ENV
ARG ADMIN_PASS_ENV

ENV ADMIN_EMAIL=$ADMIN_EMAIL_ENV
ENV ADMIN_PASS=$ADMIN_PASS_ENV

EXPOSE 8080
EXPOSE 8000
EXPOSE 49401
EXPOSE 9999

#RUN ["java", "-version"]
CMD ["java", "-jar", "target/com-unibague-inv-1.0-SNAPSHOT.jar", "server", "conf.yml"]
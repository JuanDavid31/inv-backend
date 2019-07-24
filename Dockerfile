FROM openjdk:8

WORKDIR /usr/src/invapp
COPY . /usr/src/invapp

ARG ADMIN_EMAIL_ENV
ARG ADMIN_PASS_ENV
ARG JWT_KEY_ENV

ENV ADMIN_EMAIL=$ADMIN_EMAIL_ENV
ENV ADMIN_PASS=$ADMIN_PASS_ENV
ENV JWT_KEY=$JWT_KEY_ENV

EXPOSE 8080
EXPOSE 5005

#RUN ["java", "-version"]
CMD ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005", "-jar", "target/com-unibague-inv-1.0-SNAPSHOT.jar", "server", "prod-conf.yml"]
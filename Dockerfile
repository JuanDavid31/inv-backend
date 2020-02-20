FROM openjdk:8

WORKDIR /usr/src/invapp
COPY . /usr/src/invapp

ARG ADMIN_EMAIL_ENV
ARG ADMIN_PASS_ENV
ARG JWT_KEY_ENV
ARG NEVER_BOUNCE_API_KEY_ENV
ARG AWS_ACCESS_KEY_ID_ENV
ARG AWS_SECRET_ACCESS_KEY_ENV
ARG POSTGRES_DB_ENV
ARG POSTGRES_PASSWORD_ENV

ENV ADMIN_EMAIL=$ADMIN_EMAIL_ENV
ENV ADMIN_PASS=$ADMIN_PASS_ENV
ENV POSTGRES_DB=$POSTGRES_DB_ENV
ENV POSTGRES_PASSWORD=$POSTGRES_PASSWORD_ENV
ENV JWT_KEY=$JWT_KEY_ENV
ENV NEVER_BOUNCE_API_KEY=$NEVER_BOUNCE_API_KEY_ENV
ENV AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID_ENV
ENV AWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY_ENV

EXPOSE 8080
EXPOSE 5005

#RUN ["java", "-version"]
CMD ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005", "-jar", "target/com-unibague-inv-1.0-SNAPSHOT.jar", "server", "prod-conf.yml"]
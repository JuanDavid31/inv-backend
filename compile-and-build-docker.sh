#!/bin/bash
mvn clean package
docker-compose down
docker-compose build \
--build-arg POSTGRES_DB_ENV=$POSTGRES_DB \
--build-arg POSTGRES_PASSWORD_ENV=$POSTGRES_PASSWORD \
--build-arg ADMIN_EMAIL_ENV=$ADMIN_EMAIL \
--build-arg ADMIN_PASS_ENV=$ADMIN_PASS \
--build-arg JWT_KEY_ENV="$JWT_KEY" \
--build-arg AWS_ACCESS_KEY_ID_ENV=$AWS_ACCESS_KEY_ID \
--build-arg AWS_SECRET_ACCESS_KEY_ENV=$AWS_SECRET_ACCESS_KEY
docker-compose up
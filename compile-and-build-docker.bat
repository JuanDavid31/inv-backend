call mvn clean package
call docker-compose down
call docker-compose build ^
--build-arg ADMIN_EMAIL_ENV=%ADMIN_EMAIL% ^
--build-arg ADMIN_PASS_ENV=%ADMIN_PASS% ^
--build-arg POSTGRES_DB_ENV=%POSTGRES_DB% ^
--build-arg POSTGRES_PASSWORD_ENV=%POSTGRES_PASSWORD% ^
--build-arg JWT_KEY_ENV=%JWT_KEY% ^
--build-arg NEVER_BOUNCE_API_KEY_ENV=%NEVER_BOUNCE_API_KEY% ^
--build-arg AWS_ACCESS_KEY_ID_ENV=%AWS_ACCESS_KEY_ID% ^
--build-arg AWS_SECRET_ACCESS_KEY_ENV=%AWS_SECRET_ACCESS_KEY%
call docker-compose up
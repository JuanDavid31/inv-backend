call mvn clean package
call docker-compose down
call docker-compose build ^
--build-arg ADMIN_EMAIL_ENV=%ADMIN_EMAIL% ^
--build-arg ADMIN_PASS_ENV=%ADMIN_PASS%
call docker-compose up
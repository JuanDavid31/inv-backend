FROM postgres

COPY dockerdb.sql /docker-entrypoint-initdb.d/

EXPOSE 5432

ENV POSTGRES_PASSWORD 1234
ENV POSTGRES_DB inv

VOLUME pgdevdata:/var/lib/postgresql/data
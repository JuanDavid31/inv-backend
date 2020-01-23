DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO public;

CREATE TABLE PERSONA
(
    a_email         varchar(45)  not null,
    d_nombres       varchar(30)  not null,
    d_apellidos     varchar(30)  not null,
    a_pass_hasheado varchar(150) not null
);

alter table PERSONA
    ADD CONSTRAINT PK_PERSONA primary key (a_email);

CREATE TABLE PROBLEMATICA
(
    c_id             SERIAL       not null,
    a_nombre         varchar(60)  not null,
    a_descripcion    varchar(500) not null,
    f_fecha_creacion timestamp    not null,
    c_fase           integer      not null
);

alter table PROBLEMATICA
    add constraint PK_PROBLEMATICA primary key (c_id);
alter table PROBLEMATICA
    add constraint CK_PROBLEMATICA CHECK ( c_fase >= 0 AND c_fase <= 5 );

CREATE TABLE PERSONA_PROBLEMATICA
(
    a_id              varchar(40) not null,
    a_email           varchar(45) not null,
    c_id_problematica int         not null,
    b_interventor     boolean     not null
);

ALTER TABLE PERSONA_PROBLEMATICA
    add constraint PK_PERSONA_PROBLEMATICA primary key (a_id);
ALTER TABLE PERSONA_PROBLEMATICA
    add constraint FK_PERS_PROB_PERS foreign key (a_email) REFERENCES PERSONA (a_email);
ALTER TABLE PERSONA_PROBLEMATICA
    add constraint FK_PERS_PROB_PROB foreign key (c_id_problematica) REFERENCES PROBLEMATICA (c_id);

CREATE TABLE INVITACION
(
    a_email_remitente    varchar(45)  not null,
    a_email_destinatario varchar(45)  not null,
    c_id_problematica    int          not null,
    a_id                 varchar(100) not null,
    b_vigente            boolean      not null,
    b_para_interventor   boolean      not null,
    b_rechazada          boolean      not null
);

ALTER TABLE INVITACION
    add constraint PK_INVITACION primary key (a_id);
ALTER TABLE INVITACION
    add constraint FK_REMITENTE foreign key (a_email_remitente) REFERENCES PERSONA (a_email);
ALTER TABLE INVITACION
    add constraint FK_DESTINATARIO foreign key (a_email_destinatario) REFERENCES PERSONA (a_email);
ALTER TABLE INVITACION
    add constraint FK_PROBLEMATICA foreign key (c_id_problematica) REFERENCES PROBLEMATICA (c_id);

CREATE TABLE GRUPO
(
    c_id              serial      not null,
    c_id_problematica int         not null,
    c_id_padre        int,
    d_nombre          varchar(30) not null
);

ALTER TABLE GRUPO
    ADD CONSTRAINT PK_GRUPO primary key (c_id);
ALTER TABLE GRUPO
    ADD CONSTRAINT FK_GRUPO_PROBLEMATICA foreign key (c_id_problematica) REFERENCES PROBLEMATICA (c_id);
ALTER TABLE GRUPO
    ADD CONSTRAINT FK_GRUPO_PADRE foreign key (c_id_padre) REFERENCES GRUPO (c_id);

CREATE TABLE NODO
(
    c_id           serial       not null,
    a_nombre       varchar(20) not null,
    a_id_pers_prob varchar(50)  not null,
    a_url_foto     text,
    c_id_padre     int,
    c_id_grupo     int
);

ALTER TABLE NODO
    ADD CONSTRAINT PK_NODO primary key (c_id);
ALTER TABLE NODO
    add constraint FK_NODO_PERS_PROB foreign key (a_id_pers_prob) REFERENCES PERSONA_PROBLEMATICA (a_id);
ALTER TABLE NODO
    ADD CONSTRAINT FK_NODO_PADRE foreign key (c_id_padre) REFERENCES NODO (c_id);
ALTER TABLE NODO
    ADD CONSTRAINT FK_NODO_GRUPO foreign key (c_id_grupo) REFERENCES GRUPO (c_id);

CREATE TABLE REACCION
(
    c_id           serial      not null,
    c_valor        int         not null,
    c_id_grupo     int         not null,
    a_id_pers_prob varchar(50) not null
);

ALTER TABLE REACCION
    ADD CONSTRAINT PK_REACCION primary key (c_id);
ALTER TABLE REACCION
    ADD CONSTRAINT CK_REACCION CHECK ( -1 <= c_valor AND c_valor <= 1 );
ALTER TABLE REACCION
    ADD CONSTRAINT FK_REACCION_GRUPO foreign key (c_id_grupo) REFERENCES GRUPO (c_id);
ALTER TABLE REACCION
    ADD CONSTRAINT FK_REACCION_PERS_PROB foreign key (a_id_pers_prob) REFERENCES PERSONA_PROBLEMATICA (a_id);
--alter table reaccion
--    Add constraint UK_REACCION UNIQUE (a_id_pers_prob); --Solo si quisiera una relación 1 a 1.

CREATE TABLE ESCRITO
(
    c_id           serial,
    a_nombre       varchar(20) not null,
    a_descripcion  varchar(500) not null,
    c_id_grupo     int not null,
    a_id_pers_prob varchar(50) not null
);

ALTER TABLE ESCRITO
    ADD CONSTRAINT PK_ESCRITO primary key (c_id);
ALTER TABLE ESCRITO
    ADD CONSTRAINT FK_ESCRITO_GRUPO foreign key (c_id_grupo) REFERENCES GRUPO (c_id);
ALTER TABLE ESCRITO
    ADD CONSTRAINT FK_ESCRITO_PERS_PROB foreign key (a_id_pers_prob) REFERENCES PERSONA_PROBLEMATICA (a_id);
ALTER TABLE ESCRITO
    ADD CONSTRAINT UK_ESCRITO UNIQUE(a_id_pers_prob, c_id_grupo);

--Creación de vista para facilitar Queries.
create view vista_conteo_reacciones as
(
select
	c_id_grupo,
	count(case when c_valor = -1 then c_valor end) as "negativa",
	count(case when c_valor = 0 then c_valor end)  as "neutra",
	count(case when c_valor = 1 then c_valor end)  as "positiva"
    from reaccion group by c_id_grupo
 );



--declarar la función que va a eliminar todas los registros de todas las tablas
--Usar la función -> SELECT truncate_tables('postgres');
CREATE OR REPLACE FUNCTION truncate_tables(username IN VARCHAR) RETURNS void AS
$$
DECLARE
    statements CURSOR FOR
        SELECT tablename
        FROM pg_tables
        WHERE tableowner = username
          AND schemaname = 'public';
BEGIN
    FOR stmt IN statements
        LOOP
            EXECUTE 'TRUNCATE TABLE ' || quote_ident(stmt.tablename) || ' CASCADE;';
        END LOOP;
    ALTER SEQUENCE problematica_c_id_seq RESTART WITH 1;
    ALTER SEQUENCE grupo_c_id_seq RESTART WITH 1;
    ALTER SEQUENCE nodo_c_id_seq RESTART WITH 1;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION agregarDummyData() RETURNS Void AS
$$
BEGIN
    INSERT INTO PERSONA(a_email, d_nombres, d_apellidos, a_pass_hasheado) VALUES ('juan1@.com', 'Juan1', 'Piza', '1234');
    INSERT INTO PERSONA(a_email, d_nombres, d_apellidos, a_pass_hasheado) VALUES ('juan2@.com', 'Juan2', 'Piza', '1234');
    INSERT INTO PERSONA(a_email, d_nombres, d_apellidos, a_pass_hasheado) VALUES ('juan3@.com', 'Juan3', 'Piza', '1234');
    INSERT INTO PERSONA(a_email, d_nombres, d_apellidos, a_pass_hasheado) VALUES ('juan4@.com', 'Juan4', 'Piza', '1234');
    INSERT INTO PERSONA(a_email, d_nombres, d_apellidos, a_pass_hasheado) VALUES ('juan5@.com', 'Juan5', 'Piza', '1234');
    INSERT INTO PERSONA(a_email, d_nombres, d_apellidos, a_pass_hasheado) VALUES ('juan6@.com', 'Juan6', 'Piza', '1234');
    INSERT INTO PERSONA(a_email, d_nombres, d_apellidos, a_pass_hasheado) VALUES ('juan7@.com', 'Juan7', 'Piza', '1234');
    INSERT INTO PERSONA(a_email, d_nombres, d_apellidos, a_pass_hasheado) VALUES ('juan8@.com', 'Juan8', 'Piza', '1234');
    INSERT INTO PERSONA(a_email, d_nombres, d_apellidos, a_pass_hasheado) VALUES ('juan9@.com', 'Juan9', 'Piza', '1234');
    INSERT INTO PERSONA(a_email, d_nombres, d_apellidos, a_pass_hasheado) VALUES ('juan10@.com', 'Juan10', 'Piza', '1234');

    INSERT INTO PERSONA(a_email, d_nombres, d_apellidos, a_pass_hasheado) VALUES ('david1@.com', 'David1', 'Piza', '1234');
    INSERT INTO PERSONA(a_email, d_nombres, d_apellidos, a_pass_hasheado) VALUES ('david2@.com', 'David2', 'Piza', '1234');
    INSERT INTO PERSONA(a_email, d_nombres, d_apellidos, a_pass_hasheado) VALUES ('david3@.com', 'David3', 'Piza', '1234');
    INSERT INTO PERSONA(a_email, d_nombres, d_apellidos, a_pass_hasheado) VALUES ('david4@.com', 'David4', 'Piza', '1234');
    INSERT INTO PERSONA(a_email, d_nombres, d_apellidos, a_pass_hasheado) VALUES ('david5@.com', 'David5', 'Piza', '1234');
    INSERT INTO PERSONA(a_email, d_nombres, d_apellidos, a_pass_hasheado) VALUES ('david6@.com', 'David6', 'Piza', '1234');
    INSERT INTO PERSONA(a_email, d_nombres, d_apellidos, a_pass_hasheado) VALUES ('david7@.com', 'David7', 'Piza', '1234');
    INSERT INTO PERSONA(a_email, d_nombres, d_apellidos, a_pass_hasheado) VALUES ('david8@.com', 'David8', 'Piza', '1234');
    INSERT INTO PERSONA(a_email, d_nombres, d_apellidos, a_pass_hasheado) VALUES ('david9@.com', 'David9', 'Piza', '1234');
    INSERT INTO PERSONA(a_email, d_nombres, d_apellidos, a_pass_hasheado) VALUES ('david10@.com', 'David10', 'Piza', '1234');

    INSERT INTO PROBLEMATICA(a_nombre, a_descripcion, f_fecha_creacion, c_fase)
    VALUES ('Problematica 1', 'Descripcion1', now(), 0);
    INSERT INTO PROBLEMATICA(a_nombre, a_descripcion, f_fecha_creacion, c_fase)
    VALUES ('Problematica 2', 'Descripcion2', now(), 0);

    INSERT INTO PERSONA_PROBLEMATICA(a_id, a_email, c_id_problematica, b_interventor)
    VALUES ('juan1@.com1', 'juan1@.com', 1, true);
    INSERT INTO PERSONA_PROBLEMATICA(a_id, a_email, c_id_problematica, b_interventor)
    VALUES ('juan2@.com1', 'juan2@.com', 1, true);
    INSERT INTO PERSONA_PROBLEMATICA(a_id, a_email, c_id_problematica, b_interventor)
    VALUES ('juan3@.com1', 'juan3@.com', 1, false);
    INSERT INTO PERSONA_PROBLEMATICA(a_id, a_email, c_id_problematica, b_interventor)
    VALUES ('david1@.com1', 'david1@.com', 1, true);

    INSERT INTO PERSONA_PROBLEMATICA(a_id, a_email, c_id_problematica, b_interventor)
    VALUES ('juan4@.com1', 'juan4@.com', 1, false);
    INSERT INTO PERSONA_PROBLEMATICA(a_id, a_email, c_id_problematica, b_interventor)
    VALUES ('juan5@.com1', 'juan5@.com', 1, false);
    INSERT INTO PERSONA_PROBLEMATICA(a_id, a_email, c_id_problematica, b_interventor)
    VALUES ('juan6@.com1', 'juan6@.com', 1, false);
    INSERT INTO PERSONA_PROBLEMATICA(a_id, a_email, c_id_problematica, b_interventor)
    VALUES ('juan7@.com1', 'juan7@.com', 1, false);
    INSERT INTO PERSONA_PROBLEMATICA(a_id, a_email, c_id_problematica, b_interventor)
    VALUES ('juan8@.com1', 'juan8@.com', 1, false);
    INSERT INTO PERSONA_PROBLEMATICA(a_id, a_email, c_id_problematica, b_interventor)
    VALUES ('juan9@.com1', 'juan9@.com', 1, false);
    INSERT INTO PERSONA_PROBLEMATICA(a_id, a_email, c_id_problematica, b_interventor)
    VALUES ('juan10@.com1', 'juan10@.com', 1, false);

    INSERT INTO PERSONA_PROBLEMATICA(a_id, a_email, c_id_problematica, b_interventor)
    VALUES ('david1@.com2', 'david1@.com', 2, true);
    INSERT INTO PERSONA_PROBLEMATICA(a_id, a_email, c_id_problematica, b_interventor)
    VALUES ('david2@.com2', 'david2@.com', 2, true);
    INSERT INTO PERSONA_PROBLEMATICA(a_id, a_email, c_id_problematica, b_interventor)
    VALUES ('david3@.com2', 'david3@.com', 2, false);
    INSERT INTO PERSONA_PROBLEMATICA(a_id, a_email, c_id_problematica, b_interventor)
    VALUES ('juan1@.com2', 'juan1@.com', 2, true);

    INSERT INTO PERSONA_PROBLEMATICA(a_id, a_email, c_id_problematica, b_interventor)
    VALUES ('david4@.com2', 'david4@.com', 2, false);
    INSERT INTO PERSONA_PROBLEMATICA(a_id, a_email, c_id_problematica, b_interventor)
    VALUES ('david5@.com2', 'david5@.com', 2, false);
    INSERT INTO PERSONA_PROBLEMATICA(a_id, a_email, c_id_problematica, b_interventor)
    VALUES ('david6@.com2', 'david6@.com', 2, false);
    INSERT INTO PERSONA_PROBLEMATICA(a_id, a_email, c_id_problematica, b_interventor)
    VALUES ('david7@.com2', 'david7@.com', 2, false);
    INSERT INTO PERSONA_PROBLEMATICA(a_id, a_email, c_id_problematica, b_interventor)
    VALUES ('david8@.com2', 'david8@.com', 2, false);
    INSERT INTO PERSONA_PROBLEMATICA(a_id, a_email, c_id_problematica, b_interventor)
    VALUES ('david9@.com2', 'david9@.com', 2, false);
    INSERT INTO PERSONA_PROBLEMATICA(a_id, a_email, c_id_problematica, b_interventor)
    VALUES ('david10@.com2', 'david10@.com', 2, false);


    INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente,
                           b_para_interventor, b_rechazada)
    VALUES ('juan1@.com', 'david2@.com', 1, '1juan1@.comdavid2@.com', true, false, false);
    INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente,
                           b_para_interventor, b_rechazada)
    VALUES ('juan1@.com', 'david3@.com', 1, '1juan1@.comdavid3@.com', true, false, false);
    INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente,
                           b_para_interventor, b_rechazada)
    VALUES ('juan1@.com', 'david4@.com', 1, '1juan1@.comdavid4@.com', true, false, false);
    INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente,
                           b_para_interventor, b_rechazada)
    VALUES ('juan1@.com', 'david5@.com', 1, '1juan1@.comdavid5@.com', true, false, false);
    INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente,
                           b_para_interventor, b_rechazada)
    VALUES ('juan1@.com', 'david6@.com', 1, '1juan1@.comdavid6@.com', true, false, false);
    INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente,
                           b_para_interventor, b_rechazada)
    VALUES ('juan1@.com', 'david7@.com', 1, '1juan1@.comdavid7@.com', true, false, false);
    INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente,
                           b_para_interventor, b_rechazada)
    VALUES ('juan1@.com', 'david8@.com', 1, '1juan1@.comdavid8@.com', true, false, false);
    INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente,
                           b_para_interventor, b_rechazada)
    VALUES ('juan1@.com', 'david9@.com', 1, '1juan1@.comdavid9@.com', true, false, false);
    INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente,
                           b_para_interventor, b_rechazada)
    VALUES ('juan1@.com', 'david10@.com', 1, '1juan1@.comdavid10@.com', true, false, false);

    INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente,
                           b_para_interventor, b_rechazada)
    VALUES ('david1@.com', 'david2@.com', 1, '1david1@.comdavid2@.com', true, false, false);
    INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente,
                           b_para_interventor, b_rechazada)
    VALUES ('david1@.com', 'david3@.com', 1, '1david1@.comdavid3@.com', true, false, false);
    INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente,
                           b_para_interventor, b_rechazada)
    VALUES ('david1@.com', 'david4@.com', 1, '1david1@.comdavid4@.com', true, false, false);
    INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente,
                           b_para_interventor, b_rechazada)
    VALUES ('david1@.com', 'david5@.com', 1, '1david1@.comdavid5@.com', true, false, false);
    INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente,
                           b_para_interventor, b_rechazada)
    VALUES ('david1@.com', 'david6@.com', 1, '1david1@.comdavid6@.com', true, false, false);
    INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente,
                           b_para_interventor, b_rechazada)
    VALUES ('david1@.com', 'david7@.com', 1, '1david1@.comdavid7@.com', true, false, false);
    INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente,
                           b_para_interventor, b_rechazada)
    VALUES ('david1@.com', 'david8@.com', 1, '1david1@.comdavid8@.com', true, false, false);
    INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente,
                           b_para_interventor, b_rechazada)
    VALUES ('david1@.com', 'david9@.com', 1, '1david1@.comdavid9@.com', true, false, false);
    INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente,
                           b_para_interventor, b_rechazada)
    VALUES ('david1@.com', 'david10@.com', 1, '1david1@.comdavid10@.com', true, false, false);

    INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente,
                           b_para_interventor, b_rechazada)
    VALUES ('juan1@.com', 'juan2@.com', 2, '2juan1@.comjuan2@.com', true, false, false);
    INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente,
                           b_para_interventor, b_rechazada)
    VALUES ('juan1@.com', 'juan3@.com', 2, '2juan1@.comjuan3@.com', true, false, false);
    INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente,
                           b_para_interventor, b_rechazada)
    VALUES ('juan1@.com', 'juan4@.com', 2, '2juan1@.comjuan4@.com', true, false, false);
    INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente,
                           b_para_interventor, b_rechazada)
    VALUES ('juan1@.com', 'juan5@.com', 2, '2juan1@.comjuan5@.com', true, false, false);
    INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente,
                           b_para_interventor, b_rechazada)
    VALUES ('juan1@.com', 'juan6@.com', 2, '2juan1@.comjuan6@.com', true, false, false);
    INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente,
                           b_para_interventor, b_rechazada)
    VALUES ('juan1@.com', 'juan7@.com', 2, '2juan1@.comjuan7@.com', true, false, false);
    INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente,
                           b_para_interventor, b_rechazada)
    VALUES ('juan1@.com', 'juan8@.com', 2, '2juan1@.comjuan8@.com', true, false, false);
    INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente,
                           b_para_interventor, b_rechazada)
    VALUES ('juan1@.com', 'juan9@.com', 2, '2juan1@.comjuan9@.com', true, false, false);
    INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente,
                           b_para_interventor, b_rechazada)
    VALUES ('juan1@.com', 'juan10@.com', 2, '2juan1@.comjuan10@.com', true, false, false);

    INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente,
                           b_para_interventor, b_rechazada)
    VALUES ('david1@.com', 'juan2@.com', 2, '2david1@.comjuan2@.com', true, false, false);
    INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente,
                           b_para_interventor, b_rechazada)
    VALUES ('david1@.com', 'juan3@.com', 2, '2david1@.comjuan3@.com', true, false, false);
    INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente,
                           b_para_interventor, b_rechazada)
    VALUES ('david1@.com', 'juan4@.com', 2, '2david1@.comjuan4@.com', true, false, false);
    INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente,
                           b_para_interventor, b_rechazada)
    VALUES ('david1@.com', 'juan5@.com', 2, '2david1@.comjuan5@.com', true, false, false);
    INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente,
                           b_para_interventor, b_rechazada)
    VALUES ('david1@.com', 'juan6@.com', 2, '2david1@.comjuan6@.com', true, false, false);
    INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente,
                           b_para_interventor, b_rechazada)
    VALUES ('david1@.com', 'juan7@.com', 2, '2david1@.comjuan7@.com', true, false, false);
    INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente,
                           b_para_interventor, b_rechazada)
    VALUES ('david1@.com', 'juan8@.com', 2, '2david1@.comjuan8@.com', true, false, false);
    INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente,
                           b_para_interventor, b_rechazada)
    VALUES ('david1@.com', 'juan9@.com', 2, '2david1@.comjuan9@.com', true, false, false);
    INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente,
                           b_para_interventor, b_rechazada)
    VALUES ('david1@.com', 'juan10@.com', 2, '2david1@.comjuan10@.com', true, false, false);


    INSERT INTO GRUPO(c_id_problematica, c_id_padre, d_nombre) VALUES(1, null, 'Grupo 1-1');
    INSERT INTO GRUPO(c_id_problematica, c_id_padre, d_nombre) VALUES(1, 1, 'Grupo 1-2');
    INSERT INTO GRUPO(c_id_problematica, c_id_padre, d_nombre) VALUES(1, 2, 'Grupo 1-3');
    INSERT INTO GRUPO(c_id_problematica, c_id_padre, d_nombre) VALUES(1, 3, 'Grupo 1-4');
    INSERT INTO GRUPO(c_id_problematica, c_id_padre, d_nombre) VALUES(1, 4, 'Grupo 1-5');

    INSERT INTO GRUPO(c_id_problematica, c_id_padre, d_nombre) VALUES(2, null, 'Grupo 2-1');
    INSERT INTO GRUPO(c_id_problematica, c_id_padre, d_nombre) VALUES(2, 6, 'Grupo 2-2');
    INSERT INTO GRUPO(c_id_problematica, c_id_padre, d_nombre) VALUES(2, 7, 'Grupo 2-3');
    INSERT INTO GRUPO(c_id_problematica, c_id_padre, d_nombre) VALUES(2, 8, 'Grupo 2-4');

    INSERT INTO NODO(a_id_pers_prob, a_nombre, a_url_foto, a_ruta_foto, c_id_padre, c_id_grupo) VALUES('juan1@.com1', 'Paso 1', '', '', null, 1);
    INSERT INTO NODO(a_id_pers_prob, a_nombre, a_url_foto, a_ruta_foto, c_id_padre, c_id_grupo) VALUES('juan1@.com1', 'Paso 2', '', '', 1, 1);
    INSERT INTO NODO(a_id_pers_prob, a_nombre, a_url_foto, a_ruta_foto, c_id_padre, c_id_grupo) VALUES('juan1@.com1', 'Paso 3', '', '', 2, 1);

    INSERT INTO NODO(a_id_pers_prob, a_nombre, a_url_foto, a_ruta_foto, c_id_padre, c_id_grupo) VALUES('juan1@.com1', 'Paso 1', '', '', 3, 2);
    INSERT INTO NODO(a_id_pers_prob, a_nombre, a_url_foto, a_ruta_foto, c_id_padre, c_id_grupo) VALUES('juan1@.com1', 'Paso 2', '', '', 4, 2);
    INSERT INTO NODO(a_id_pers_prob, a_nombre, a_url_foto, a_ruta_foto, c_id_padre, c_id_grupo) VALUES('juan1@.com1', 'Paso 3', '', '', 5, 2);

    INSERT INTO NODO(a_id_pers_prob, a_nombre, a_url_foto, a_ruta_foto, c_id_padre, c_id_grupo) VALUES('juan2@.com1', 'Paso 1', '', '', null, 3);
    INSERT INTO NODO(a_id_pers_prob, a_nombre, a_url_foto, a_ruta_foto, c_id_padre, c_id_grupo) VALUES('juan3@.com1', 'Paso 2', '', '', null, 3);
    INSERT INTO NODO(a_id_pers_prob, a_nombre, a_url_foto, a_ruta_foto, c_id_padre, c_id_grupo) VALUES('juan4@.com1', 'Paso 3', '', '', null, 3);

    INSERT INTO NODO(a_id_pers_prob, a_nombre, a_url_foto, a_ruta_foto, c_id_padre, c_id_grupo) VALUES('juan5@.com1', 'Paso 1', '', '', null, 4);
    INSERT INTO NODO(a_id_pers_prob, a_nombre, a_url_foto, a_ruta_foto, c_id_padre, c_id_grupo) VALUES('juan6@.com1', 'Paso 2', '', '', null, 4);
    INSERT INTO NODO(a_id_pers_prob, a_nombre, a_url_foto, a_ruta_foto, c_id_padre, c_id_grupo) VALUES('juan7@.com1', 'Paso 3', '', '', null, 4);

    INSERT INTO NODO(a_id_pers_prob, a_nombre, a_url_foto, a_ruta_foto, c_id_padre, c_id_grupo) VALUES('juan8@.com1', 'Paso 1', '', '', null, 5);
    INSERT INTO NODO(a_id_pers_prob, a_nombre, a_url_foto, a_ruta_foto, c_id_padre, c_id_grupo) VALUES('juan9@.com1', 'Paso 2', '', '', null, 5);
    INSERT INTO NODO(a_id_pers_prob, a_nombre, a_url_foto, a_ruta_foto, c_id_padre, c_id_grupo) VALUES('juan10@.com1', 'Paso 3', '', '', null, 5);

    INSERT INTO NODO(a_id_pers_prob, a_nombre, a_url_foto, a_ruta_foto, c_id_padre, c_id_grupo) VALUES('david1@.com2', 'Paso 1', '', '', null, 6); --16
    INSERT INTO NODO(a_id_pers_prob, a_nombre, a_url_foto, a_ruta_foto, c_id_padre, c_id_grupo) VALUES('david1@.com2', 'Paso 2', '', '', 16, 6);
    INSERT INTO NODO(a_id_pers_prob, a_nombre, a_url_foto, a_ruta_foto, c_id_padre, c_id_grupo) VALUES('david1@.com2', 'Paso 3', '', '', 17, 6);

    INSERT INTO NODO(a_id_pers_prob, a_nombre, a_url_foto, a_ruta_foto, c_id_padre, c_id_grupo) VALUES('david2@.com2', 'Paso 1', '', '', null, 7); --19
    INSERT INTO NODO(a_id_pers_prob, a_nombre, a_url_foto, a_ruta_foto, c_id_padre, c_id_grupo) VALUES('david2@.com2', 'Paso 2', '', '', 19, 7);
    INSERT INTO NODO(a_id_pers_prob, a_nombre, a_url_foto, a_ruta_foto, c_id_padre, c_id_grupo) VALUES('david2@.com2', 'Paso 3', '', '', 20, 7);

    INSERT INTO NODO(a_id_pers_prob, a_nombre, a_url_foto, a_ruta_foto, c_id_padre, c_id_grupo) VALUES('david3@.com2', 'Paso 1', '', '', null, 8); --22
    INSERT INTO NODO(a_id_pers_prob, a_nombre, a_url_foto, a_ruta_foto, c_id_padre, c_id_grupo) VALUES('david3@.com2', 'Paso 2', '', '', 22, 8);
    INSERT INTO NODO(a_id_pers_prob, a_nombre, a_url_foto, a_ruta_foto, c_id_padre, c_id_grupo) VALUES('david3@.com2', 'Paso 3', '', '', 23, 8);

    INSERT INTO NODO(a_id_pers_prob, a_nombre, a_url_foto, a_ruta_foto, c_id_padre, c_id_grupo) VALUES('david4@.com2', 'Paso 1', '', '', null, 9); --25
    INSERT INTO NODO(a_id_pers_prob, a_nombre, a_url_foto, a_ruta_foto, c_id_padre, c_id_grupo) VALUES('david4@.com2', 'Paso 2', '', '', 25, 9);
    INSERT INTO NODO(a_id_pers_prob, a_nombre, a_url_foto, a_ruta_foto, c_id_padre, c_id_grupo) VALUES('david4@.com2', 'Paso 3', '', '', 26, 9);

    INSERT INTO REACCION(c_valor, c_id_grupo, a_id_pers_prob) VALUES(1, 1, 'juan1@.com1');
    INSERT INTO REACCION(c_valor, c_id_grupo, a_id_pers_prob) VALUES(0, 3, 'juan2@.com1');
    INSERT INTO REACCION(c_valor, c_id_grupo, a_id_pers_prob) VALUES(1, 2, 'juan3@.com1');
    INSERT INTO REACCION(c_valor, c_id_grupo, a_id_pers_prob) VALUES(-1, 1, 'juan4@.com1');
    INSERT INTO REACCION(c_valor, c_id_grupo, a_id_pers_prob) VALUES(1, 5, 'juan5@.com1');
    INSERT INTO REACCION(c_valor, c_id_grupo, a_id_pers_prob) VALUES(0, 1, 'juan6@.com1');
    INSERT INTO REACCION(c_valor, c_id_grupo, a_id_pers_prob) VALUES(1, 4, 'juan7@.com1');
    INSERT INTO REACCION(c_valor, c_id_grupo, a_id_pers_prob) VALUES(-1, 3, 'juan8@.com1');
    INSERT INTO REACCION(c_valor, c_id_grupo, a_id_pers_prob) VALUES(0, 4, 'juan9@.com1');
    INSERT INTO REACCION(c_valor, c_id_grupo, a_id_pers_prob) VALUES(-1, 1, 'juan10@.com1'); --Gano el 1

    INSERT INTO REACCION(c_valor, c_id_grupo, a_id_pers_prob) VALUES(1, 6, 'david1@.com2');--
    INSERT INTO REACCION(c_valor, c_id_grupo, a_id_pers_prob) VALUES(1, 7, 'david2@.com2');
    INSERT INTO REACCION(c_valor, c_id_grupo, a_id_pers_prob) VALUES(1, 8, 'david3@.com2');
    INSERT INTO REACCION(c_valor, c_id_grupo, a_id_pers_prob) VALUES(0, 9, 'david4@.com2');
    INSERT INTO REACCION(c_valor, c_id_grupo, a_id_pers_prob) VALUES(0, 9, 'david5@.com2');
    INSERT INTO REACCION(c_valor, c_id_grupo, a_id_pers_prob) VALUES(0, 8, 'david6@.com2');
    INSERT INTO REACCION(c_valor, c_id_grupo, a_id_pers_prob) VALUES(-1, 7, 'david7@.com2');
    INSERT INTO REACCION(c_valor, c_id_grupo, a_id_pers_prob) VALUES(-1, 6, 'david8@.com2');
    INSERT INTO REACCION(c_valor, c_id_grupo, a_id_pers_prob) VALUES(-1, 7, 'david9@.com2');
    INSERT INTO REACCION(c_valor, c_id_grupo, a_id_pers_prob) VALUES(-1, 6, 'david10@.com2');

    --Grupo 1-1 tiene la mayor puntuación con una reacción negativa
    --10 Escritos
    --insert into ESCRITO() VALUES();

    --Grupo 2-4 tiene la mayor puntuación con una reacción neutra
    --10 Escritos

END;
$$ LANGUAGE plpgsql;

select agregardummydata();

DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO public;

CREATE TABLE PERSONA(
    a_email varchar(30),
    d_nombre varchar(30),
    a_pass_hasheado varchar(150)
);

alter table PERSONA ADD CONSTRAINT PK_PERSONA primary key(a_email);

INSERT INTO PERSONA(a_email, d_nombre, a_pass_hasheado) VALUES('juan1@.com', 'Juan1', '1234');
INSERT INTO PERSONA(a_email, d_nombre, a_pass_hasheado) VALUES('juan2@.com', 'Juan2', '1234');
INSERT INTO PERSONA(a_email, d_nombre, a_pass_hasheado) VALUES('juan3@.com', 'Juan3', '1234');
INSERT INTO PERSONA(a_email, d_nombre, a_pass_hasheado) VALUES('juan4@.com', 'Juan4', '1234');
INSERT INTO PERSONA(a_email, d_nombre, a_pass_hasheado) VALUES('juan5@.com', 'Juan5', '1234');
INSERT INTO PERSONA(a_email, d_nombre, a_pass_hasheado) VALUES('david1@.com', 'David1', '1234');
INSERT INTO PERSONA(a_email, d_nombre, a_pass_hasheado) VALUES('david2@.com', 'David2', '1234');
INSERT INTO PERSONA(a_email, d_nombre, a_pass_hasheado) VALUES('david3@.com', 'David3', '1234');
INSERT INTO PERSONA(a_email, d_nombre, a_pass_hasheado) VALUES('david4@.com', 'David4', '1234');
INSERT INTO PERSONA(a_email, d_nombre, a_pass_hasheado) VALUES('david5@.com', 'David5', '1234');

CREATE TABLE PROBLEMATICA(
    c_id SERIAL,
    a_nombre varchar(60),
    a_descripcion varchar(500),
    f_fecha_creacion timestamp,
    c_fase integer
);

alter table PROBLEMATICA add constraint PK_PROBLEMATICA primary key(c_id);
alter table PROBLEMATICA add constraint CK_PROBLEMATICA CHECK ( c_fase >= 0 AND c_fase <= 5 );

INSERT INTO PROBLEMATICA(a_nombre, a_descripcion, f_fecha_creacion, c_fase) VALUES('Problematica 1', 'Descripcion1', now(), 0);
INSERT INTO PROBLEMATICA(a_nombre, a_descripcion, f_fecha_creacion, c_fase) VALUES('Problematica 2', 'Descripcion2', now(), 0);

CREATE TABLE PERSONA_PROBLEMATICA(
    a_id varchar(40),
    a_email varchar(30),
    c_id_problematica int,
    b_interventor boolean
);

ALTER TABLE PERSONA_PROBLEMATICA add constraint PK_PERSONA_PROBLEMATICA primary key(a_id);
ALTER TABLE PERSONA_PROBLEMATICA add constraint FK_PERS_PROB_PERS foreign key(a_email) REFERENCES PERSONA(a_email);
ALTER TABLE PERSONA_PROBLEMATICA add constraint FK_PERS_PROB_PROB foreign key(c_id_problematica) REFERENCES PROBLEMATICA(c_id);

INSERT INTO PERSONA_PROBLEMATICA(a_id, a_email, c_id_problematica, b_interventor) VALUES('juan1@.com1', 'juan1@.com', 1, true);
INSERT INTO PERSONA_PROBLEMATICA(a_id, a_email, c_id_problematica, b_interventor) VALUES('juan2@.com1', 'juan2@.com', 1, true);
INSERT INTO PERSONA_PROBLEMATICA(a_id, a_email, c_id_problematica, b_interventor) VALUES('juan3@.com1', 'juan3@.com', 1, false);

INSERT INTO PERSONA_PROBLEMATICA(a_id, a_email, c_id_problematica, b_interventor) VALUES('david1@.com2', 'david1@.com', 2, true);
INSERT INTO PERSONA_PROBLEMATICA(a_id, a_email, c_id_problematica, b_interventor) VALUES('juan1@.com2', 'juan1@.com', 2, true);
INSERT INTO PERSONA_PROBLEMATICA(a_id, a_email, c_id_problematica, b_interventor) VALUES('david2@.com2', 'david2@.com', 2, true);
INSERT INTO PERSONA_PROBLEMATICA(a_id, a_email, c_id_problematica, b_interventor) VALUES('david3@.com2', 'david3@.com', 2, false);

CREATE TABLE INVITACION(
  a_email_remitente varchar(30),
  a_email_destinatario varchar(30),
  c_id_problematica int,
  a_id varchar(100),
  b_vigente boolean,
  b_para_interventor boolean,
  b_rechazada boolean
);

ALTER TABLE INVITACION add constraint PK_INVICATION primary key(a_id);
ALTER TABLE INVITACION add constraint FK_REMITENTE foreign key(a_email_remitente) REFERENCES PERSONA(a_email);
ALTER TABLE INVITACION add constraint FK_DESTINATARIO foreign key(a_email_destinatario) REFERENCES PERSONA(a_email);
ALTER TABLE INVITACION add constraint FK_PROBLEMATICA foreign key(c_id_problematica) REFERENCES PROBLEMATICA(c_id);


INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente, b_para_interventor, b_rechazada)
    VALUES('juan1@.com', 'juan4@.com', 1, '1juan1@.comjuan4@.com', true, false, false);
INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente, b_para_interventor, b_rechazada)
    VALUES('juan1@.com', 'juan5@.com', 1, '1juan1@.comjuan5@.com', true, false, false);
--Invitación a un David
INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente, b_para_interventor, b_rechazada)
    VALUES('juan1@.com', 'david2@.com', 1, '1juan1@.comdavid2@.com', true, false, false);

INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente, b_para_interventor, b_rechazada)
    VALUES('david1@.com', 'david4@.com', 2, '2david1@.comdavid4@.com', true, false, false);
INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente, b_para_interventor, b_rechazada)
    VALUES('david1@.com', 'david5@.com', 2, '2david1@.comdavid5@.com', true, false, false);
--Invitación a un Juan's
INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente, b_para_interventor, b_rechazada)
    VALUES('david1@.com', 'juan2@.com', 2, '2david1@.comdjuan2@.com', true, false, false);
INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente, b_para_interventor, b_rechazada)
    VALUES('david1@.com', 'juan4@.com', 1, '1david1@.comjuan4@.com', true, false, false);
INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente, b_para_interventor, b_rechazada)
    VALUES('david1@.com', 'juan4@.com', 2, '2david1@.comjuan4@.com', true, false, false);

CREATE TABLE NODO(
    c_id serial,
    a_id_pers_prob varchar(40),
    a_url_foto varchar(250)
);

ALTER TABLE NODO ADD CONSTRAINT PK_NODO primary key(c_id);
ALTER TABLE NODO add constraint FK_NODO_PERS_PROB foreign key(a_id_pers_prob) REFERENCES PERSONA_PROBLEMATICA(a_id);


--declarar la función que va a eliminar todas los registros de todas las tablas
CREATE OR REPLACE FUNCTION truncate_tables(username IN VARCHAR) RETURNS void AS $$
DECLARE
    statements CURSOR FOR
        SELECT tablename FROM pg_tables
        WHERE tableowner = username AND schemaname = 'public';
BEGIN
    FOR stmt IN statements LOOP
        EXECUTE 'TRUNCATE TABLE ' || quote_ident(stmt.tablename) || ' CASCADE;';
    END LOOP;
    ALTER SEQUENCE problematica_c_id_seq RESTART WITH 1;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION agregarDummyData() RETURNS Void AS $$
BEGIN
  INSERT INTO PERSONA(a_email, d_nombre, a_pass_hasheado) VALUES('juan1@.com', 'Juan1', '1234');
  INSERT INTO PERSONA(a_email, d_nombre, a_pass_hasheado) VALUES('juan2@.com', 'Juan2', '1234');
  INSERT INTO PERSONA(a_email, d_nombre, a_pass_hasheado) VALUES('juan3@.com', 'Juan3', '1234');
  INSERT INTO PERSONA(a_email, d_nombre, a_pass_hasheado) VALUES('juan4@.com', 'Juan4', '1234');
  INSERT INTO PERSONA(a_email, d_nombre, a_pass_hasheado) VALUES('juan5@.com', 'Juan5', '1234');
  INSERT INTO PERSONA(a_email, d_nombre, a_pass_hasheado) VALUES('david1@.com', 'David1', '1234');
  INSERT INTO PERSONA(a_email, d_nombre, a_pass_hasheado) VALUES('david2@.com', 'David2', '1234');
  INSERT INTO PERSONA(a_email, d_nombre, a_pass_hasheado) VALUES('david3@.com', 'David3', '1234');
  INSERT INTO PERSONA(a_email, d_nombre, a_pass_hasheado) VALUES('david4@.com', 'David4', '1234');
  INSERT INTO PERSONA(a_email, d_nombre, a_pass_hasheado) VALUES('david5@.com', 'David5', '1234');

  INSERT INTO PROBLEMATICA(a_nombre, a_descripcion, f_fecha_creacion) VALUES('Problematica 1', 'Descripcion1', now());
  INSERT INTO PROBLEMATICA(a_nombre, a_descripcion, f_fecha_creacion) VALUES('Problematica 2', 'Descripcion2', now());

  INSERT INTO PERSONA_PROBLEMATICA(a_id, a_email, c_id_problematica, b_interventor) VALUES('juan1@.com1', 'juan1@.com', 1, true);
  INSERT INTO PERSONA_PROBLEMATICA(a_id, a_email, c_id_problematica, b_interventor) VALUES('juan2@.com1', 'juan2@.com', 1, true);
  INSERT INTO PERSONA_PROBLEMATICA(a_id, a_email, c_id_problematica, b_interventor) VALUES('juan3@.com1', 'juan3@.com', 1, false);

  INSERT INTO PERSONA_PROBLEMATICA(a_id, a_email, c_id_problematica, b_interventor) VALUES('david1@.com2', 'david1@.com', 2, true);
  INSERT INTO PERSONA_PROBLEMATICA(a_id, a_email, c_id_problematica, b_interventor) VALUES('juan1@.com2', 'juan1@.com', 2, true);
  INSERT INTO PERSONA_PROBLEMATICA(a_id, a_email, c_id_problematica, b_interventor) VALUES('david2@.com2', 'david2@.com', 2, true);
  INSERT INTO PERSONA_PROBLEMATICA(a_id, a_email, c_id_problematica, b_interventor) VALUES('david3@.com2', 'david3@.com', 2, false);

  INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente, b_para_interventor, b_rechazada)
        VALUES('juan1@.com', 'juan4@.com', 1, '1juan1@.comjuan4@.com', true, false, false);
  INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente, b_para_interventor, b_rechazada)
        VALUES('juan1@.com', 'juan5@.com', 1, '1juan1@.comjuan5@.com', true, false, false);
--Invitación a un David
  INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente, b_para_interventor, b_rechazada)
        VALUES('juan1@.com', 'david2@.com', 1, '1juan1@.comdavid2@.com', true, false, false);

  INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente, b_para_interventor, b_rechazada)
        VALUES('david1@.com', 'david4@.com', 2, '2david1@.comdavid4@.com', true, false, false);
  INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente, b_para_interventor, b_rechazada)
        VALUES('david1@.com', 'david5@.com', 2, '2david1@.comdavid5@.com', true, false, false);
--Invitación a un Juan's
  INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente, b_para_interventor, b_rechazada)
        VALUES('david1@.com', 'juan2@.com', 2, '2david1@.comdjuan2@.com', true, false, false);
  INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente, b_para_interventor, b_rechazada)
        VALUES('david1@.com', 'juan4@.com', 1, '1david1@.comjuan4@.com', true, false, false);
  INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente, b_para_interventor, b_rechazada)
        VALUES('david1@.com', 'juan4@.com', 2, '2david1@.comjuan4@.com', true, false, false);
END;
$$ LANGUAGE plpgsql;

--Usar la función
--SELECT truncate_tables('postgres');


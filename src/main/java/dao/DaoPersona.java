package dao;

import entity.Persona;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface DaoPersona {

    @SqlUpdate("CREATE TABLE PERSONA(\n" +
            "    a_email varchar(30),\n" +
            "    d_nombre varchar(30),\n" +
            "    a_pass_hasheado varchar(150)\n" +
            ");\n" +
            "\n" +
            "alter table PERSONA ADD CONSTRAINT PK_PERSONA primary key(a_email);\n" +
            "\n" +
            "INSERT INTO PERSONA(a_email, d_nombre, a_pass_hasheado) VALUES('juan@juan.com', 'Juan', '1234');\n" +
            "INSERT INTO PERSONA(a_email, d_nombre, a_pass_hasheado) VALUES('david@david.com', 'David', 'asdf');\n" +
            "\n" +
            "CREATE TABLE PROBLEMATICA(\n" +
            "    c_id SERIAL,\n" +
            "    a_nombre varchar(60),\n" +
            "    a_descripcion varchar(500),\n" +
            "    f_fecha_creacion date\n" +
            ");\n" +
            "\n" +
            "alter table PROBLEMATICA add constraint PK_PROBLEMATICA primary key(c_id);\n" +
            "\n" +
            "CREATE TABLE PERSONA_PROBLEMATICA(\n" +
            "    a_id varchar(40),\n" +
            "    a_email varchar(30),\n" +
            "    c_id_problematica int,\n" +
            "    b_interventor boolean\n" +
            ");\n" +
            "\n" +
            "ALTER TABLE PERSONA_PROBLEMATICA add constraint PK_PERSONA_PROBLEMATICA primary key(a_id);\n" +
            "ALTER TABLE PERSONA_PROBLEMATICA add constraint FK_PERS_PROB_PERS foreign key(a_email) REFERENCES PERSONA(a_email);\n" +
            "ALTER TABLE PERSONA_PROBLEMATICA add constraint FK_PERS_PROB_PROB foreign key(c_id_problematica) REFERENCES PROBLEMATICA(c_id);")
    void crearTable();

    @SqlUpdate("INSERT INTO PERSONA(a_email, d_nombre, a_pass_hasheado) VALUES(:email, :nombre, :pass)")
    @GetGeneratedKeys
    Persona agregarPersona(@BindBean Persona persona);

    @SqlQuery("Select d_nombre, a_email FROM PERSONA WHERE a_email = :email AND a_pass_hasheado = :pass")
    Persona darPersonaPorCredenciales(@BindBean Persona persona);

    @SqlUpdate("DROP SCHEMA public CASCADE;\n" +
            "    CREATE SCHEMA public;\n" +
            "    \n" +
            "    GRANT ALL ON SCHEMA public TO postgres;\n" +
            "    GRANT ALL ON SCHEMA public TO public;")
    void eliminarTablas();
}
package entity;

import org.jdbi.v3.core.mapper.reflect.ColumnName;

public class Grupo {

    public int id;
    public int idPadre;
    public String nombre;
    public int idProblematica;

    public Grupo() {
    }

    @ColumnName("c_id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ColumnName("c_id_padre")
    public int getIdPadre() {
        return idPadre;
    }

    public void setIdPadre(int idPadre) {
        this.idPadre = idPadre;
    }

    @ColumnName("d_nombre")
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @ColumnName("c_id_problematica")
    public int getIdProblematica() {
        return idProblematica;
    }

    public void setIdProblematica(int idProblematica) {
        this.idProblematica = idProblematica;
    }
}

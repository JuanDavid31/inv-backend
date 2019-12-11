package entity;

import org.jdbi.v3.core.mapper.reflect.ColumnName;

public class Grupo {

    public int id;
    public Integer idPadre;
    public String nombre;
    public int idProblematica;
    public int reaccion;
    public int cantidad;

    public Grupo() {
    }

    public Grupo(String nombre){
        this.nombre = nombre;
    }

    public Grupo(String nombre, int idPadre){
        this.nombre = nombre;
        this.idPadre = idPadre;
    }

    @ColumnName("c_id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ColumnName("c_id_padre")
    public Integer getIdPadre() {
        return idPadre;
    }

    public void setIdPadre(Integer idPadre) {
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

    @ColumnName("c_valor")
    public int getReaccion() {
        return reaccion;
    }

    public void setReaccion(int reaccion) {
        this.reaccion = reaccion;
    }

    @ColumnName("count")
    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    @Override
    public String toString() {
        return "Grupo{" +
                "id=" + id +
                ", idPadre=" + idPadre +
                ", nombre='" + nombre + '\'' +
                ", idProblematica=" + idProblematica +
                ", reaccion=" + reaccion +
                ", cantidad=" + cantidad +
                '}';
    }
}

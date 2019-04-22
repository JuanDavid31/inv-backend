package entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jdbi.v3.core.mapper.reflect.ColumnName;
import org.joda.time.DateTime;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

public class Problematica {

    @JsonProperty
    private int id;

    @JsonProperty
    private String nombre;

    @JsonProperty
    private String descripcion;

    @JsonProperty
    private boolean esInterventor;

    @JsonProperty
    @JsonFormat(pattern="dd/MM/yyyy HH:mm:ss")
    private LocalDateTime fechaCreacion;

    public Problematica(){}

    public Problematica(int id, String nombre, String descripcion){
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.esInterventor = false;
    }

    public Problematica(int id, String nombre, String descripcion, boolean esInterventor, Timestamp fechaCreacion){
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.esInterventor = esInterventor;
        this.fechaCreacion = fechaCreacion.toLocalDateTime();
    }

    @ColumnName("c_id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ColumnName("a_nombre")
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @ColumnName("a_descripcion")
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @ColumnName("b_interventor")
    public boolean getEsInterventor(){
        return esInterventor;
    }

    public void setEsInterventor(boolean esInterventor) {
        this.esInterventor = esInterventor;
    }

    @ColumnName("f_fecha_creacion")
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}

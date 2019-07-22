package entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jdbi.v3.core.mapper.reflect.ColumnName;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Problematica {

    @JsonProperty
    private int id;

    @JsonProperty
    @NotNull(message = "no puede ser vacio")
    @Size(min = 10, max = 60, message = "debe tener entre 10 y 60 caracteres y ser descriptivo")
    private String nombre;

    @JsonProperty
    @NotNull(message = "no puede ser vacio")
    @Size(min = 20, max = 500, message = "debe tener entre 20 y 500 caracteres")
    private String descripcion;

    @JsonProperty
    private boolean esInterventor;

    @JsonProperty
    @JsonFormat(pattern="dd/MM/yyyy HH:mm:ss")
    private LocalDateTime fechaCreacion;

    @JsonProperty
    private int fase;

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

    @ColumnName("c_fase")
    public int getFase() {
        return fase;
    }

    public void setFase(int fase) {
        this.fase = fase;
    }
}

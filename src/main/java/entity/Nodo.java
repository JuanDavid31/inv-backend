package entity;

import org.jdbi.v3.core.mapper.reflect.ColumnName;

public class Nodo {

    public int id;
    public String email;
    public String nombre;
    public int idProblematica;
    public String urlFoto;
    public String rutaFoto;
    public int idPadre;

    public Nodo(){

    }

    public Nodo(String nombre, String email, int idProblematica) {
        this.nombre = nombre;
        this.email = email;
        this.idProblematica = idProblematica;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getIdProblematica() {
        return idProblematica;
    }

    public void setIdProblematica(int idProblematica) {
        this.idProblematica = idProblematica;
    }

    @ColumnName("a_url_foto")
    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }

    @ColumnName("a_ruta_foto")
    public String getRutaFoto() {
        return rutaFoto;
    }

    public void setRutaFoto(String rutaFoto) {
        this.rutaFoto = rutaFoto;
    }

    @ColumnName("c_id_padre")
    public int getIdPadre() {
        return idPadre;
    }

    public void setIdPadre(int idPadre) {
        this.idPadre = idPadre;
    }
}

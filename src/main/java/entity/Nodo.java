package entity;

import org.jdbi.v3.core.mapper.reflect.ColumnName;

public class Nodo {

    public int id;
    public String email;
    public int idProblematica;
    public String urlFoto;
    public int idPadre;

    public Nodo(){

    }

    public Nodo(String email, int idProblematica) {
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

    @ColumnName("c_id_padre")
    public int getIdPadre() {
        return idPadre;
    }

    public void setIdPadre(int idPadre) {
        this.idPadre = idPadre;
    }
}

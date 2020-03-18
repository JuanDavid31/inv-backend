package entity

import org.jdbi.v3.core.mapper.reflect.ColumnName

class Nodo {

    @get:ColumnName("c_id")
    var id: Int = 0
    @get:ColumnName("a_nombre")
    var nombre: String = ""
    @get:ColumnName("a_url_foto")
    var urlFoto: String = ""
    @get:ColumnName("c_id_nodo_padre")
    var idPadre: Int = 0
    @get:ColumnName("c_id_grupo")
    var idGrupo: Int? = null
    @get:ColumnName("nombreCreador")
    var nombreCreador: String = ""
    var email: String = ""
    var idProblematica: Int = 0

    constructor(){}

    constructor(nombre: String, email: String, idProblematica: Int){
        this.nombre = nombre
        this.email = email
        this.idProblematica = idProblematica
    }

    override fun toString(): String {
        return "Nodo{" +
                "id='" + id + '\'' +
                ", idPadre='" + idPadre + '\'' +
                ", nombre=" + nombre + '\'' +
                ", idProblematica=" + idProblematica + '\'' +
                ", urlFoto=" + urlFoto +
                '}';
    }

}
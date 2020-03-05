package entity

import org.jdbi.v3.core.mapper.reflect.ColumnName

class Grupo {

    @get:ColumnName("c_id")
    var id: Int? = null
    @get:ColumnName("c_id_padre")
    var idPadre: Int? = null
    @get:ColumnName("d_nombre")
    var nombre: String? = null
    @get:ColumnName("c_id_problematica")
    var idProblematica: Int? = 0

    constructor()

    constructor(id: Int, nombre: String){
        this.id = id
        this.nombre = nombre
        idPadre = null
    }

    override fun toString(): String {
        return "Relacion{" +
                "id='" + id + '\'' +
                ", idPadre='" + idPadre + '\'' +
                ", nombre=" + nombre + '\'' +
                ", idProblematica=" + idProblematica +
                '}';
    }

}
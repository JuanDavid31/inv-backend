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

    constructor(nombre: String){
        this.nombre = nombre;
    }

    constructor(id: Int, nombre: String, idPadre: Int){
        this.id = id
        this.nombre = nombre
        this.idPadre = idPadre
    }
}
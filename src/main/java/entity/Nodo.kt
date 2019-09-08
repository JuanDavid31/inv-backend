package entity

import org.jdbi.v3.core.mapper.reflect.ColumnName

class Nodo(nombre: String, var email: String, var idProblematica: Int) {

    @get:ColumnName("c_id")
    var id: Int = 0
    @get:ColumnName("a_nombre")
    var nombre: String = nombre
    @get:ColumnName("a_url_foto")
    var urlFoto: String = ""
    @get:ColumnName("a_ruta_foto")
    var rutaFoto: String = ""
    @get:ColumnName("c_id_padre")
    var idPadre: Int = 0
}
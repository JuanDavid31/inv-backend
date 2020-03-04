package entity

import org.jdbi.v3.core.mapper.reflect.ColumnName

class Relacion() {


    @get:ColumnName("c_id")
    var id: Int? = null

    @get:ColumnName("c_id_nodo")
    var idNodo: Int? = null

    @get:ColumnName("c_id_nodo_padre")
    var idNodoPadre: Int? = null

    @get:ColumnName("c_id_grupo")
    var idGrupo: Int? = null

    @get:ColumnName("c_id_grupo_padre")
    var idGrupoPadre: Int? = null

    @get:ColumnName("c_fase")
    var fase: Int? = null

}
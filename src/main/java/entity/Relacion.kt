package entity

import org.jdbi.v3.core.mapper.reflect.ColumnName

class Relacion {


    @get:ColumnName("c_id")
    var id: Int = 0

    @get:ColumnName("c_id_nodo")
    var idNodo: Int = 0

    @get:ColumnName("c_id_nodo_padre")
    var idNodoPadre: Int = 0

    @get:ColumnName("c_id_grupo")
    var idGrupo: Int = 0

    @get:ColumnName("c_id_grupo_padre")
    var idGrupoPadre: Int = 0

    @get:ColumnName("c_fase")
    var fase: Int = 0

    constructor()

}
package entity

import org.jdbi.v3.core.mapper.reflect.ColumnName

class GrupoConReaccion {

    @get:ColumnName("c_id")
    var id: Int? = null
    @get:ColumnName("c_id_padre")
    var idPadre: Int? = null
    @get:ColumnName("d_nombre")
    var nombre: String? = null
    @get:ColumnName("negativa")
    var reaccionesNegativas: Int? = null
    @get:ColumnName("neutra")
    var reaccionesNeutras: Int? = null
    @get:ColumnName("positiva")
    var reaccionesPositivas: Int? = null
    var esGrupo: Boolean = true

    constructor()
}
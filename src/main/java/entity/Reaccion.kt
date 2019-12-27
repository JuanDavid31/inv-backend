package entity

import org.jdbi.v3.core.mapper.reflect.ColumnName

class Reaccion {

    @get:ColumnName("c_valor")
    var valor: Int = 0
    @get:ColumnName("c_id_grupo")
    var idGrupo: Int = 0
    @get:ColumnName("a_id_pers_prob")
    var idPersonaProblematica: String = ""
}
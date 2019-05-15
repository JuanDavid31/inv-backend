package entity

import org.jdbi.v3.core.mapper.reflect.ColumnName

class Escrito(){

    var id = 0
        @ColumnName("c_id") get() = id
    var descripcion : String? = null
        @ColumnName("a_descripcion") get() = descripcion
    var idGrupo: String? = null
        @ColumnName("c_id_grupo") get() = idGrupo
    var idPersonaProblematica: String? = null
        @ColumnName("a_id_pers_prob") get() = idPersonaProblematica

}
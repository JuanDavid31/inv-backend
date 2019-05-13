package entity

import org.jdbi.v3.core.mapper.reflect.ColumnName

data class Escrito constructor(@ColumnName("c_id") var id: Int,
                               @ColumnName("descripcion") var descripcion : String,
                               @ColumnName("c_id_grupo") var idGrupo: String,
                               @ColumnName("a_id_pers_prob") var idPersonaProblematica: String)
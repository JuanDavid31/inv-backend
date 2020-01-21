package entity

import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.validator.constraints.NotEmpty
import org.jdbi.v3.core.mapper.reflect.ColumnName
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

class Escrito(){

    @JsonProperty
    @get:ColumnName("c_id")
    var id = 0

    @JsonProperty
    @NotEmpty(message = "no puede ser vacio")
    @Size(min = 4, max = 20, message = "debe tener minimo 4 y maximo 20 caracteres")
    @get:ColumnName("a_nombre")
    var nombre: String = ""

    @JsonProperty
    @NotEmpty(message = "no puede ser vacio")
    @Size(min = 30, max = 500, message = "debe tener minimo 30 y maximo 500 caracteres")
    @get:ColumnName("a_descripcion")
    var descripcion : String? = null

    @JsonProperty
    @NotNull(message = "no puede ser vacio")
    @get:ColumnName("c_id_grupo")
    var idGrupo: Int? = null

    @get:ColumnName("a_id_pers_prob")
    var idPersonaProblematica: String? = null


}
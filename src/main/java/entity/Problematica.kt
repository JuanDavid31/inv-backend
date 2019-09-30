package entity

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import org.jdbi.v3.core.mapper.reflect.ColumnName

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size
import java.sql.Timestamp
import java.time.LocalDateTime

class Problematica {

    @JsonProperty
    @get:ColumnName("c_id")
    var id: Int = 0

    @JsonProperty
    @NotNull(message = "no puede ser vacio")
    @Size(min = 10, max = 60, message = "debe tener entre 10 y 60 caracteres y ser descriptivo")
    @get:ColumnName("a_nombre")
    var nombre: String? = null

    @JsonProperty
    @NotNull(message = "no puede ser vacio")
    @Size(min = 20, max = 500, message = "debe tener entre 20 y 500 caracteres")
    @get:ColumnName("a_descripcion")
    var descripcion: String? = null

    @JsonProperty
    @get:ColumnName("b_interventor")
    var esInterventor: Boolean = false

    @JsonProperty
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @get:ColumnName("f_fecha_creacion")
    var fechaCreacion: LocalDateTime? = null

    @JsonProperty
    @get:ColumnName("c_fase")
    var fase: Int = 0

    constructor() {}

    constructor(id: Int, nombre: String, descripcion: String) {
        this.id = id
        this.nombre = nombre
        this.descripcion = descripcion
        this.esInterventor = false
    }

    constructor(id: Int, nombre: String, descripcion: String, esInterventor: Boolean, fechaCreacion: Timestamp) {
        this.id = id
        this.nombre = nombre
        this.descripcion = descripcion
        this.esInterventor = esInterventor
        this.fechaCreacion = fechaCreacion.toLocalDateTime()
    }
}

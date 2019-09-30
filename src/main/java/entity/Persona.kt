package entity


import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.validator.constraints.Email
import org.jdbi.v3.core.mapper.reflect.ColumnName

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

class Persona {

    @JsonProperty
    @NotNull(message = "no puede ser vacio")
    @Size(max = 45, message = "no puede tener más de 45 caracteres")
    @Email(message = "invalido", regexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$")
    @get:ColumnName("a_email")
    var email: String = ""

    @JsonProperty
    @NotNull(message = "no puede estar vacio")
    @Size(min = 3, max = 30, message = "debe tener minimo 3 y maximo 45 caracteres")
    @get:ColumnName("d_nombres")
    var nombres: String = ""

    @JsonProperty
    @NotNull(message = "no puede estar vacio")
    @Size(min = 3, max = 30, message = "debe tener minimo 3 y maximo 45 caracteres")
    @get:ColumnName("d_apellidos")
    var apellidos: String = ""

    @JsonProperty
    @NotNull(message = "no puede estar vacia")
    @Size(min = 8, max = 25, message = "debe tener minimo 8 y maximo 25 caracteres")
    @get:ColumnName("a_pass_hasheado")
    var pass: String = ""

    constructor() {

    }

    constructor(email: String, nombres: String) {
        this.email = email
        this.nombres = nombres
    }

    constructor(email: String, nombres: String, apellidos: String, pass: String) {
        this.email = email
        this.nombres = nombres
        this.apellidos = apellidos
        this.pass = pass
    }

    constructor(nombres: String) {
        this.nombres = nombres
    }

    override fun toString(): String {
        return "Persona{" +
                "email='" + email + '\''.toString() +
                ", nombres='" + nombres + '\''.toString() +
                ", apellidos='" + apellidos + '\''.toString() +
                ", pass='" + pass + '\''.toString() +
                '}'.toString()
    }
}

package entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Email;
import org.jdbi.v3.core.mapper.reflect.ColumnName;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Persona {

    @JsonProperty
    @NotNull(message = "no puede ser vacio")
    @Size(max = 45, message = "no puede tener más de 45 caracteres")
    @Email(message = "invalido")
    public String email;

    @JsonProperty
    @NotNull(message = "no puede estar vacio")
    @Size(min=3, max = 30, message = "debe tener minimo 3 y maximo 45 caracteres")
    public String nombres;

    @JsonProperty
    @NotNull(message = "no puede estar vacio")
    @Size(min=3, max=10, message = "debe tener minimo 3 y maximo 45 caracteres")
    public String apellidos;

    @JsonProperty
    @NotNull(message = "no puede estar vacia")
    @Size(min=8, max=25, message = "debe tener minimo 3 y maximo 25 caracteres")
    public String pass;

    public Persona(){

    }

    public Persona(String email, String nombres){
        this.email = email;
        this.nombres = nombres;
    }

    public Persona(String email, String nombres, String pass) {
        this.email = email;
        this.nombres = nombres;
        this.pass = pass;
    }

    public Persona(String nombre){
        this.nombres = nombre;
    }

    @ColumnName("a_email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @ColumnName("d_nombres")
    public String getNombre() {
        return nombres;
    }

    public void setNombre(String nombre) {
        this.nombres = nombre;
    }

    @ColumnName("d_apellidos")
    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    @ColumnName("a_pass_hasheado")
    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    @Override
    public String toString() {
        return "Persona{" +
                "email='" + email + '\'' +
                ", nombres='" + nombres + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", pass='" + pass + '\'' +
                '}';
    }
}

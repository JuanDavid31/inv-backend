package entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.jdbi.v3.core.mapper.reflect.ColumnName;

public class Persona {

    @JsonProperty
    public String email;
    @JsonProperty
    public String nombre;
    @JsonProperty
    public String pass;

    public Persona(){

    }

    public Persona(String email, String nombre){
        this.email = email;
        this.nombre = nombre;
    }

    public Persona(String email, String nombre, String pass) {
        this.email = email;
        this.nombre = nombre;
        this.pass = pass;
    }

    public Persona(String nombre){
        this.nombre = nombre;
    }

    @ColumnName("a_email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @ColumnName("d_nombre")
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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
                ", nombre='" + nombre + '\'' +
                ", pass='" + pass + '\'' +
                '}';
    }
}

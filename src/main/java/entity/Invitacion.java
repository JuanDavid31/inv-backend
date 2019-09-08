package entity;

import org.hibernate.validator.constraints.Email;
import org.jdbi.v3.core.mapper.reflect.ColumnName;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class Invitacion {

    public String id;

    @NotNull(message = "no puede ser vacio")
    @Min(value = 1, message = "debe ser valido")
    public int idProblematica;

    @NotNull(message = "no puede ser vacio")
    @Email(message = "debe ser valido")
    public String emailRemitente;

    @NotNull(message = "no puede ser vacio")
    @Email(message = "debe ser valido")
    public String emailDestinatario;

    @NotNull(message = "no puede ser vacio")
    public boolean paraInterventor;

    public String nombreDestinatario;
    public boolean estaVigente;
    public boolean rechazada;

    public Invitacion() {
    }

    public Invitacion(String id, int idProblematica, String emailRemitente, String emailDestinatario, boolean estaVigente, boolean paraInterventor) {
        this.id = id;
        this.idProblematica = idProblematica;
        this.emailRemitente = emailRemitente;
        this.emailDestinatario = emailDestinatario;
        this.estaVigente = estaVigente;
        this.paraInterventor = paraInterventor;
    }

    @ColumnName("a_id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @ColumnName("a_email_remitente")
    public String getEmailRemitente() {
        return emailRemitente;
    }

    public void setEmailRemitente(String emailRemitente) {
        this.emailRemitente = emailRemitente;
    }

    @ColumnName("a_email_destinatario")
    public String getEmailDestinatario() {
        return emailDestinatario;
    }

    public void setEmailDestinatario(String emailDestinatario) {
        this.emailDestinatario = emailDestinatario;
    }

    @ColumnName("c_id_problematica")
    public int getIdProblematica() {
        return idProblematica;
    }

    public void setIdProblematica(int idProblematica) {
        this.idProblematica = idProblematica;
    }

    @ColumnName("b_vigente")
    public boolean getEstaVigente() {
        return estaVigente;
    }

    public void setEstaVigente(boolean estaVigente) {
        this.estaVigente = estaVigente;
    }

    @ColumnName("b_para_invertentor")
    public boolean getParaInterventor() {
        return paraInterventor;
    }

    public void setParaInterventor(boolean paraInterventor) {
        this.paraInterventor = paraInterventor;
    }

    @ColumnName("d_nombres")
    public String getNombreDestinatario() {
        return nombreDestinatario;
    }

    public void setNombreDestinatario(String nombreDestinatario) {
        this.nombreDestinatario = nombreDestinatario;
    }

    @ColumnName("b_rechazada")
    public boolean getRechazada() {
        return rechazada;
    }

    public void setRechazada(boolean rechazada) {
        this.rechazada = rechazada;
    }
}

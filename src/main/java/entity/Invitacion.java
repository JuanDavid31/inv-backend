package entity;

import org.jdbi.v3.core.mapper.reflect.ColumnName;

public class Invitacion {

    public String id;
    public int idProblematica;
    public String emailRemitente;
    public String emailDestinatario;
    public String nombreDestinatario;
    public boolean estaVigente;
    public boolean paraInterventor;
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

    @ColumnName("d_nombre")
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

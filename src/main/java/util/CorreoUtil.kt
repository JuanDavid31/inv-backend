package util

import java.util.*
import javax.mail.*
import javax.mail.internet.MimeMessage

class CorreoUtils(val usuario: String, val pass: String){

    private fun darSesion(): Session? {
        val prop = Properties()
        prop.put("mail.smtp.auth", true)
        prop.put("mail.smtp.starttls.enable", "true")
        prop.put("mail.smtp.host", "smtp.gmail.com")
        prop.put("mail.smtp.socketFactory.port", "465")
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
        prop.put("mail.smtp.port", "465")

        return Session.getInstance(prop, object: Authenticator() {
            override fun getPasswordAuthentication() = PasswordAuthentication(usuario, pass)
        })
    }

    fun enviar(destinatario: String){
        val mensaje = MimeMessage(darSesion())
        mensaje.addRecipients(Message.RecipientType.TO, destinatario)
        mensaje.setSubject("Asunto de prueba")
        mensaje.setDescription("Descripción de prueba")
        mensaje.setText("Texto")
        Transport.send(mensaje)
    }
}
package util

import entity.Persona
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

    fun enviarPassA(persona: Persona): Boolean{
        return try {
            Transport.send(darMensajeOlvidoPass(persona))
            true
        }catch (e: MessagingException){
            e.printStackTrace()
            false
        }
    }

    private fun darMensajeOlvidoPass(persona: Persona): Message{
        val mensaje = MimeMessage(darSesion())
        mensaje.addRecipients(Message.RecipientType.TO, persona.email)
        mensaje.setSubject("Contraseña olvidada")
        mensaje.setText("Estimado ${persona.nombres} aquí esta su contraseña: \n ${persona.pass}")
        return mensaje
    }

    fun existe(correo: String): Boolean {
        return try {
            Transport.send(darMensajeVerificacionCorreo(correo))
            true
        }catch (e: Exception){
            e.printStackTrace()
            false
        }
    }

    private fun darMensajeVerificacionCorreo(correo: String): Message{
        val mensaje = MimeMessage(darSesion())
        mensaje.addRecipients(Message.RecipientType.TO, correo)
        mensaje.setSubject("Verificación de existencia")
        mensaje.setText("Estimado usuario, si puede leer este mensaje entonces la verificación de correo ha sido exitosa")
        return mensaje
    }
}
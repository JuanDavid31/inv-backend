package util

import com.neverbounce.api.client.NeverbounceClient
import com.neverbounce.api.client.NeverbounceClientFactory
import com.neverbounce.api.model.SafeToSend
import com.neverbounce.api.model.SingleCheckResponse
import entity.Persona
import java.util.*
import javax.mail.*
import javax.mail.internet.MimeMessage

class CorreoUtils(val usuario: String, val pass: String, val neverBounceKey: String){

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

    fun existe(correo: String): Boolean = verificar(correo)

    fun verificar(correo: String): Boolean {
        val neverbounceClient: NeverbounceClient = NeverbounceClientFactory.create(neverBounceKey)
        val singleCheckResponse: SingleCheckResponse = neverbounceClient
                .prepareSingleCheckRequest()
                .withEmail(correo) // address to verify
                .withAddressInfo(true) // return address info with response
                .withCreditsInfo(true) // return account credits info with response
                .withTimeout(60) // only wait on slow email servers for 20 seconds max
                .build()
                .execute()

        return singleCheckResponse.result.isSafeToSend != SafeToSend.NO
    }

}
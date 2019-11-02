package entity

import org.eclipse.jetty.websocket.api.Session

data class SesionCliente(val sesion:Session, var nombre: String, var email: String)
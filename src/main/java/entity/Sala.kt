package entity

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import org.eclipse.jetty.websocket.api.Session
import java.util.concurrent.ConcurrentHashMap

class Sala(val clientes: MutableMap<String, SesionCliente>, var nodos: MutableMap<String, JsonNode>){

    var posicionesIniciales: JsonNode? = null;

    fun agregarSesion(sesion: Session){
        clientes[sesion.hashCode().toString()] = SesionCliente(sesion, "", "", false)
    }

    fun eliminarCliente(sesion: Session) = clientes.remove(sesion.hashCode().toString())

    fun actualizarNodo(nuevoNodo: JsonNode) {
        val idNodo = nuevoNodo.get("data").get("id").asText()
        val nodoACambiar = nodos.get(idNodo)
        (nodoACambiar as ObjectNode).replace("position", nuevoNodo.get("position"))
    }

    fun cambiarNodos(nuevosNodos: List<JsonNode>){
        this.nodos = nuevosNodos.map { it.get("data").get("id").asText() to it }.toMap().toMutableMap()
    }

}
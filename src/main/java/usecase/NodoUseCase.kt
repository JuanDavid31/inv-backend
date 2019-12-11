package usecase

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.IntNode
import com.fasterxml.jackson.databind.node.NullNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import dao.DaoNodo
import entity.Mensaje
import entity.Error
import entity.Nodo

class NodoUseCase(val daoNodo: DaoNodo) {

    fun apadrinar(id: Int, idPadre: Int): Any {
        val apadrinado = daoNodo.apadrinar(id, idPadre)
        return if (apadrinado) Mensaje("Conexión exitosa") else Error(arrayOf("El nodo no existe."))
    }

    fun desApadrinar(id: Int): Any {
        val conexionesEliminadas = daoNodo.eliminarConexionesPadreEHijo(id)
        return if(conexionesEliminadas) Mensaje("Conexiones eliminadas exitosamente") else Error(arrayOf("El nodo no existe o no tiene conexiones."))
    }

    fun darNodosPorProblematica(idProblematica: Long): List<JsonNode> {
        return daoNodo.darNodos(idProblematica).map {
            val objectMapper = ObjectMapper()
            val nodo = objectMapper.createObjectNode()
            val data = objectMapper.createObjectNode()

            (data as ObjectNode).set("id", IntNode(it.id))
            data.set("nombre", TextNode(it.nombre))
            data.set("parent", if (it.idGrupo != null) IntNode(it.idGrupo!!) else NullNode.instance)
            data.set("urlFoto", TextNode(it.urlFoto))

            (nodo as ObjectNode).set("data", data)

            nodo
        }
    }

    fun actualizarGrupoNodo(nodo: Nodo) = daoNodo.actualizarGrupoNodo(nodo)

}
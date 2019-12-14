package usecase

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.BooleanNode
import com.fasterxml.jackson.databind.node.IntNode
import com.fasterxml.jackson.databind.node.NullNode
import com.fasterxml.jackson.databind.node.TextNode
import dao.DaoGrupo
import entity.Grupo

class GrupoUseCase(val daoGrupo: DaoGrupo){

    fun darGrupos(idProblematica: Long): List<JsonNode> {
        val conexiones: MutableList<JsonNode> = ArrayList()
        return daoGrupo.darGrupos(idProblematica)
            .map {
                val objectMapper = ObjectMapper()
                val data = objectMapper.createObjectNode()
                val grupo = objectMapper.createObjectNode()

                data.set("id", IntNode(it.id))
                data.set("parent", NullNode.instance)
                data.set("nombre", TextNode(it.nombre))
                data.set("esGrupo", BooleanNode.TRUE )

                if(it.idPadre != null){
                    val conexion = objectMapper.createObjectNode()
                    val data = objectMapper.createObjectNode()

                    data.set("id", TextNode("$it.idPadre$it.id"))
                    data.set("source", IntNode(it.id))
                    data.set("target", IntNode(it.idPadre))
                    conexion.set("data", data)
                    conexiones.add(conexion)
                }

                grupo.set("data", data)
            }.toMutableList() + conexiones
    }

    fun agregarGrupo(idProblematica: Int, grupo: Grupo) = daoGrupo.agregarGrupo(idProblematica, grupo)

    fun actualizarGrupo(idGrupo: Int, grupo: Grupo): Boolean = daoGrupo.actualizarGrupo(idGrupo, grupo)

    fun apadrinar(id: Int, idPadre: Int, idProblematica: Int): Boolean = daoGrupo.apadrinar(id, idPadre, idProblematica)

    fun desApadrinar(id: Int, idProblematica: Int): Boolean = daoGrupo.desApadrinar(id, idProblematica)

    fun darGruposConReacciones(idProblematica: Int) = daoGrupo.darGruposConReacciones(idProblematica)

    fun darGrupoConReaccion(idProblematica: Int, idPersonaProblematica: String): Grupo? {
        val optionalGrupo = daoGrupo.darGrupoConReaccion(idProblematica, idPersonaProblematica)
        return if (optionalGrupo.isPresent) optionalGrupo.get() else null
    }

    fun eliminarGrupo(id: Int, idProblematica: Int): Boolean = daoGrupo.eliminarGrupo(id, idProblematica)

    /**
     * Lanza una excepción si la lista esta vacia.
     */
    fun eliminarGrupos(idsGrupos: List<Int>, idSala: Int) = if (idsGrupos.isEmpty()) false else daoGrupo.eliminarGrupos(idsGrupos, idSala)

    /**
     * Lanza una excepción si la lista esta vacia.
     */
    fun eliminarConexiones(idsGrupos: List<Any?>) = if(idsGrupos.isEmpty()) false else daoGrupo.eliminarConexiones(idsGrupos)
}
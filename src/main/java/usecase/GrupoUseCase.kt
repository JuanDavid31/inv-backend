package usecase

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.BooleanNode
import com.fasterxml.jackson.databind.node.IntNode
import com.fasterxml.jackson.databind.node.NullNode
import com.fasterxml.jackson.databind.node.TextNode
import dao.DaoGrupo
import entity.Grupo
import org.jdbi.v3.core.statement.UnableToExecuteStatementException

class GrupoUseCase(val daoGrupo: DaoGrupo){

    fun darGrupos(idProblematica: Long): List<JsonNode> {
        return daoGrupo.darGrupos(idProblematica)
            .map {
                val objectMapper = ObjectMapper()
                val data = objectMapper.createObjectNode()
                val grupo = objectMapper.createObjectNode()

                data.set("id", IntNode(it.id))
                data.set("parent", if(it.idPadre != null) IntNode(it.idPadre) else NullNode.instance)
                data.set("nombre", TextNode(it.nombre))
                data.set("esGrupo", BooleanNode.TRUE )

                grupo.set("data", data)
            }
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

    fun eliminarGrupos(idsGrupos: List<Int>, idSala: Int) = if (idsGrupos.isEmpty()) false else daoGrupo.eliminarGrupos(idsGrupos, idSala)
}
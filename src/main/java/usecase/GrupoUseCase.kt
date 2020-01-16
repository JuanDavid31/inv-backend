package usecase

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.BooleanNode
import com.fasterxml.jackson.databind.node.IntNode
import com.fasterxml.jackson.databind.node.NullNode
import com.fasterxml.jackson.databind.node.TextNode
import dao.DaoGrupo
import dao.DaoReaccion
import entity.Grupo

class GrupoUseCase(val daoGrupo: DaoGrupo, val daoReaccion: DaoReaccion){

    fun darGrupos(idProblematica: Int): List<JsonNode> {
        val conexiones: MutableList<JsonNode> = ArrayList<JsonNode>()
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

                    val id = "${it.idPadre}${it.id}".toInt()
                    data.set("id", IntNode(id))
                    data.set("source", IntNode(it.idPadre))
                    data.set("target", IntNode(it.id))
                    conexion.set("data", data)
                    conexiones.add(conexion)
                }

                grupo.set("data", data)
            }.toMutableList() + conexiones
    }

    fun agregarGrupo(idProblematica: Int, grupo: Grupo) = daoGrupo.agregarGrupo(idProblematica, grupo)

    fun actualizarNombreYPadreGrupo(grupo: Grupo): Boolean = daoGrupo.actualizarNombreYPadreGrupo(grupo)

    fun darGruposConReaccionDeUsuario(idProblematica: Int, email: String): MutableList<Grupo> {
        val grupos = daoGrupo.darGrupos(idProblematica)
        val reaccionOptional = daoReaccion.darReaccionEnGrupoPorUsuario(idProblematica, email)
        reaccionOptional.ifPresent {reaccion ->
            val grupo = grupos.find { it.id == reaccion.idGrupo }
            grupo!!.reaccion = reaccion.valor
            grupo!!.cantidad = 1
        }
        return grupos
    }

    fun darGruposConReacciones(idProblematica: Int) = daoGrupo.darGruposConReacciones(idProblematica)

    fun darGrupoConReaccion(idProblematica: Int, idPersonaProblematica: String): Grupo? {
        val optionalGrupo = daoGrupo.darGrupoConReaccion(idProblematica, idPersonaProblematica)
        return if (optionalGrupo.isPresent) optionalGrupo.get() else null
    }

    /**
     * Lanza una excepción si la lista esta vacia.
     */
    fun eliminarGrupos(idsGrupos: List<Int>, idSala: Int) = if (idsGrupos.isEmpty()) false else daoGrupo.eliminarGrupos(idsGrupos, idSala)

    /**
     * Lanza una excepción si la lista esta vacia.
     */
    fun eliminarConexiones(idsGrupos: List<Int>) = if(idsGrupos.isEmpty()) false else daoGrupo.eliminarConexiones(idsGrupos)
}
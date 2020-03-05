package usecase

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.*
import dao.DaoGrupo
import dao.DaoReaccion
import entity.Grupo

class GrupoUseCase(val daoGrupo: DaoGrupo, val daoReaccion: DaoReaccion, val nodoUseCase: NodoUseCase){

    fun darGrupos(idProblematica: Int): List<JsonNode> {
        val grupos: List<Grupo> =  daoGrupo.darGrupos(idProblematica)
        val gruposJson: List<JsonNode> = grupos.map {
            hashMapOf("id" to it.id,
                    "parent" to null,
                    "nombre" to it.nombre,
                    "esGrupo" to true)
        }.map { ObjectMapper().valueToTree<JsonNode>(hashMapOf("data" to it)) }

        val conexionesJson: List<JsonNode> = grupos.filter { it.idPadre != null } //TODO: Se puede reemplazar por una busqueda en la tabla Relación.
                .map {
                    val data = hashMapOf("id" to "${it.idPadre}${it.id}",
                            "source" to it.idPadre,
                            "target" to it.id)

                    hashMapOf("data" to data)
                }.map { ObjectMapper().valueToTree<JsonNode>(it) }
        return gruposJson + conexionesJson
    }

    fun agregarGrupo(idProblematica: Int, grupo: Grupo) = daoGrupo.agregarGrupo(idProblematica, grupo)

    fun actualizarNombre(grupo: Grupo): Boolean = daoGrupo.actualizarNombre(grupo)

    fun darGruposConReaccionDeUsuario(idProblematica: Int, email: String): List<JsonNode> {
        val grupos = darGrupos(idProblematica)
        val reacciones = daoReaccion.darReaccionesPorUsuario(idProblematica, email)
        val nodosJson = nodoUseCase.darNodosPorProblematica(idProblematica)

        reacciones.forEach { reacccion ->
            val grupo = grupos.find {it.get("data").get("id").asInt() == reacccion.idGrupo}
            (grupo?.get("data") as ObjectNode).set<JsonNode>("reaccion", IntNode(reacccion.valor))
        }

        return grupos + nodosJson
    }

    fun darGruposConReacciones(idProblematica: Int): List<Any> {
        val nodosJson = nodoUseCase.darNodosPorProblematica(idProblematica)
        val gruposConReacciones = daoGrupo.darGruposConReacciones(idProblematica).map { hashMapOf<String, Any>("data" to it) }
        return gruposConReacciones + nodosJson
    }

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

    fun eliminarGrupo(idGrupo: Int) = daoGrupo.eliminarGrupo(idGrupo)
}
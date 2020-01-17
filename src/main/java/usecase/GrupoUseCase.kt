package usecase

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.*
import dao.DaoGrupo
import dao.DaoReaccion
import entity.Grupo

class GrupoUseCase(val daoGrupo: DaoGrupo, val daoReaccion: DaoReaccion, val nodoUseCase: NodoUseCase){

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

    fun darGruposConReaccionDeUsuario(idProblematica: Int, email: String): List<JsonNode> {
            /*MutableList<Grupo> {*/
        val grupos = darGrupos(idProblematica)
        val reacciones = daoReaccion.darReaccionesPorUsuario(idProblematica, email)
        val nodosJson = nodoUseCase.darNodosPorProblematica(idProblematica);

        reacciones.forEach { reacccion ->
            val grupo = grupos.find {it.get("data").get("id").asInt() == reacccion.idGrupo}
            (grupo?.get("data") as ObjectNode).set("reaccion", IntNode(reacccion.valor))
        }

/*        reaccionOptional.ifPresent {reaccion ->
            val grupo = grupos.find { it.id == reaccion.idGrupo }
            grupo!!.reaccion = reaccion.valor
            grupo!!.cantidad = 1
        }*/
        return grupos + nodosJson
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

    private fun jsonNodeAObjeto(json: JsonNode){

    }

    private fun objetoAJsonNode(objeto: Any){

    }

    /*fun cosas(idProblematica: Int): List<JsonNode> {
        val grupos =  daoGrupo.darGrupos(idProblematica)
        val gruposJson: List<JsonNode> = grupos.map {
                    val data = hashMapOf("id" to it.id,
                            "parent" to null,
                            "nombre" to it.nombre,
                            "esGrupo" to true)
                    return ObjectMapper().valueToTree<JsonNode>(hashMapOf("data" to data))
                }//.map { ObjectMapper().valueToTree<JsonNode>(hashMapOf("data" to it)) }

        val conexionesJson = grupos.filter { it.idPadre != null }
            .map {
                val data = hashMapOf("id" to "${it.idPadre}${it.id}",
                        "source" to it.idPadre,
                        "target" to it.id)

                val conexion = hashMapOf("data" to data)
                ObjectMapper().valueToTree<JsonNode>(conexion)
            }
        return gruposJson + conexionesJson
    }*/

}
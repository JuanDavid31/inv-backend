package usecase

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import dao.DaoNodo
import dao.DaoRelacion
import entity.Mensaje
import entity.Error
import entity.Nodo

class NodoUseCase(val daoNodo: DaoNodo, val daoRelacion: DaoRelacion) {

    fun apadrinar(id: Int, idPadre: Int): Any {
        val apadrinado = daoRelacion.agregarNodoANodo(id, idPadre, 1);
        return if (apadrinado) Mensaje("Conexión exitosa") else Error(arrayOf("El nodo no existe."))
    }

    fun desApadrinar(id: Int, idPadre: Int): Any {
        val conexionesEliminadas = daoRelacion.eliminarNodoANodo(id, idPadre, 1)
        return if(conexionesEliminadas) Mensaje("Conexiones eliminadas exitosamente") else Error(arrayOf("El nodo no existe o no tiene conexiones."))
    }

    fun darNodosPorProblematica(idProblematica: Int): List<JsonNode> {
        val nodosJson = daoNodo.darNodosPorProblematica(idProblematica)
                .map {
                    if (it.idPadre == 1) {
                        it.idPadre = 0
                    }
                    it
                }
                .map {
                    val data = hashMapOf("id" to it.id,
                            "nombre" to it.nombre,
                            "parent" to it.idGrupo,
                            "urlFoto" to it.urlFoto,
                            "nombreCreador" to it.nombreCreador)

                    hashMapOf("data" to data)
                }.map { ObjectMapper().valueToTree<JsonNode>(it) }

        val conexionesJson: List<JsonNode> = daoNodo.darConexionesSegundaFase(idProblematica)
                .filter { it.idPadre != null }
                .map {
                    val data = hashMapOf("id" to "${it.idPadre}${it.id}",
                            "source" to it.idPadre,
                            "target" to it.id)

                    hashMapOf("data" to data)
                }.map { ObjectMapper().valueToTree<JsonNode>(it) }
        return nodosJson + conexionesJson
    }

}
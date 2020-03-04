package usecase

import dao.DaoRelacion
import entity.Relacion

class RelacionUseCase (val daoRelacion: DaoRelacion){

    fun conectarNodos(relacion: Relacion) = daoRelacion.agregarNodoANodo(relacion.idNodo, relacion.idNodoPadre, 2)

    fun conectarNodoYGrupo(relacion: Relacion)= daoRelacion.agregarNodoAGrupo(relacion)

    fun desconectarNodoYGrupo(relacion: Relacion) = daoRelacion.eliminarNodoAGrupo(relacion)

    fun conectarGrupos(relacion: Relacion) = daoRelacion.agregarGrupoAGrupo(relacion)

    fun desconectarGrupos(relacion: Relacion) = daoRelacion.eliminarGrupoAGrupo(relacion)

    fun desconectarNodos(id: Int, idPadre: Int) = daoRelacion.eliminarNodoANodo(id, idPadre, 2)

}
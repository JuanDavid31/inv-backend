package usecase

import dao.DaoRelacion
import entity.Relacion

class RelacionUseCase (val daoRelacion: DaoRelacion){

    fun conectarNodos(relacion: Relacion){
        daoRelacion.agregarNodoANodo(relacion.id, relacion.idNodoPadre, 2)
    }

    fun conectarNodoYGrupo(relacion: Relacion)= daoRelacion.agregarNodoAGrupo(relacion)


    fun desconectarNodoYGrupo(relacion: Relacion) = daoRelacion.eliminarNodoAGrupo(relacion)

}
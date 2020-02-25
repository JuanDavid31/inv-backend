package usecase

import dao.DaoRelacion
import entity.Relacion

class RelacionUseCase (val daoRelacion: DaoRelacion){

    fun conectarNodos(relacion: Relacion){
        daoRelacion.conectarNodos(relacion.id, relacion.idNodoPadre, 2)
    }
}
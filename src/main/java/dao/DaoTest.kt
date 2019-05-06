package dao

import entity.Grupo
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.result.ResultIterable

class DaoTest(val jdbi : Jdbi){


    fun testttt():Boolean{
        val withHandle = jdbi.withHandle<Boolean, Exception> {
            /*it.createQuery("").mapToBean(Grupo.class))

        }*/

        return false
    }
}
package dao

import entity.Grupo
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.result.ResultIterable

class DaoTest(val jdbi : Jdbi){


    fun test1() = jdbi.withHandle<Grupo, Exception> { it.createQuery("").mapToBean(Grupo::class.java).findOnly()}

    fun test2() = jdbi.withHandle<Boolean, Exception>{it.createUpdate("").execute() > 0}
}
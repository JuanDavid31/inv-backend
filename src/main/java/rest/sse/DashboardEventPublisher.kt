package rest.sse

import com.fasterxml.jackson.databind.ObjectMapper
import util.SingletonUtils

class DashboardEventPublisher : EventPublisher() {

    fun difundirAParticipantes(datos: Map<String, Any>, participantes: List<String>){
        val idSesion = SingletonUtils.darIdSesion() //Si este metodo se realiza en otro hilo la llamada debe moverse.
        publishers.filterKeys { it !== idSesion}
                //.flatMapTo(arrayListOf<SessionWrapper>()){ (_, sesionWrapper) -> arrayListOf(sesionWrapper) }
                .map { (_, sessionWrapper) -> sessionWrapper}
                .filter { sessionWrapper -> participantes.contains(sessionWrapper.emailUsuario) }
                .forEach { sesionWrapper -> sesionWrapper.eventSource.emit(mapper.writeValueAsString(datos))}
    }
}
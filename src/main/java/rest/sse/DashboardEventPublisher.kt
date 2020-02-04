package rest.sse

class DashboardEventPublisher : EventPublisher() {

    /**
     * Difunde el evento a todos los participantes que pertenezcan a la problematica
     * y se encuentre conectados, el usuario que inicio la acción de difusión es omitido de esta
     * misma.
     * @param idSesion Sesion de la persona que disparo la difusión
     * @param datos JSON-like que contiene los datos que se enviaran a cada participante
     * @param participantes lista de todos los participantes que participan en la problematica.
     */
    fun difundirAvanceFaseAParticipantesMenosA(idSesion: String, datos: Map<String, Any>, participantes: List<String>){
        publishers.filterKeys { it !== idSesion}
                .map { (_, sessionWrapper) -> sessionWrapper}
                .filter { sessionWrapper -> participantes.contains(sessionWrapper.emailUsuario) }
                .forEach { sesionWrapper -> sesionWrapper.eventSource.emit(mapper.writeValueAsString(datos))}
    }
}
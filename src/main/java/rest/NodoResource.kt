package rest

import usecase.FotoUseCase
import usecase.NodoUseCase

import javax.validation.constraints.NotNull
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/nodos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class NodoResource(private val fotoUseCase: FotoUseCase, private val nodoUseCase: NodoUseCase) {

    @Path("/{idNodo}")
    @PUT
    fun editarNodo(@PathParam("idNodo") idNodo: Int,
                   @QueryParam("apadrinar") @NotNull apadrinar: Boolean,
                   @QueryParam("id-padre") @NotNull idPadre: Int): Response {
        val resultado by lazy {
            if (apadrinar) {
                nodoUseCase.apadrinar(idNodo, idPadre)
            } else {
                nodoUseCase.desApadrinar(idNodo, idPadre)
            }
        }

        return when(resultado){
            is Error -> Response.status(Response.Status.BAD_REQUEST).entity(resultado).build()
            else -> Response.ok(resultado).build()
        }
    }

    @Path("/{idNodo}")
    @DELETE
    fun eliminarNodo(@PathParam("idNodo") idNodo: Int): Response? {
        val resultado = fotoUseCase.eliminarNodoYFoto(idNodo);
        return when(resultado){
            is Error -> Response.status(Response.Status.BAD_REQUEST).entity(resultado).build()
            else -> Response.ok(resultado).build()
        }
    }
}
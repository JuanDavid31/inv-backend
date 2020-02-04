package util

import entity.Nodo
import io.dropwizard.lifecycle.Managed
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.S3Utilities
import software.amazon.awssdk.services.s3.model.*
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths

class S3Utils : Managed {

    val NOMBRE_BUCKET = "nodos"
    var clienteS3: S3Client? = null
    var bucketNodos: Bucket? = null

    @Throws(Exception::class)
    override fun start() {

        clienteS3 = S3Client.builder()
                .region(Region.US_EAST_2)
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build()

        val build = ListBucketsRequest.builder().build()
        val listBucketsResponse = clienteS3?.listBuckets(build)

        bucketNodos = listBucketsResponse?.buckets()?.get(0)
    }

    //TODO: Añadir manejo de errores para mostrar que no se pudieron cargar las imagenes.
    fun cargarImagen(nodo: Nodo, foto: InputStream, extension: String): String{

        val rutaArchivo = darRutaArchivo(nodo, extension)

        val builder = PutObjectRequest
                .builder()
                .acl(ObjectCannedACL.PUBLIC_READ)
                .bucket(NOMBRE_BUCKET)
                .key(rutaArchivo)
                .build()

        val archivoFoto = File(rutaArchivo)

        archivoFoto.parentFile.mkdirs()

        Files.copy(foto, Paths.get(rutaArchivo))

        val requestBody = RequestBody.fromFile(archivoFoto)
        val putObjectResponse = clienteS3?.putObject(builder, requestBody)

        val utilidadesS3 = S3Utilities
                .builder()
                .region(Region.US_EAST_2)
                .build()

        val urlRequest = GetUrlRequest
                .builder()
                .bucket(NOMBRE_BUCKET)
                .key(rutaArchivo)
                .build()

        archivoFoto.delete()
        return utilidadesS3.getUrl(urlRequest).toString()
    }

    fun eliminarImagen(nodo: Nodo): Nodo{

        val rutaArchivo = darRutaArchivo(nodo)

        val deleteObjectRequest = DeleteObjectRequest
                .builder()
                .bucket(NOMBRE_BUCKET)
                .key(rutaArchivo)
                .build()

        clienteS3?.deleteObject(deleteObjectRequest)
        return nodo
    }

    private fun darRutaArchivo(nodo: Nodo, extension: String) = "${nodo.idProblematica}/${nodo.id}.${extension}"

    private fun darRutaArchivo(nodo: Nodo) = "${nodo.idProblematica}/${nodo.id}.${nodo.urlFoto.split(".").last()}"

    @Throws(Exception::class)
    override fun stop() {
        clienteS3?.close()
    }
}
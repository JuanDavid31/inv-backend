package util

import io.dropwizard.lifecycle.Managed
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.ListBucketsRequest

class S3Utils : Managed {

    var s3Client: S3Client? = null

    @Throws(Exception::class)
    override fun start() {

        s3Client = S3Client.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build()

        val build = ListBucketsRequest.builder().build()
        val listBuckets = s3Client?.listBuckets(build)
        println("Imprimiendo buckets")
        listBuckets?.buckets()?.stream()?.forEach { println(it)}

    }

    @Throws(Exception::class)
    override fun stop() {
        s3Client?.close()
    }
}
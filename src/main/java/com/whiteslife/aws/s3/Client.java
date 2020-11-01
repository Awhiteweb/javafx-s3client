package com.whiteslife.aws.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;

import java.util.List;
import java.util.stream.Collectors;

public class Client {
    private AmazonS3Client client;

    public Client() {

    }

    public List<Bucket> listBuckets() {
        return this.client.listBuckets();
    }

    public S3ObjectList listObjects( String bucket ) {
        return this.listObjects( bucket, 25 );
    }

    public S3ObjectList listObjects( String bucket, String lastKey ) {
        return this.listObjects( bucket, 25, lastKey );
    }

    public S3ObjectList listObjects( String bucket, int maxKeys ) {
        return this.listObjects( new ListObjectsV2Request()
                .withBucketName( bucket )
                .withMaxKeys( maxKeys ) );

    }

    public S3ObjectList listObjects( String bucket, int maxKeys, String lastKey ) {
        return this.listObjects( new ListObjectsV2Request()
                .withBucketName( bucket )
                .withMaxKeys( maxKeys )
                .withStartAfter( lastKey ) );
    }

    private S3ObjectList listObjects( ListObjectsV2Request request ) {
        ListObjectsV2Result result = this.client.listObjectsV2( request );
        return new S3ObjectList( result.getObjectSummaries().stream().map( s -> s.getKey() ).collect( Collectors.toList() ), result.isTruncated() );
    }
}

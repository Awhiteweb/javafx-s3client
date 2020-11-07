package com.whiteslife.aws.s3;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import io.reactivex.rxjava3.core.Observable;

import java.util.stream.Stream;

public class ObservableS3Client implements Client {
    private final AmazonS3 client;

    private ObservableS3Client( Regions region ) {
        this.client = AmazonS3Client.builder().withRegion( region ).build();
    }

    public static ObservableS3Client instance( Regions region ) {
        return Singleton.getInstance( region );
    }
    private static class Singleton {
        private static ObservableS3Client instance;
        private static ObservableS3Client getInstance( Regions region) {
            if( instance == null ) {
                instance = new ObservableS3Client(region);
            }
            return instance;
        }
    }

    @Override
    public Observable<Void> observeAbortMultipartUpload( String bucket, String key, String uploadId ) {
        AbortMultipartUploadRequest request = new AbortMultipartUploadRequest( bucket, key, uploadId );
        return Observable.defer( () -> {
            this.client.abortMultipartUpload( request );
            return Observable.empty();
        });
    }

    @Override
    public Observable<Void> observeCompleteMultipartUpload( String bucket, String key, String uploadId ) {
        this.client.completeMultipartUpload( new CompleteMultipartUploadRequest() );
        return Observable.empty();
    }

    @Override
    public Observable<Void> observeInitiateMultipartUpload( String bucket, String key ) {
        this.client.initiateMultipartUpload( new InitiateMultipartUploadRequest( "", "" ) );
        return Observable.empty();
    }

    @Override
    public Observable<Stream<String>> observeBucketStream() {
        return Observable.just( this.client.listBuckets().stream().map( Bucket::getName ) );
    }

    @Override
    public Observable<MultipartUploadListing> observeMultipartUploads( String bucket) {
        ListMultipartUploadsRequest request = new ListMultipartUploadsRequest( bucket );
        return this.observeMultipartUploads( request );
    }

    @Override
    public Observable<MultipartUploadListing> observeMultipartUploads( String bucket, String keyMarker ) {
        ListMultipartUploadsRequest request = new ListMultipartUploadsRequest( bucket ).withKeyMarker( keyMarker );
        return this.observeMultipartUploads( request );
    }

    @Override
    public Observable<S3ObjectInputStream> observeObjectContent( String bucket, String key ) {
        GetObjectRequest objectRequest = new GetObjectRequest(bucket, key);
        return Observable.just( this.client.getObject( objectRequest ).getObjectContent() );
    }

    @Override
    public Observable<S3ObjectList> observeObjects( String bucket ) {
        return this.observeObjects( bucket, 25 );
    }

    @Override
    public Observable<S3ObjectList> observeObjects( String bucket, String startAfter ) {
        return this.observeObjects( bucket, 25, startAfter );
    }

    @Override
    public Observable<S3ObjectList> observeObjects( String bucket, int maxKeys ) {
        return this.observeObjects( new ListObjectsV2Request()
                .withBucketName( bucket )
                .withMaxKeys( maxKeys ) );

    }

    @Override
    public Observable<S3ObjectList> observeObjects( String bucket, int maxKeys, String startAfter ) {
        return this.observeObjects( new ListObjectsV2Request()
                .withBucketName( bucket )
                .withMaxKeys( maxKeys )
                .withStartAfter( startAfter ) );
    }

    private Observable<S3ObjectList> observeObjects( ListObjectsV2Request request ) {
        ListObjectsV2Result result = this.client.listObjectsV2( request );
        int total = result.getObjectSummaries().size();
        String startAfter = total > 0 ? result.getObjectSummaries().get( total - 1 ).getKey() : null;
        Stream<String> keyStream = result.getObjectSummaries().stream().map( S3ObjectSummary::getKey );
        return Observable.just(new S3ObjectList( request.getBucketName(), keyStream, startAfter, result.isTruncated() ));
    }

    private Observable<MultipartUploadListing> observeMultipartUploads(ListMultipartUploadsRequest request) {
        return Observable.just(this.client.listMultipartUploads( request ));
    }
}

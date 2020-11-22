package com.whiteslife.aws.s3;

import com.amazonaws.services.s3.model.MultipartUploadListing;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import io.reactivex.rxjava3.core.Observable;

import java.util.stream.Stream;

public interface Client {
    Observable<Void> observeAbortMultipartUpload( String bucket, String key, String uploadId );

    Observable<Void> observeCompleteMultipartUpload( String bucket, String key, String uploadId );

    Observable<Void> observeInitiateMultipartUpload( String bucket, String key );

    Observable<Stream<String>> observeBucketStream();

    Observable<MultipartUploadListing> observeMultipartUploads( String bucket );

    Observable<MultipartUploadListing> observeMultipartUploads( String bucket, String keyMarker );

    Observable<S3ObjectInputStream> observeObjectContent( String bucket, String key );

    Observable<S3ObjectList> observeObjects( String bucket );

    Observable<S3ObjectList> observeObjects( String bucket, String startAfter );

    Observable<S3ObjectList> observeObjects( String bucket, int maxKeys );

    Observable<S3ObjectList> observeObjects( String bucket, int maxKeys, String startAfter );

    Observable<S3PartList> observeMultipartUploadParts( String bucket, String key, String uploadId );

    Observable<S3PartList> observeMultipartUploadParts( String bucket, String key, String uploadId, int partNumberMarker );
}

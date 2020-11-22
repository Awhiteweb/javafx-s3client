package com.whiteslife.aws.s3;

import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.whiteslife.view.models.ListModel;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Function;

import java.util.stream.Stream;

public interface Repository {
    Observable<Stream<String>> observeBucketStream();

    Observable<Stream<ListModel>> observeKeyStream();

    void retrieveBucketStream();

    void retrieveObjectKeys( String bucket );

    void retrieveMultipartUploadList( String bucket );

    <R> Observable<R> observeObjectContent(String bucket, String key, Function<S3ObjectInputStream, R> fn);

    void disposeAll();
}

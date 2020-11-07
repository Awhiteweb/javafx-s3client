package com.whiteslife.aws.s3;

import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.whiteslife.mocks.MockResponse;
import com.whiteslife.reactivx.extensions.JavaFxScheduler;
import com.whiteslife.rx.observables.RecFunction;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.ReplaySubject;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class S3Repository implements Repository {
    private final ObservableS3Client observableS3Client;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private BehaviorSubject<List<String>> keyStreamSubject = BehaviorSubject.createDefault( Collections.emptyList() );
    private ReplaySubject<List<String>> bucketStreamSubject = ReplaySubject.createWithSize( 1 );

    public S3Repository( ObservableS3Client observableS3Client ) {
        this.observableS3Client = observableS3Client;
    }

    @Override
    public Observable<Stream<String>> observeBucketStream() {
        return this.bucketStreamSubject.observeOn( JavaFxScheduler.platform() )
                .map( l -> l.stream() );
    }

    @Override
    public Observable<Stream<String>> observeKeyStream() {
        return this.keyStreamSubject.observeOn( JavaFxScheduler.platform() )
                .map( l -> l.stream() );
    }

    @Override
    public void retrieveBucketStream() {
        this.compositeDisposable.add( Observable.defer( () -> this.observableS3Client.observeBucketStream() )
                .subscribeOn( Schedulers.computation() )
                .doOnNext( r -> this.bucketStreamSubject.onNext( r.collect( Collectors.toList() ) ) )
                .doOnError( Throwable::printStackTrace )
                .subscribe() );
    }

    @Override
    public void retrieveObjectKeys( String bucket ) {
        this.compositeDisposable.add( Observable.defer( () -> this.observableS3Client.observeObjects( bucket ) )
                .subscribeOn( Schedulers.computation() )
                .concatMap( r -> this.responseIterator( r, this.observableS3Client::observeObjects ) )
                .subscribe() );
    }

    @Override
    public void retrieveMultipartUploadList( String bucket ) {
        this.compositeDisposable.add( Observable.defer( () -> this.observableS3Client.observeMultipartUploads( bucket ) )
                .subscribeOn( Schedulers.computation() )
                .doOnNext( r -> {
                    List<String> l = r.getMultipartUploads()
                            .stream()
                            .map( u -> String.format( "%s (%s)", u.getKey(), u.getUploadId() ) )
                            .collect( Collectors.toList() );
                    this.keyStreamSubject.onNext( l );
                } )
                .subscribe() );
    }


    @Override
    public <R> Observable<R> observeObjectContent( String bucket, String key, Function<S3ObjectInputStream, R> fn ) {
        return Observable.defer( () -> this.observableS3Client.observeObjectContent( bucket, key ) )
                .subscribeOn( Schedulers.computation() )
                .map( fn );
    }

    @Override
    public void disposeAll() {
        this.compositeDisposable.dispose();
    }

    private Observable<MockResponse> responseIterator( S3ObjectList currentResponse, RecFunction<String, String, Observable<S3ObjectList>> fn ) throws Throwable {
        if( currentResponse.getKeys() != null ) {
            List<String> keyList = currentResponse.getKeys().collect( Collectors.toList() );
            this.keyStreamSubject.onNext( keyList );
        }
        if( currentResponse.isTruncated() ) {
            return fn.apply( currentResponse.getBucket(), currentResponse.getStartAfter() )
                    .concatMap( r -> responseIterator( r, fn ) );
        }
        return Observable.empty();
    }

}

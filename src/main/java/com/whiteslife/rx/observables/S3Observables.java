package com.whiteslife.rx.observables;

import com.whiteslife.aws.s3.Client;
import com.whiteslife.aws.s3.S3ObjectList;
import com.whiteslife.reactivx.extensions.JavaFxScheduler;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.MaybeSource;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Supplier;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class S3Observables {
    private final Client client;

    public S3Observables(Client client) {
        this.client = client;
    }

    public void listBucketsAsync( ObservableList<String> observableList ) {
        Observable.fromCallable( this.client::listBuckets )
                .subscribeOn( Schedulers.io() )
                .map( buckets -> buckets.stream().map( b -> b.getName() ) )
                .observeOn( JavaFxScheduler.platform() )
                .forEach( names -> {
                    observableList.clear();
                    observableList.addAll( names.collect( Collectors.toList() ) );
                } );
    }

    public void listObjectsAsync( String bucket, ObservableList<String> observableList ) {
        Observable.fromCallable( () -> this.client.listObjects(bucket) )
                .subscribeOn( Schedulers.io() )
                .concatMapMaybe( (io.reactivex.rxjava3.functions.Function<S3ObjectList, MaybeSource<List<String>>>) s3ObjectList -> {
                    if(s3ObjectList.isTruncated()) {
                        return Maybe.just( s3ObjectList.getKeys() );
                    }
                    return Maybe.empty();
                } )
                .observeOn( JavaFxScheduler.platform() )
                .forEach( keys -> observableList.addAll( keys ) );
    }

//    public static <T> Observable<T> expandObservable(
//            final T initialValue, final Function<T, T> expandFunc) {
//        return Observable.just(initialValue)
//                .concatWith( Observable.defer( (Supplier<Observable<T>>) () -> expandObservable( expandFunc.apply( initialValue ), expandFunc ) );
//    }

}

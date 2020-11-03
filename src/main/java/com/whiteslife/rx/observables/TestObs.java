package com.whiteslife.rx.observables;

import com.whiteslife.mocks.MockApi;
import com.whiteslife.mocks.MockResponse;
import com.whiteslife.reactivx.extensions.JavaFxScheduler;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.collections.ObservableList;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class TestObs {
    private static final List<String> staticNameList = new LinkedList<>();

    public void expandTest( ObservableList<String> observableList ) throws Throwable {
        staticNameList.clear();
        this.observableApi( null )
                .subscribeOn( Schedulers.computation() )
                .concatMap( r -> expandObjectKeys( r, this::observableApi ) )
//                .doOnEach( x -> System.out.println( "update" ) )
                .map( x -> {
                    System.out.printf( "number of items: %s", x.getNames().size() );
                    return x.getNames();
                } )
                .observeOn( JavaFxScheduler.platform() )
                .doOnComplete( () -> {
                    System.out.println( "Completed requests" );
                    observableList.addAll( staticNameList );
                } )
                .forEach( x -> System.out.printf( "number of items in forEach %s%n", x.size() ) );
    }

    public Observable<MockResponse> observableApi( String startAfter ) {
        return Observable.just( MockApi.instance().request( startAfter ) );
    }

    public static Observable<MockResponse> expandObjectKeys( MockResponse current, Function<String, Observable<MockResponse>> fn ) throws Throwable {
        staticNameList.addAll( current.getNames() );
        if( current.isTruncated() ) {
            System.out.printf("request is truncated last item: %s%n", current.getStartAfter() );
            return fn.apply( current.getStartAfter() )
                    .concatMap( r -> Observable.defer( () -> expandObjectKeys( r, fn ) ) );
        }
        System.out.println( "no more items" );
        return Observable.empty();
    }
}

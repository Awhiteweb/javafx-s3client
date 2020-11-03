package com.whiteslife.rx.observables;

import com.whiteslife.mocks.MockApi;
import com.whiteslife.mocks.MockResponse;
import com.whiteslife.reactivx.extensions.JavaFxScheduler;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

import java.util.Collections;
import java.util.List;

public class TestObservableTwo {
    private BehaviorSubject<List<String>> nameListSubject = BehaviorSubject.createDefault( Collections.emptyList() );
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public void observeNameList( Consumer<List<String>> consumer, Consumer<Throwable> onError ) {
        compositeDisposable.add( nameListSubject.observeOn( JavaFxScheduler.platform() ).subscribe( consumer, onError ) );
    }

    public void disposeAll() {
        this.compositeDisposable.dispose();
    }

    public void expandTest() throws Throwable {
        Disposable d = this.observableApi( null )
                .subscribeOn( Schedulers.computation() )
                .concatMap( r -> this.responseIterator( r, this::observableApi ) )
                .subscribe();
        this.compositeDisposable.add( d );
    }

    private Observable<MockResponse> responseIterator( MockResponse currentResponse, Function<String, Observable<MockResponse>> fn ) throws Throwable {
        if( currentResponse.getNames() != null ) {
            this.nameListSubject.onNext( currentResponse.getNames() );
        }
        if( currentResponse.isTruncated() ) {
            return fn.apply( currentResponse.getStartAfter() )
                    .concatMap( r -> responseIterator( r, fn ) );
        }
        return Observable.empty();
    }

    public Observable<MockResponse> observableApi( String startAfter ) {
        return Observable.defer( () -> MockApi.instance().request( startAfter ) );
    }
}

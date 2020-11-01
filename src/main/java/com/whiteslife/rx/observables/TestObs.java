package com.whiteslife.rx.observables;

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

    public void createTest( ObservableList<String> observableList ) {
        this.begin()
                .subscribeOn( Schedulers.computation() )
                .map( x -> x.names )
                .observeOn( JavaFxScheduler.platform() )
                .forEach( observableList::addAll );
    }

    public void expandTest( ObservableList<String> observableList ) throws Throwable {
        staticNameList.clear();
        this.observableApi( null )
                .subscribeOn( Schedulers.computation() )
                .concatMap( r -> expandObjectKeys( r, this::observableApi ) )
//                .doOnEach( x -> System.out.println( "update" ) )
                .map( x -> {
                    System.out.printf( "number of items: %s", x.names.size() );
                    return x.names;
                } )
                .observeOn( JavaFxScheduler.platform() )
                .doOnComplete( () -> {
                    System.out.println( "Completed requests" );
                    observableList.addAll( staticNameList );
                } )
                .forEach( x -> System.out.printf( "number of items in forEach %s%n", x.size() ) );
    }

    private Observable<TestObj> begin() {
        return Observable.range( 0, 4 )
                .map( i -> nameList.get( i * 4 ) )
                .map( this::apiMock );
    }

    public Observable<TestObj> observableApi( String startAfter ) {
        return Observable.just( this.apiMock( startAfter ) );
    }


    private TestObj apiMock( String startAfter ) {
        long sleepDuration = (long) ( Math.random() * 1000 );
        try {
            System.out.printf( "requesting from %s%n", startAfter );
            Thread.sleep( sleepDuration );
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
        int max = 5;
        int last = startAfter == null ? max : nameList.indexOf( startAfter ) + max;
        System.out.printf( "last item is %s%n", last );
        if( last > nameList.size() ) {
            return new TestObj( null, nameList.subList( last - max, nameList.size() - 1 ), false );
        }
        return new TestObj( nameList.get( last - 1 ), nameList.subList( last - max, last ), true );
    }

    private final List<String> nameList = Arrays.asList(
            "Ali Haley",
            "Kevin Potter",
            "Maiya Best",
            "Jamie-Lee Bullock",
            "Harold Pruitt",
            "Judah Ratliff",
            "Elana Liu",
            "Ellesse Crouch",
            "Gregory Schultz",
            "Yvette Sierra",
            "Jaeden Andrew",
            "Saffa Quinn",
            "Izabelle Villa",
            "Jose Thorne",
            "Shani Seymour",
            "Ashleigh Prosser",
            "Jarrad Thomas",
            "Sion Kirkland",
            "Roxy Mccaffrey",
            "Izabelle Prosser",
            "Jose Noble",
            "Shani Thorne",
            "Ashleigh Kirkland",
            "Jarrad Mccaffrey",
            "Sion Noble",
            "Roxy Ratliff",
            "Aneesha Noble"
    );

    private static class TestObj {
        public String startAfter;
        public List<String> names;
        public boolean isTruncated;

        public TestObj( String startAfter, List<String> names, boolean isTruncated ) {
            this.startAfter = startAfter;
            this.names = names;
            this.isTruncated = isTruncated;
        }
    }

    public static Observable<TestObj> expandObjectKeys( TestObj current, Function<String, Observable<TestObj>> fn ) throws Throwable {
        staticNameList.addAll( current.names );
        if( current.isTruncated ) {
            System.out.printf("request is truncated last item: %s%n", current.startAfter );
            return fn.apply( current.startAfter )
                    .concatMap( r -> Observable.defer( () -> expandObjectKeys( r, fn ) ) );
        }
        System.out.println( "no more items" );
        return Observable.empty();
    }
}

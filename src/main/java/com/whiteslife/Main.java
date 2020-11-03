package com.whiteslife;

import com.whiteslife.rx.observables.TestObservableTwo;
import io.reactivex.rxjava3.core.Observable;

import java.util.List;

public class Main {
    public static void main(String[] args) {
//        try {
//            TestObservableTwo tester = new TestObservableTwo();
//            tester.observeNameList( c -> c.forEach( System.out::println ), Throwable::printStackTrace );
//            System.out.println( "started observing" );
////            pause( 2000 );
//            tester.expandTest();
////            pause( 2000 );
//            System.out.println( "disposing observables" );
//            tester.disposeAll();
//        }
//        catch( Throwable throwable ) {
//            throwable.printStackTrace();
//        }
//        pause( 2000 );
        Root.launch( args );
    }

    public static void pause(int ms) {
        try {
            Thread.sleep( ms );
        }
        catch(InterruptedException e) {
            e.printStackTrace();
        }
    }
}

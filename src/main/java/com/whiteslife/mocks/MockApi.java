package com.whiteslife.mocks;

import com.whiteslife.rx.observables.TestObs;

import java.util.Arrays;
import java.util.List;

public class MockApi {
    private MockApi() {
    }

    public static MockApi instance() {
        return ThreadSafeInitialiser.instance;
    }
    private static class ThreadSafeInitialiser {
        private static final MockApi instance = new MockApi();
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

    public MockResponse request(String startAfter) {
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
            return new MockResponse( null, nameList.subList( last - max, nameList.size() - 1 ), false );
        }
        return new MockResponse( nameList.get( last - 1 ), nameList.subList( last - max, last ), true );
    }
}

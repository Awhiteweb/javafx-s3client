package com.whiteslife.rx.observables;

@FunctionalInterface
public interface RecFunction<T1, R> {
    R apply( T1 t1 );
}

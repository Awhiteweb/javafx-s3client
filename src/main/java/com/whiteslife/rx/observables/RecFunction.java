package com.whiteslife.rx.observables;

@FunctionalInterface
public interface RecFunction<T1, T2, R> {
    R apply( T1 t1, T2 t2 );
}

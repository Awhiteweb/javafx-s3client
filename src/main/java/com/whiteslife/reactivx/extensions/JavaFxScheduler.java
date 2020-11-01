package com.whiteslife.reactivx.extensions;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.util.Duration;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class JavaFxScheduler extends Scheduler {
    private static final JavaFxScheduler INSTANCE = new JavaFxScheduler();

    JavaFxScheduler() {
    }

    public static JavaFxScheduler platform() {
        return INSTANCE;
    }

    private static void assertThatTheDelayIsValidForTheJavaFxTimer( long delay ) {
        if( delay < 0L || delay > 2147483647L ) {
            throw new IllegalArgumentException( String.format( "The JavaFx timer only accepts non-negative delays up to %d milliseconds.", 2147483647 ) );
        }
    }

    @Override
    @NonNull
    public Worker createWorker() {
        return new JavaFxWorker();
    }

    private static class JavaFxWorker extends Worker implements Runnable {
        private final AtomicReference<QueuedRunnable> tail;
        private volatile QueuedRunnable head;

        private JavaFxWorker() {
            this.head = new QueuedRunnable( null );
            this.tail = new AtomicReference<>( this.head );
        }

        public void dispose() {
            this.tail.set( null );

            for( QueuedRunnable qr = this.head; qr != null; qr = qr.getAndSet( null ) ) {
                qr.dispose();
            }

        }

        public boolean isDisposed() {
            return this.tail.get() == null;
        }

        @Override
        @NonNull
        public Disposable schedule( @NonNull Runnable action ) {
            if( this.isDisposed() ) {
                return Disposable.disposed();
            } else {
                QueuedRunnable queuedRunnable = action instanceof QueuedRunnable ? (QueuedRunnable) action : new QueuedRunnable( action );

                QueuedRunnable tailPivot;
                do {
                    tailPivot = this.tail.get();
                } while( tailPivot != null && !tailPivot.compareAndSet( null, queuedRunnable ) );

                if( tailPivot == null ) {
                    queuedRunnable.dispose();
                } else {
                    this.tail.compareAndSet( tailPivot, queuedRunnable );
                    if( tailPivot == this.head ) {
                        if( Platform.isFxApplicationThread() ) {
                            this.run();
                        } else {
                            Platform.runLater( this );
                        }
                    }
                }

                return queuedRunnable;
            }
        }

        @Override
        @NonNull
        public Disposable schedule( @NonNull Runnable action, long delayTime, TimeUnit unit ) {
            long delay = Math.max( 0L, unit.toMillis( delayTime ) );
            assertThatTheDelayIsValidForTheJavaFxTimer( delay );
            QueuedRunnable queuedRunnable = new QueuedRunnable( action );
            if( delay == 0L ) {
                return this.schedule( queuedRunnable );
            } else {
                Timeline timer = new Timeline( new KeyFrame( Duration.millis( (double) delay ), ( event ) -> this.schedule( queuedRunnable ) ) );
                timer.play();
                return Disposable.fromRunnable( () -> {
                    queuedRunnable.dispose();
                    timer.stop();
                } );
            }
        }

        public void run() {
            for( QueuedRunnable qr = this.head.get(); qr != null; qr = qr.get() ) {
                qr.run();
                this.head = qr;
            }

        }

        private static class QueuedRunnable extends AtomicReference<QueuedRunnable> implements Disposable, Runnable {
            private volatile Runnable action;

            private QueuedRunnable( Runnable action ) {
                this.action = action;
            }

            public void dispose() {
                this.action = null;
            }

            public boolean isDisposed() {
                return this.action == null;
            }

            public void run() {
                Runnable action = this.action;
                if( action != null ) {
                    action.run();
                }

                this.action = null;
            }
        }
    }
}

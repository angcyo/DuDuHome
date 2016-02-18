package rx.ext;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import rx.Notification;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Contains a set of helper methods, used in the examples.
 */
public final class Helpers {

    public static <T> Subscription subscribePrint(Observable<T> observable,
                                                  String name) {
        return observable.subscribe(
                (v) -> System.out.println(Thread.currentThread().getName()
                        + "|" + name + " : " + v), (e) -> {
                    System.err.println("Error from " + name + ":");
                    System.err.println(e);
                }, () -> System.out.println(name + " ended!"));
    }

    /**
     * Subscribes to an observable, printing all its emissions.
     * Blocks until the observable calls onCompleted or onError.
     */
    public static <T> void blockingSubscribePrint(Observable<T> observable, String name) {
        CountDownLatch latch = new CountDownLatch(1);
        subscribePrint(observable.finallyDo(() -> latch.countDown()), name);
        try {
            latch.await();
        } catch (InterruptedException e) {
        }
    }

    public static <T> Action1<Notification<? super T>> debug(String description) {
        return debug(description, "");
    }

    public static <T> Action1<Notification<? super T>> debug(String description, String offset) {
        AtomicReference<String> nextOffset = new AtomicReference<String>(">");

        return (Notification<? super T> notification) -> {
            switch (notification.getKind()) {
                case OnNext:

                    System.out.println(
                            Thread.currentThread().getName() +
                                    "|" + description + ": " + offset +
                                    nextOffset.get() +
                                    notification.getValue()
                    );
                    break;
                case OnError:
                    System.err.println(Thread.currentThread().getName() +
                            "|" + description + ": " + offset +
                            nextOffset.get() + " X " + notification.getThrowable());
                    break;
                case OnCompleted:
                    System.out.println(Thread.currentThread().getName() +
                            "|" + description + ": " + offset +
                            nextOffset.get() + "|"
                    );
                    break;
                default:
                    break;
            }
            nextOffset.getAndSet("-" + nextOffset.get());
        };
    }

}
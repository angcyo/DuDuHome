package rx.ext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Scheduler;
import rx.Scheduler.Worker;
import rx.Subscriber;
import rx.functions.Action0;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

/**
 * Contains a set of methods for creating custom {@link Observable}s.
 */
public class CreateObservable {

    public static ConnectableObservable<String> from(final InputStream stream) {
        return from(new BufferedReader(new InputStreamReader(stream)));
    }

    public static Observable<String> from(final Reader reader) {
        return Observable.defer(() -> {
            return from(new BufferedReader(reader)).refCount();
        }).cache();
    }

    public static ConnectableObservable<String> from(final BufferedReader reader) {
        return Observable.create((Subscriber<? super String> subscriber) -> {
            try {
                String line;

                if (subscriber.isUnsubscribed()) {
                    return;
                }

                while (!subscriber.isUnsubscribed() && (line = reader.readLine()) != null) {
                    subscriber.onNext(line);
                }
            } catch (IOException e) {
                subscriber.onError(e);
            }

            if (!subscriber.isUnsubscribed()) {
                subscriber.onCompleted();
            }
        }).publish();
    }

    public static Observable<Long> interval(Long... gaps) {
        return interval(Arrays.asList(gaps));
    }

    public static Observable<Long> interval(List<Long> gaps) {
        return interval(gaps, TimeUnit.MILLISECONDS);
    }

    public static Observable<Long> interval(List<Long> gaps, TimeUnit unit) {
        return interval(gaps, unit, Schedulers.computation());
    }

    public static Observable<Long> interval(List<Long> gaps, TimeUnit unit, Scheduler scheduler) {
        if (gaps == null || gaps.isEmpty()) {
            throw new IllegalArgumentException("Provide one or more interval gaps!");
        }

        return Observable.<Long>create(subscriber -> {
            int size = gaps.size();

            Worker worker = scheduler.createWorker();
            subscriber.add(worker);

            final Action0 action = new Action0() {

                long current = 0;

                @Override
                public void call() {
                    subscriber.onNext(current++);

                    long currentGap = gaps.get((int) current % size);
                    worker.schedule(this, currentGap, unit);
                }
            };

            worker.schedule(action, gaps.get(0), unit);
        });
    }
}
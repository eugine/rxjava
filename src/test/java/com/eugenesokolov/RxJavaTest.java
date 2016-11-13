package com.eugenesokolov;

import org.junit.Rule;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

// examples from article: Testing RxJava
// https://www.infoq.com/articles/Testing-RxJava
public class RxJavaTest {

    private static final List<String> WORDS = Arrays.asList(
            "the",
            "quick",
            "brown",
            "fox",
            "jumped",
            "over",
            "the",
            "lazy",
            "dog"
    );

    @Rule
    public ImmediateSchedulersRule immediateSchedulersRule = new ImmediateSchedulersRule();

    @Test
    public void testInSameThread() {
        // given:
        List<String> results = new ArrayList<>();
        Observable<String> observable = Observable
                .from(WORDS)
                .zipWith(Observable.range(1, Integer.MAX_VALUE),
                        (string, index) -> String.format("%2d. %s", index, string));

        // when:
        observable.subscribe(results::add);

        // then:
        assertThat(results, notNullValue());
        assertThat(results, hasSize(9));
        assertThat(results, hasItem(" 4. fox"));
    }

    @Test
    public void testUsingTestSubscriber() {
        // given:
        TestSubscriber<String> subscriber = new TestSubscriber<>();

        Observable<String> observable = Observable
                .from(WORDS)
                .zipWith(Observable.range(1, Integer.MAX_VALUE),
                        (string, index) -> String.format("%2d. %s", index, string));

        // when:
        observable.subscribe(subscriber);

        // then:
        subscriber.assertCompleted();
        subscriber.assertNoErrors();
        subscriber.assertValueCount(9);
        assertThat(subscriber.getOnNextEvents(), hasItem(" 4. fox"));
    }

    @Test
    public void testUsingBlockingCall() {
        // given:
        Observable<String> observable = Observable.from(WORDS)
                .zipWith(Observable.range(1, Integer.MAX_VALUE),
                        (string, index) -> String.format("%2d. %s", index, string));

        // when:
        Iterable<String> results = observable
                .subscribeOn(Schedulers.computation())
                .toBlocking()
                .toIterable();

        // then:
        assertThat(results, notNullValue());
        assertThat(results, iterableWithSize(9));
        assertThat(results, hasItem(" 4. fox"));
    }

    @Test
    public void testUsingComputationScheduler() {
        // given:
        TestSubscriber<String> subscriber = new TestSubscriber<>();
        Observable<String> observable = Observable.from(WORDS)
                .zipWith(Observable.range(1, Integer.MAX_VALUE),
                        (string, index) -> String.format("%2d. %s", index, string));

        // when:
        observable.subscribeOn(Schedulers.computation())
                .subscribe(subscriber);

        await().timeout(2, SECONDS)
                .until(subscriber::getValueCount, equalTo(9));

        // then:
        subscriber.assertCompleted();
        subscriber.assertNoErrors();
        assertThat(subscriber.getOnNextEvents(), hasItem(" 4. fox"));
    }

    @Test
    public void testUsingRxJavaHooksWithImmediateScheduler() {
        // given:
        RxJavaHooks.setOnComputationScheduler(scheduler -> Schedulers.immediate());

        TestSubscriber<String> subscriber = new TestSubscriber<>();
        Observable<String> observable = Observable.from(WORDS)
                .zipWith(Observable.range(1, Integer.MAX_VALUE),
                        (string, index) -> String.format("%2d. %s", index, string));

        try {
            // when:
            observable.subscribeOn(Schedulers.computation())
                    .subscribe(subscriber);

            // then:
            subscriber.assertCompleted();
            subscriber.assertNoErrors();
            subscriber.assertValueCount(9);
            assertThat(subscriber.getOnNextEvents(), hasItem(" 4. fox"));
        } finally {
            RxJavaHooks.reset();
        }
    }
}

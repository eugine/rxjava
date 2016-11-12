package com.eugenesokolov;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void helloRxJava(String... names) {
        Observable.from(names).subscribe(System.out::println);
    }

    public static void main(String[] args) {
//        helloRxJava("Hello", "RxJava");
//        other("Hello", "Streams", "Not");
//        connection();

        rxJavaByExample();
//        rxJavaTime();

//        grokkingRx();

        try {
            Thread.sleep(60_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void grokkingRx() {
        Observable<String> myObservable = Observable.create(
                new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> sub) {
                        sub.onNext("Hello, world!");
                        sub.onCompleted();
                    }
                }
        );

        myObservable.subscribe(System.out::println);

    }

    private static void rxJavaTime() {
        Observable<Long> fast = Observable.interval(1, TimeUnit.SECONDS);
        Observable<Long> slow = Observable.interval(3, TimeUnit.SECONDS);

        Observable<Long> clock = Observable.merge(
                slow.filter(tick-> isSlowTickTime()),
                fast.filter(tick-> !isSlowTickTime())
        );

        clock.subscribe(tick-> System.out.println(new Date()));
    }

    private static long start = System.currentTimeMillis();
    public static Boolean isSlowTickTime() {
        return (System.currentTimeMillis() - start) % 30_000 >= 15_000;
    }

    private static void rxJavaByExample() {
        List<String> words = Arrays.asList(
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

//        Observable.range(1, 100)
//            .subscribe(System.out::println);

        Observable.just(words)
                .flatMap(Observable::from)
                .subscribe(System.out::println);

//        Observable.from(words)
//                .flatMap(word -> Observable.from(word.split("")))
//                .distinct()
//                .sorted()
//                .zipWith(Observable.range(1, 1000), (item, index) -> String.format("%2d. %s", index, item))
////                .de
//                .subscribe(System.out::println);
    }

    private static void connection() {
        Connection connection = new Connection();
        Observable<String> observable1 = connection.send("echo hello");
        Observable<String> observable2 = connection.send("pwd");

        observable1.subscribe(System.out::println, System.err::println);
        observable2.subscribe(System.out::println, System.err::println);

        try {
            System.out.println(Thread.currentThread().getName() + "-thread-" + Thread.currentThread().getId() + ": goes to sleep");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void other(String... items) {
        Observable.from(items)
                .filter(s -> s.contains("e"))
                .map(String::toUpperCase)
                .reduce(new StringBuilder(), StringBuilder::append)
                .subscribe(System.out::print, System.err::println, () -> System.out.println("!"));

    }
}

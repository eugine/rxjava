package com.eugenesokolov;

import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FileRxJava {

    public static void main(String[] args) {
        Subscription subscription = Observable
                .interval(1, TimeUnit.SECONDS)
                .flatMap(i -> fileObservable("rxjava.txt"))
                .flatMap(Observable::from)
                .map(String::toUpperCase)
                .filter(item -> item.compareTo("RX3") > 0)
                .subscribeOn(Schedulers.io())
                .subscribe(System.out::println);

        while (!subscription.isUnsubscribed()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private static Observable<List<String>> fileObservable(String resourceFileName) {
        return Observable.create(subscriber -> {
            try {
                subscriber.onNext(Files.readAllLines(Paths.get(FileRxJava.class.getResource("/" + resourceFileName).toURI())));
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }
}

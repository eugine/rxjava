package com.eugenesokolov;

import rx.Observable;
import rx.schedulers.Schedulers;

public class Connection {

    public Observable<String> send(String command) {
        return Observable.just(command)
                .doOnNext(this::checkConnection)
//                .map(String::getBytes)
                .map(this::addHeader)
                .map(this::sendBytes)
                .map(this::readAnswer)
                .subscribeOn(Schedulers.io());
    }

    private String readAnswer(String result) {
        return getPrintMsg("Response for " + result);
    }

    private String sendBytes(String bytes) {
        System.out.println(getPrintMsg("Sending bytes: " + bytes));
        return bytes;
    }

    private String addHeader(String bytes) {
        System.out.println(getPrintMsg("Adding header to " + bytes));
        return bytes;
    }

    private void checkConnection(String bytes) {
        System.out.println(getPrintMsg("Checking connection for " + bytes));
    }

    private String getPrintMsg(String msg) {
        return Thread.currentThread().getName() + "-thread-" + Thread.currentThread().getId() + ": " + msg;
    }
}

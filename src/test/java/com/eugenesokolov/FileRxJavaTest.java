package com.eugenesokolov;

import org.junit.Test;
import rx.Observable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class FileRxJavaTest {

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


}
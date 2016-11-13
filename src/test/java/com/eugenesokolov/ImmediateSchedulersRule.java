package com.eugenesokolov;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

class ImmediateSchedulersRule implements TestRule {

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.immediate());
                RxJavaHooks.setOnComputationScheduler(scheduler -> Schedulers.immediate());
                RxJavaHooks.setOnNewThreadScheduler(scheduler -> Schedulers.immediate());
                try {
                    base.evaluate();
                } finally {
                    RxJavaHooks.reset();
                }
            }
        };
    }
}

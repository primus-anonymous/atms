package com.neocaptainnemo.testing.app;

import android.app.Application;
import android.content.Context;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@Module
public class AppModule {

    Application app;

    public AppModule(Application app) {
        this.app = app;
    }

    @Provides
    @Singleton
    Application providesApplication() {
        return app;
    }

    @Provides
    @Singleton
    Context providesContext() {
        return app.getApplicationContext();
    }

    @Provides
    @Named("subscribe")
    Scheduler providesSusbcriptionScheduler() {
        return Schedulers.io();
    }

    @Provides
    @Named("observe")
    Scheduler providesObserveScheduler() {
        return AndroidSchedulers.mainThread();
    }
}

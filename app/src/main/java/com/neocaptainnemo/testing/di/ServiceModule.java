package com.neocaptainnemo.testing.di;

import android.app.Application;

import com.neocaptainnemo.testing.service.Atms;
import com.neocaptainnemo.testing.service.AtmsRepo;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ServiceModule {

    @Provides
    @Singleton
    Atms providesAtms(Application application) {
        return new AtmsRepo(application.getApplicationContext());
    }
}

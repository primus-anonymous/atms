package com.neocaptainnemo.testing;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.neocaptainnemo.testing.di.AppComponent;
import com.neocaptainnemo.testing.di.AppModule;
import com.neocaptainnemo.testing.di.DaggerAppComponent;

public class App extends Application {

    private AppComponent appComponent;


    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);

        appComponent = DaggerAppComponent
                .builder()
                .appModule(new AppModule(this))
                .build();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}

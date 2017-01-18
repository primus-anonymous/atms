package com.neocaptainnemo.testing.mocks;

import com.neocaptainnemo.testing.MainActivityTest;
import com.neocaptainnemo.testing.app.AppComponent;
import com.neocaptainnemo.testing.app.AppModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, MockServiceModule.class})
public interface MockAppComponent extends AppComponent {

    void inject(MainActivityTest activityTest);
}

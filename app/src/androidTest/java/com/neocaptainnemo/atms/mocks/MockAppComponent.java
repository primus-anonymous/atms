package com.neocaptainnemo.atms.mocks;

import com.neocaptainnemo.atms.MainActivityTest;
import com.neocaptainnemo.atms.app.AppComponent;
import com.neocaptainnemo.atms.app.AppModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, MockServiceModule.class})
public interface MockAppComponent extends AppComponent {

    void inject(MainActivityTest activityTest);
}

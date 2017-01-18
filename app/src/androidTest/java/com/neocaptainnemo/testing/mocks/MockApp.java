package com.neocaptainnemo.testing.mocks;

import com.neocaptainnemo.testing.app.App;
import com.neocaptainnemo.testing.app.AppComponent;
import com.neocaptainnemo.testing.app.AppModule;

public class MockApp extends App {

    private MockAppComponent mockAppComponent;

    @Override
    public AppComponent getAppComponent() {
        if (mockAppComponent == null) {
            mockAppComponent = DaggerMockAppComponent.builder()
                    .appModule(new AppModule(this))
                    .build();
        }
        return mockAppComponent;
    }
}

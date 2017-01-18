package com.neocaptainnemo.atms.mocks;

import com.neocaptainnemo.atms.app.App;
import com.neocaptainnemo.atms.app.AppComponent;
import com.neocaptainnemo.atms.app.AppModule;

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

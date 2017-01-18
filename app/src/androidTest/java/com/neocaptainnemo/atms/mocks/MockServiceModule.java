package com.neocaptainnemo.atms.mocks;

import com.neocaptainnemo.atms.service.Atms;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

@Module
class MockServiceModule {

    @Provides
    @Singleton
    Atms providesAtms() {

        return mock(Atms.class);
    }

}

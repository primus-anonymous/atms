package com.neocaptainnemo.testing.mocks;

import com.neocaptainnemo.testing.service.Atms;

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

package com.neocaptainnemo.atms.mocks

import com.neocaptainnemo.atms.service.Atms
import dagger.Module
import dagger.Provides
import org.mockito.Mockito.mock
import javax.inject.Singleton

@Module
class MockServiceModule {

    @Provides
    @Singleton
    fun providesAtms(): Atms = mock(Atms::class.java)
}


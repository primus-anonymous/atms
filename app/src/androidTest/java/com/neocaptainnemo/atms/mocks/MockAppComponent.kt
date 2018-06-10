package com.neocaptainnemo.atms.mocks

import android.app.Application
import com.neocaptainnemo.atms.MainActivityTest
import com.neocaptainnemo.atms.app.AppComponent
import com.neocaptainnemo.atms.app.AppModule
import com.neocaptainnemo.atms.di.ActivityModules
import com.neocaptainnemo.atms.di.FragmentModules
import com.neocaptainnemo.atms.di.ViewModelModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, MockServiceModule::class, AndroidSupportInjectionModule::class,
    ActivityModules::class, FragmentModules::class, ViewModelModule::class])
interface MockAppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): MockAppComponent
    }

    fun inject(activityTest: MainActivityTest)

    fun inject(app: MockApp)
}

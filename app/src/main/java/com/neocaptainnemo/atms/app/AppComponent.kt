package com.neocaptainnemo.atms.app

import android.app.Application
import com.neocaptainnemo.atms.di.ActivityModules
import com.neocaptainnemo.atms.di.FragmentModules
import com.neocaptainnemo.atms.di.ViewModelModule
import com.neocaptainnemo.atms.service.ServiceModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, ServiceModule::class, AndroidSupportInjectionModule::class,
    ActivityModules::class, FragmentModules::class, ViewModelModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(app: App)

}

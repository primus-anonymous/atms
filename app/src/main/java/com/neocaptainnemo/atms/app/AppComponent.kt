package com.neocaptainnemo.atms.app

import com.neocaptainnemo.atms.service.ServiceModule
import com.neocaptainnemo.atms.ui.MainActivity
import com.neocaptainnemo.atms.ui.list.ListFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, ServiceModule::class])
interface AppComponent {

    fun inject(activity: MainActivity)

    fun inject(fragment: ListFragment)

}

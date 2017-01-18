package com.neocaptainnemo.atms.app;

import com.neocaptainnemo.atms.service.ServiceModule;
import com.neocaptainnemo.atms.ui.MainActivity;
import com.neocaptainnemo.atms.ui.list.ListFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, ServiceModule.class})
public interface AppComponent {

    void inject(MainActivity activity);

    void inject(ListFragment fragment);

}

package com.neocaptainnemo.testing.app;

import com.neocaptainnemo.testing.service.ServiceModule;
import com.neocaptainnemo.testing.ui.MainActivity;
import com.neocaptainnemo.testing.ui.list.ListFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, ServiceModule.class})
public interface AppComponent {

    void inject(MainActivity activity);

    void inject(ListFragment fragment);

}

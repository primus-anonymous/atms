package com.neocaptainnemo.testing.ui;

import android.content.Context;

import com.neocaptainnemo.testing.model.ViewPort;
import com.neocaptainnemo.testing.service.Atms;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import rx.Observable;
import rx.schedulers.Schedulers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PresenterTest {

    @Mock
    IView view;
    @Mock
    Context context;
    @Mock
    Atms atms;

    Presenter presenter;

    @Before
    public void before() {
        presenter = new Presenter(context, atms, Schedulers.test(), Schedulers.test());
        presenter.setView(view);
    }

    @Test
    public void fetch() {

        when(atms.request(any())).thenReturn(Observable.empty());

        presenter.fetchAtms(new ViewPort(1, 1, 1, 1));

        verify(view).onStartGettingAtsm();
    }
}

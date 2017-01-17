package com.neocaptainnemo.testing.ui;

import android.content.Context;

import com.google.gson.Gson;
import com.neocaptainnemo.testing.R;
import com.neocaptainnemo.testing.model.AtmNode;
import com.neocaptainnemo.testing.model.ViewPort;
import com.neocaptainnemo.testing.service.Atms;
import com.neocaptainnemo.testing.service.OsmResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.schedulers.Schedulers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
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
        presenter = new Presenter(context, atms, Schedulers.immediate(), Schedulers.immediate());
        presenter.setView(view);
    }

    @Test
    public void fetchFirstTime() {

        when(atms.request(any())).thenReturn(Observable.just(Collections.emptyList()));

        presenter.fetchAtms(new ViewPort(1, 1, 1, 1));

        verify(view).showProgress();
    }

    @Test
    public void fetchInsideCachedViewport() {

        when(atms.request(any())).thenReturn(Observable.just(Collections.emptyList()));

        presenter.fetchAtms(new ViewPort(1, 1, 1, 1));
        presenter.fetchAtms(new ViewPort(1, 1, 1, 1));

        verify(view, times(1)).showProgress();
    }

    @Test
    public void fetchOutsideCachedViewport() {

        when(atms.request(any())).thenReturn(Observable.just(Collections.emptyList()));

        presenter.fetchAtms(new ViewPort(1, 1, 1, 1));
        presenter.fetchAtms(new ViewPort(1, 1, 2, 1));

        verify(view, times(2)).showProgress();
    }

    @Test
    public void fetchErrorInBetween() {

        when(atms.request(any())).thenReturn(Observable.just(Collections.emptyList()));
        presenter.fetchAtms(new ViewPort(1, 1, 1, 1));

        when(atms.request(any())).thenReturn(Observable.error(new Exception()));
        presenter.fetchAtms(new ViewPort(1, 1, 2, 1));

        when(atms.request(any())).thenReturn(Observable.just(Collections.emptyList()));
        presenter.fetchAtms(new ViewPort(1, 1, 1, 1));

        verify(view, times(3)).showProgress();
    }

    @Test
    public void fetchSuccess() {
        when(atms.request(any())).thenReturn(Observable.just(Collections.emptyList()));
        presenter.fetchAtms(new ViewPort(1, 1, 1, 1));

        verify(view).onGotAtms(Collections.emptyList());
    }

    @Test
    public void fetchError() {
        when(atms.request(any())).thenReturn(Observable.error(new Exception()));
        presenter.fetchAtms(new ViewPort(1, 1, 1, 1));

        verify(view).hideProgress();
    }

    @Test
    public void fetchSuccessNoFilter() {

        OsmResponse response = getOsmResponse();

        when(atms.request(any())).thenReturn(Observable.just(response.getAtms()));

        presenter.fetchAtms(new ViewPort(1, 1, 1, 1));
        verify(view).onGotAtms(response.getAtms());
    }

    @Test
    public void fetchSuccessFilterResults() {

        OsmResponse response = getOsmResponse();

        when(atms.request(any())).thenReturn(Observable.just(response.getAtms()));

        presenter.fetchAtms(new ViewPort(1, 1, 1, 1));
        presenter.setFilter("B SEB");

        AtmNode atmNode = new AtmNode();
        atmNode.setId(325105300);
        List<AtmNode> expected = new ArrayList<>();
        expected.add(atmNode);

        verify(view).onGotAtms(expected);
    }

    @Test
    public void fetchSuccessFilterNoResults() {

        OsmResponse response = getOsmResponse();

        when(atms.request(any())).thenReturn(Observable.just(response.getAtms()));

        presenter.fetchAtms(new ViewPort(1, 1, 1, 1));
        presenter.setFilter("B SEB!2");

        verify(view).onGotAtms(Collections.emptyList());
    }

    @Test
    public void fetchGeneralError() {

        when(context.getString(R.string.general_error)).thenReturn("GeneralError");

        when(atms.request(any())).thenReturn(Observable.error(new Exception()));
        presenter.fetchAtms(new ViewPort(1, 1, 2, 1));

        verify(view).showError("GeneralError");
    }

    @Test
    public void fetchIOError() {

        when(context.getString(R.string.network_error)).thenReturn("NetworkError");

        when(atms.request(any())).thenReturn(Observable.error(new IOException()));
        presenter.fetchAtms(new ViewPort(1, 1, 2, 1));

        verify(view).showError("NetworkError");
    }


    @Test
    public void openMap() {
        presenter.setOpenedTab(Presenter.Tab.MAP);
        verify(view).showMap();
    }

    @Test
    public void openList() {
        presenter.setOpenedTab(Presenter.Tab.LIST);
        verify(view).showList();
    }

    @Test
    public void openSettings() {
        presenter.setOpenedTab(Presenter.Tab.SETTINGS);
        verify(view).showSettings();
    }

    private OsmResponse getOsmResponse() {
        InputStream stream = ClassLoader.getSystemResourceAsStream("response.json");

        InputStreamReader reader = new InputStreamReader(stream);
        return new Gson().fromJson(reader, OsmResponse.class);
    }


}

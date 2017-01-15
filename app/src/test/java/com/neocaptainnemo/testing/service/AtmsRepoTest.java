package com.neocaptainnemo.testing.service;

import com.google.gson.Gson;
import com.neocaptainnemo.testing.model.ViewPort;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.InputStream;
import java.io.InputStreamReader;

import rx.Observable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AtmsRepoTest {

    @Mock
    OpenStreetMap openStreetMap;

    AtmsRepo repo;

    @Before
    public void before() {
        repo = new AtmsRepo(openStreetMap);
    }

    @Test
    public void fetchSuccessNoNull() {

        InputStream stream = ClassLoader.getSystemResourceAsStream("response.json");
        InputStreamReader reader = new InputStreamReader(stream);
        OsmResponse response = new Gson().fromJson(reader, OsmResponse.class);

        when(openStreetMap.request(any())).thenReturn(Observable.just(response));

        Assertions.assertThat(repo.request(new ViewPort(1, 1, 1, 1)).toBlocking().first())
                .isEqualTo(response.getAtms());

    }

    @Test
    public void fetchNull() {

        InputStream stream = ClassLoader.getSystemResourceAsStream("response_null.json");
        InputStreamReader reader = new InputStreamReader(stream);
        OsmResponse response = new Gson().fromJson(reader, OsmResponse.class);

        when(openStreetMap.request(any())).thenReturn(Observable.just(response));

        Assertions.assertThat(repo.request(new ViewPort(1, 1, 1, 1)).toBlocking().first())
                .isEmpty();
    }

}

package com.neocaptainnemo.testing.service;

import android.content.Context;

import com.neocaptainnemo.testing.R;
import com.neocaptainnemo.testing.service.DistanceFormatter;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DistanceFormatterTest {

    @Mock
    Context context;

    DistanceFormatter formatter;

    @Before
    public void before() {
        formatter = new DistanceFormatter(context);
    }


    @Test
    public void testMeters() {
        double dist = 100.0;

        when(context.getString(R.string.distance_m, dist))
                .thenReturn("100 m");

        Assertions.assertThat(formatter.format(dist)).isEqualTo("100 m");
    }

    @Test
    public void testMeters999() {
        double dist = 999;

        when(context.getString(R.string.distance_m, dist))
                .thenReturn("999 m");

        Assertions.assertThat(formatter.format(dist)).isEqualTo("999 m");
    }

    @Test
    public void testMeters1000() {
        double dist = 1000;

        when(context.getString(R.string.distance_km, 1.0))
                .thenReturn("1 km");

        Assertions.assertThat(formatter.format(dist)).isEqualTo("1 km");
    }

    @Test
    public void testMeters2000() {
        double dist = 2000;

        when(context.getString(R.string.distance_km, 2.0))
                .thenReturn("2 km");

        Assertions.assertThat(formatter.format(dist)).isEqualTo("2 km");
    }
}

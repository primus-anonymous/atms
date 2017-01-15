package com.neocaptainnemo.testing.model;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ViewPortTest {

    @Test
    public void inside() {
        ViewPort viewPort = new ViewPort(0, 10, 20, 30);
        ViewPort inside = new ViewPort(5, 15, 10, 25);

        Assertions.assertThat(viewPort.isInside(inside)).isTrue();
    }

    @Test
    public void outside() {
        ViewPort viewPort = new ViewPort(0, 10, 20, 30);
        ViewPort inside = new ViewPort(0, 40, 60, 80);

        Assertions.assertThat(viewPort.isInside(inside)).isFalse();
    }

    @Test
    public void outsideLng() {
        ViewPort viewPort = new ViewPort(0, 10, 20, 30);
        ViewPort inside = new ViewPort(5, 15, 21, 25);

        Assertions.assertThat(viewPort.isInside(inside)).isFalse();
    }

    @Test
    public void outsideLat1() {
        ViewPort viewPort = new ViewPort(0, 10, 20, 30);
        ViewPort inside = new ViewPort(5, 9, 10, 25);

        Assertions.assertThat(viewPort.isInside(inside)).isFalse();
    }

    @Test
    public void outsideLat2() {
        ViewPort viewPort = new ViewPort(0, 10, 20, 30);
        ViewPort inside = new ViewPort(5, 15, 10, 31);

        Assertions.assertThat(viewPort.isInside(inside)).isFalse();
    }
}

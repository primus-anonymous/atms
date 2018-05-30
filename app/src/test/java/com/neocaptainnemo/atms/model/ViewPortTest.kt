package com.neocaptainnemo.atms.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ViewPortTest {

    @Test
    fun inside() {
        val viewPort = ViewPort(0.0, 10.0, 20.0, 30.0)
        val inside = ViewPort(5.0, 15.0, 10.0, 25.0)

        assertThat(viewPort.isInside(inside)).isTrue()
    }

    @Test
    fun outside() {
        val viewPort = ViewPort(0.0, 10.0, 20.0, 30.0)
        val inside = ViewPort(0.0, 40.0, 60.0, 80.0)

        assertThat(viewPort.isInside(inside)).isFalse()
    }

    @Test
    fun outsideLng() {
        val viewPort = ViewPort(0.0, 10.0, 20.0, 30.0)
        val inside = ViewPort(5.0, 15.0, 21.0, 25.0)

        assertThat(viewPort.isInside(inside)).isFalse()
    }

    @Test
    fun outsideLat1() {
        val viewPort = ViewPort(0.0, 10.0, 20.0, 30.0)
        val inside = ViewPort(5.0, 9.0, 10.0, 25.0)

        assertThat(viewPort.isInside(inside)).isFalse()
    }

    @Test
    fun outsideLat2() {
        val viewPort = ViewPort(0.0, 10.0, 20.0, 30.0)
        val inside = ViewPort(5.0, 15.0, 10.0, 31.0)

        assertThat(viewPort.isInside(inside)).isFalse()
    }
}

package com.neocaptainnemo.atms.service

import android.content.Context
import com.neocaptainnemo.atms.R
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class DistanceFormatterTest {

    @Mock
    lateinit var context: Context

    lateinit var formatter: DistanceFormatter

    @Before
    fun before() {
        formatter = DistanceFormatter(context)
    }


    @Test
    fun testMeters() {
        val dist = 100.0

        whenever(context.getString(R.string.distance_m, dist))
                .thenReturn("100 m")

        assertThat(formatter.format(dist)).isEqualTo("100 m")
    }

    @Test
    fun testMeters999() {
        val dist = 999.0

        whenever(context.getString(R.string.distance_m, dist))
                .thenReturn("999 m")

        assertThat(formatter.format(dist)).isEqualTo("999 m")
    }

    @Test
    fun testMeters1000() {
        val dist = 1000.0

        whenever(context.getString(R.string.distance_km, 1.0))
                .thenReturn("1 km")

        assertThat(formatter.format(dist)).isEqualTo("1 km")
    }

    @Test
    fun testMeters2000() {
        val dist = 2000.0

        whenever(context.getString(R.string.distance_km, 2.0))
                .thenReturn("2 km")

        assertThat(formatter.format(dist)).isEqualTo("2 km")
    }
}

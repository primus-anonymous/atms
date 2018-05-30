package com.neocaptainnemo.atms.service

import com.google.gson.Gson
import com.neocaptainnemo.atms.model.ViewPort
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.io.InputStreamReader

@RunWith(MockitoJUnitRunner::class)
class AtmsRepoTest {

    @Mock
    lateinit var openStreetMap: OpenStreetMap

    lateinit var repo: AtmsRepo

    @Before
    fun before() {
        repo = AtmsRepo(openStreetMap)
    }

    @Test
    fun fetchSuccessNoNull() {

        val stream = ClassLoader.getSystemResourceAsStream("response.json")
        val reader = InputStreamReader(stream)
        val response = Gson().fromJson(reader, OsmResponse::class.java)

        whenever(openStreetMap.request(anyString())).then { Observable.just(response) }

        val testable = repo.request(ViewPort(1.0, 1.0, 1.0, 1.0)).test()

        testable.assertValue(response.atms)
    }

    @Test
    fun fetchNull() {

        val stream = ClassLoader.getSystemResourceAsStream("response_null.json")
        val reader = InputStreamReader(stream)
        val response = Gson().fromJson(reader, OsmResponse::class.java)

        whenever(openStreetMap.request(anyString())).then { Observable.just(response) }

        val testable = repo.request(ViewPort(1.0, 1.0, 1.0, 1.0)).test()

        testable.assertValue { it.isEmpty() }
    }

}

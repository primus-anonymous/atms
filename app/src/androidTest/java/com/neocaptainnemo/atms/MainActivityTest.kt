package com.neocaptainnemo.atms


import android.app.Instrumentation
import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.espresso.Espresso
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.IdlingResource
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.doesNotExist
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.intent.Intents.intended
import android.support.test.espresso.intent.Intents.intending
import android.support.test.espresso.intent.matcher.IntentMatchers.hasAction
import android.support.test.espresso.intent.matcher.IntentMatchers.hasData
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.GrantPermissionRule
import android.support.test.runner.AndroidJUnit4
import android.support.v7.preference.AndroidResources
import android.support.v7.widget.RecyclerView
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import com.google.gson.Gson
import com.neocaptainnemo.atms.matchers.RecyclerViewMatcher
import com.neocaptainnemo.atms.mocks.MockApp
import com.neocaptainnemo.atms.service.Atms
import com.neocaptainnemo.atms.service.OsmResponse
import com.neocaptainnemo.atms.ui.MainActivity
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.io.InputStreamReader
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Rule
    @JvmField
    var activityRule: IntentsTestRule<MainActivity> = IntentsTestRule(
            MainActivity::class.java, true, false)

    @Rule
    @JvmField
    val grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

    @Inject
    lateinit var atms: Atms

    private val osmResponse: OsmResponse
        @Throws(IOException::class)
        get() {
            val stream = InstrumentationRegistry.getContext().assets.open("response.json")

            val reader = InputStreamReader(stream)
            return Gson().fromJson(reader, OsmResponse::class.java)
        }

    @Before
    fun before() {
        val instrumentation = getInstrumentation()

        val app = instrumentation.targetContext.applicationContext as MockApp
        app.component.inject(this)
    }


    @Test
    fun mapDisplayedOnly() {
        whenever(atms.request(any())).thenReturn(Observable.just(listOf()))

        activityRule.launchActivity(Intent())

        onView(withId(R.id.mapRoot))
                .check(matches(isDisplayed()))
        onView(withId(R.id.listRoot))
                .check(doesNotExist())
        onView(withId(AndroidResources.ANDROID_R_LIST_CONTAINER))
                .check(doesNotExist())
    }

    @Test
    fun listClick() {
        whenever(atms.request(any())).thenReturn(Observable.just(listOf()))

        activityRule.launchActivity(Intent())

        onView(withId(R.id.action_list))
                .perform(click())

        onView(withId(R.id.mapRoot))
                .check(matches(not(isDisplayed())))
        onView(withId(R.id.listRoot))
                .check(matches(isDisplayed()))
        onView(withId(AndroidResources.ANDROID_R_LIST_CONTAINER))
                .check(doesNotExist())

    }


    @Test
    fun settingsClick() {
        whenever(atms.request(any())).thenReturn(Observable.just(listOf()))

        activityRule.launchActivity(Intent())

        onView(withId(R.id.action_settings))
                .perform(click())

        onView(withId(R.id.mapRoot))
                .check(matches(not(isDisplayed())))
        onView(withId(R.id.listRoot))
                .check(doesNotExist())
        onView(withId(AndroidResources.ANDROID_R_LIST_CONTAINER))
                .check(matches(isDisplayed()))

    }

    @Test
    fun emptyListEmptyMessage() {
        whenever(atms.request(any())).thenReturn(Observable.just(listOf()))

        activityRule.launchActivity(launchTestIntent())

        onView(withId(R.id.action_list))
                .perform(click())

        onView(withId(R.id.empty))
                .check(matches(isDisplayed()))
    }

    @Test
    fun dataListEmptyMessage() {

        whenever(atms.request(any())).thenReturn(Observable.just(osmResponse.atms))

        activityRule.launchActivity(launchTestIntent())

        onView(withId(R.id.action_list))
                .perform(click())

        onView(withId(R.id.empty))
                .check(matches(not(isDisplayed())))
    }


    @Test
    fun dataListActualData() {

        whenever(atms.request(any())).thenReturn(Observable.just(osmResponse.atms))

        activityRule.launchActivity(launchTestIntent())

        onView(withId(R.id.action_list))
                .perform(click())

        onView(RecyclerViewMatcher(R.id.atmList).atPosition(0))
                .check(matches(hasDescendant(withText("Swedbank"))))
        onView(RecyclerViewMatcher(R.id.atmList).atPosition(0))
                .check(matches(hasDescendant(withText(R.string.no_address))))
        onView(RecyclerViewMatcher(R.id.atmList).atPosition(1))
                .check(matches(hasDescendant(withText("AB SEB Bankas"))))
        onView(RecyclerViewMatcher(R.id.atmList).atPosition(1))
                .check(matches(hasDescendant(withText("Gedimino pr. 12, 01103, Vilnius"))))
    }

    @Test
    fun dataListClickClosesList() {

        whenever(atms.request(any())).thenReturn(Observable.just(osmResponse.atms))

        activityRule.launchActivity(launchTestIntent())

        onView(withId(R.id.action_list))
                .perform(click())

        onView(withId(R.id.atmList))
                .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        onView(withId(R.id.listRoot))
                .check(matches(not(isDisplayed())))

    }

    @Test
    fun dataListClickOpensBottomSheet() {

        whenever(atms.request(any())).thenReturn(Observable.just(osmResponse.atms))

        activityRule.launchActivity(launchTestIntent())

        onView(withId(R.id.action_list))
                .perform(click())

        onView(withId(R.id.atmList))
                .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click()))

        onView(withId(R.id.atmName))
                .check(matches(withText("AB SEB Bankas")))
        onView(withId(R.id.atmAddress))
                .check(matches(withText("Gedimino pr. 12, 01103, Vilnius")))

    }

    @Test
    fun navigateIsHidden() {

        whenever(atms.request(any())).thenReturn(Observable.just(osmResponse.atms))

        activityRule.launchActivity(launchTestIntent())

        onView(withId(R.id.navigate))
                .check(matches(not(isDisplayed())))
    }


    @Test
    fun navigateAppearsWhenBottomSheetUp() {

        whenever(atms.request(any())).thenReturn(Observable.just(osmResponse.atms))

        activityRule.launchActivity(launchTestIntent())

        onView(withId(R.id.action_list))
                .perform(click())

        onView(withId(R.id.atmList))
                .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click()))

        val fabIdlingResource = FabIdlingResource()
        Espresso.registerIdlingResources(fabIdlingResource)
        onView(withId(R.id.navigate))
                .check(matches(isDisplayed()))
        Espresso.unregisterIdlingResources(fabIdlingResource)
    }

    @Test
    fun navigateTriggersSelectionDialog() {

        whenever(atms.request(any())).thenReturn(Observable.just(osmResponse.atms))

        activityRule.launchActivity(launchTestIntent())

        onView(withId(R.id.action_list))
                .perform(click())

        onView(withId(R.id.atmList))
                .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click()))

        val fabIdlingResource = FabIdlingResource()
        Espresso.registerIdlingResources(fabIdlingResource)
        onView(withId(R.id.navigate))
                .perform(click())
        Espresso.unregisterIdlingResources(fabIdlingResource)

        onView(withText(R.string.choose_the_nav_way)).check(matches(isDisplayed()))
        onView(withText(R.string.nav_walk)).check(matches(isDisplayed()))
        onView(withText(R.string.nav_drive)).check(matches(isDisplayed()))

    }

    @Test
    fun selectNavigationIntent() {

        whenever(atms.request(any())).thenReturn(Observable.just(osmResponse.atms))

        activityRule.launchActivity(launchTestIntent())

        onView(withId(R.id.action_list))
                .perform(click())

        onView(withId(R.id.atmList))
                .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click()))

        val fabIdlingResource = FabIdlingResource()
        Espresso.registerIdlingResources(fabIdlingResource)
        onView(withId(R.id.navigate))
                .perform(click())
        Espresso.unregisterIdlingResources(fabIdlingResource)

        val expected = allOf(hasAction(Intent.ACTION_VIEW), hasData("google.navigation:q=54.686560,25.281028&mode=w"))

        intending(expected).respondWith(Instrumentation.ActivityResult(0, null))

        onView(withText(R.string.nav_walk)).perform(click())

        intended(expected)
    }


    @Test
    fun simpleSearchEmptyRes() {

        whenever(atms.request(any())).thenReturn(Observable.just(osmResponse.atms))

        activityRule.launchActivity(launchTestIntent())

        onView(withId(R.id.action_list))
                .perform(click())

        onView(withId(R.id.empty))
                .check(matches(not(isDisplayed())))

        onView(withId(R.id.search)).perform(click())
        onView(isAssignableFrom(EditText::class.java)).perform(typeText("Mdfsdf"), pressKey(KeyEvent.KEYCODE_ENTER))

        onView(withId(R.id.empty))
                .check(matches(isDisplayed()))

    }

    private fun launchTestIntent(): Intent {
        val intent = Intent()
        intent.putExtra(MainActivity.test, true)
        return intent
    }

    inner class FabIdlingResource : IdlingResource {

        private var resourceCallback: IdlingResource.ResourceCallback? = null

        override fun getName(): String = FabIdlingResource::class.java.name

        override fun isIdleNow(): Boolean {

            if (activityRule.activity == null) {
                return false
            }

            val view = activityRule.activity.findViewById<View>(R.id.navigate)

            if (view != null && view.visibility == View.VISIBLE) {
                resourceCallback!!.onTransitionToIdle()
                return true
            }
            return false
        }

        override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback) {
            this.resourceCallback = callback

        }
    }


}

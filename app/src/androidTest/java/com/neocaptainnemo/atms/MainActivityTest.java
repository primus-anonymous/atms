package com.neocaptainnemo.atms;

import android.app.Instrumentation;
import android.content.Intent;
import android.os.Build;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.neocaptainnemo.atms.matchers.RecyclerViewMatcher;
import com.neocaptainnemo.atms.mocks.MockApp;
import com.neocaptainnemo.atms.mocks.MockAppComponent;
import com.neocaptainnemo.atms.service.Atms;
import com.neocaptainnemo.atms.service.OsmResponse;
import com.neocaptainnemo.atms.ui.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;

import javax.inject.Inject;

import rx.Observable;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressKey;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasData;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.AllOf.allOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public IntentsTestRule<MainActivity> activityRule = new IntentsTestRule<>(
            MainActivity.class, true, false);
    @Inject
    Atms atms;

    static RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {
        return new RecyclerViewMatcher(recyclerViewId);
    }

    @Before
    public void before() {
        Instrumentation instrumentation = getInstrumentation();
        MockApp app
                = (MockApp) instrumentation.getTargetContext().getApplicationContext();
        ((MockAppComponent) app.getAppComponent()).inject(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + getTargetContext().getPackageName()
                            + " android.permission.ACCESS_FINE_LOCATION");
        }

    }


    @Test
    public void mapDisplayedOnly() {
        when(atms.request(any())).thenReturn(Observable.just(Collections.emptyList()));

        activityRule.launchActivity(new Intent());

        onView(withId(R.id.map_container))
                .check(matches(isDisplayed()));
        onView(withId(R.id.list_container))
                .check(matches(not(isDisplayed())));
        onView(withId(R.id.settings_container))
                .check(matches(not(isDisplayed())));

    }

    @Test
    public void listClick() {
        when(atms.request(any())).thenReturn(Observable.just(Collections.emptyList()));

        activityRule.launchActivity(new Intent());

        onView(withId(R.id.action_list))
                .perform(click());

        onView(withId(R.id.map_container))
                .check(matches(isDisplayed()));
        onView(withId(R.id.list_container))
                .check(matches(isDisplayed()));
        onView(withId(R.id.settings_container))
                .check(matches(not(isDisplayed())));

    }


    @Test
    public void settingsClick() {
        when(atms.request(any())).thenReturn(Observable.just(Collections.emptyList()));

        activityRule.launchActivity(new Intent());

        onView(withId(R.id.action_settings))
                .perform(click());

        onView(withId(R.id.map_container))
                .check(matches(isDisplayed()));
        onView(withId(R.id.list_container))
                .check(matches(not(isDisplayed())));
        onView(withId(R.id.settings_container))
                .check(matches(isDisplayed()));

    }

    @Test
    public void emptyListEmptyMessage() throws IOException {
        when(atms.request(any())).thenReturn(Observable.just(Collections.emptyList()));

        activityRule.launchActivity(launchTestIntent());

        onView(withId(R.id.action_list))
                .perform(click());

        onView(withId(R.id.empty))
                .check(matches(isDisplayed()));
    }

    @Test
    public void dataListEmptyMessage() throws IOException {

        when(atms.request(any())).thenReturn(Observable.just(getOsmResponse().getAtms()));

        activityRule.launchActivity(launchTestIntent());

        onView(withId(R.id.action_list))
                .perform(click());

        onView(withId(R.id.empty))
                .check(matches(not(isDisplayed())));
    }


    @Test
    public void dataListActualData() throws IOException {

        when(atms.request(any())).thenReturn(Observable.just(getOsmResponse().getAtms()));

        activityRule.launchActivity(launchTestIntent());

        onView(withId(R.id.action_list))
                .perform(click());

        onView(withRecyclerView(R.id.atm_list).atPosition(0))
                .check(matches(hasDescendant(withText("Swedbank"))));
        onView(withRecyclerView(R.id.atm_list).atPosition(0))
                .check(matches(hasDescendant(withText(R.string.no_address))));
        onView(withRecyclerView(R.id.atm_list).atPosition(1))
                .check(matches(hasDescendant(withText("AB SEB Bankas"))));
        onView(withRecyclerView(R.id.atm_list).atPosition(1))
                .check(matches(hasDescendant(withText("Gedimino pr. 12, 01103, Vilnius"))));
    }

    @Test
    public void dataListClickClosesList() throws IOException {

        when(atms.request(any())).thenReturn(Observable.just(getOsmResponse().getAtms()));

        activityRule.launchActivity(launchTestIntent());

        onView(withId(R.id.action_list))
                .perform(click());

        onView(withId(R.id.atm_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        onView(withId(R.id.list_container))
                .check(matches(not(isDisplayed())));

    }

    @Test
    public void dataListClickOpensBottomSheet() throws IOException {

        when(atms.request(any())).thenReturn(Observable.just(getOsmResponse().getAtms()));

        activityRule.launchActivity(launchTestIntent());

        onView(withId(R.id.action_list))
                .perform(click());

        onView(withId(R.id.atm_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

        onView(withId(R.id.atm_name))
                .check(matches(withText("AB SEB Bankas")));
        onView(withId(R.id.atm_address))
                .check(matches(withText("Gedimino pr. 12, 01103, Vilnius")));

    }

    @Test
    public void navigateIsHidden() throws IOException {

        when(atms.request(any())).thenReturn(Observable.just(getOsmResponse().getAtms()));

        activityRule.launchActivity(launchTestIntent());

        onView(withId(R.id.navigate))
                .check(matches(not(isDisplayed())));
    }


    @Test
    public void navigateAppearsWhenBotomSheetUp() throws IOException {

        when(atms.request(any())).thenReturn(Observable.just(getOsmResponse().getAtms()));

        activityRule.launchActivity(launchTestIntent());

        onView(withId(R.id.action_list))
                .perform(click());

        onView(withId(R.id.atm_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

        IdlingResource fabIdlingResource = new FabIdlingResource();
        Espresso.registerIdlingResources(fabIdlingResource);
        onView(withId(R.id.navigate))
                .check(matches(isDisplayed()));
        Espresso.unregisterIdlingResources(fabIdlingResource);
    }

    @Test
    public void navigateTriggersSelectionDialog() throws IOException {

        when(atms.request(any())).thenReturn(Observable.just(getOsmResponse().getAtms()));

        activityRule.launchActivity(launchTestIntent());

        onView(withId(R.id.action_list))
                .perform(click());

        onView(withId(R.id.atm_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

        IdlingResource fabIdlingResource = new FabIdlingResource();
        Espresso.registerIdlingResources(fabIdlingResource);
        onView(withId(R.id.navigate))
                .perform(click());
        Espresso.unregisterIdlingResources(fabIdlingResource);

        onView(withText(R.string.choose_the_nav_way)).check(matches(isDisplayed()));
        onView(withText(R.string.nav_walk)).check(matches(isDisplayed()));
        onView(withText(R.string.nav_drive)).check(matches(isDisplayed()));

    }

    @Test
    public void selectNavigationIntent() throws IOException {

        when(atms.request(any())).thenReturn(Observable.just(getOsmResponse().getAtms()));

        activityRule.launchActivity(launchTestIntent());

        onView(withId(R.id.action_list))
                .perform(click());

        onView(withId(R.id.atm_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

        IdlingResource fabIdlingResource = new FabIdlingResource();
        Espresso.registerIdlingResources(fabIdlingResource);
        onView(withId(R.id.navigate))
                .perform(click());
        Espresso.unregisterIdlingResources(fabIdlingResource);

        onView(withText(R.string.nav_walk)).perform(click());

        intended(allOf(hasAction(Intent.ACTION_VIEW), hasData("google.navigation:q=54.686560,25.281028&mode=w")));
    }


    @Test
    public void simpleSearchEmptyRes() throws IOException {

        when(atms.request(any())).thenReturn(Observable.just(getOsmResponse().getAtms()));

        activityRule.launchActivity(launchTestIntent());

        onView(withId(R.id.action_list))
                .perform(click());

        onView(withId(R.id.empty))
                .check(matches(not(isDisplayed())));

        onView(withId(R.id.search)).perform(click());
        onView(isAssignableFrom(EditText.class)).perform(typeText("Mdfsdf"), pressKey(KeyEvent.KEYCODE_ENTER));

        onView(withId(R.id.empty))
                .check(matches(isDisplayed()));

    }

    private OsmResponse getOsmResponse() throws IOException {
        InputStream
                stream = InstrumentationRegistry.getContext().getAssets().open("response.json");

        InputStreamReader reader = new InputStreamReader(stream);
        return new Gson().fromJson(reader, OsmResponse.class);
    }

    private Intent launchTestIntent() {
        Intent intent = new Intent();
        intent.putExtra(MainActivity.test, true);
        return intent;
    }

    public class FabIdlingResource implements IdlingResource {

        private ResourceCallback resourceCallback;

        @Override
        public String getName() {
            return FabIdlingResource.class.getName();
        }

        @Override
        public boolean isIdleNow() {

            if (activityRule.getActivity() == null) {
                return false;
            }

            View view = activityRule.getActivity().findViewById(R.id.navigate);

            if (view != null && view.getVisibility() == View.VISIBLE) {
                resourceCallback.onTransitionToIdle();
                return true;
            }
            return false;
        }

        @Override
        public void registerIdleTransitionCallback(ResourceCallback callback) {
            this.resourceCallback = callback;

        }
    }
}

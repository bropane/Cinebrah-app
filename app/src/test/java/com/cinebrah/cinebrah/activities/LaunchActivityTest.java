package com.cinebrah.cinebrah.activities;

import android.test.ActivityInstrumentationTestCase2;

public class LaunchActivityTest extends ActivityInstrumentationTestCase2<LaunchActivity> {

    LaunchActivity activity;

    public LaunchActivityTest() {
        super(LaunchActivity.class);
        activity = getActivity();
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testOnCreate() throws Exception {
        assertEquals(true, activity.gettingGcm);
    }
}
package com.example.android.camera2.basic

import android.Manifest
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MainInstrumentedTest {

    @get:Rule
    val permissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA)

    @get:Rule
    val activityRule: ActivityTestRule<CameraActivity> =
            ActivityTestRule(CameraActivity::class.java)

    @Test
    fun useAppContext() {
        // Context of the app under test
        val context = ApplicationProvider.getApplicationContext() as Context
        assertEquals("com.android.example.camera2.basic", context.packageName)
    }
}
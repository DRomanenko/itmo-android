package com.github.dromanenko.test

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.LinearLayout
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.*
import com.github.dromanenko.test.R.string.*
import org.hamcrest.Matchers.notNullValue
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test


class UiTests {
    companion object {
        const val LAUNCH_TIMEOUT = 5000L
        const val CORRECT_QUERY = "+7"
        const val INCORRECT_QUERY = "7-8-4-5-4-5-4-8-7-8-7-8"
        const val SAMPLE_PACKAGE = "com.github.dromanenko.test"
    }

    private lateinit var device: UiDevice

    @Before
    fun startMainActivityFromHomeScreen() {
        // Initialize UiDevice instance
        device = UiDevice.getInstance(getInstrumentation())

        // Start from the home screen
        device.pressHome()

        // Wait for launcher
        val launcherPackage: String = getLauncherPackageName()
        assertThat(launcherPackage, notNullValue())
        device.wait(
            Until.hasObject(By.pkg(launcherPackage).depth(0)),
            LAUNCH_TIMEOUT
        )

        // Launch the blueprint app
        val context = getApplicationContext<Context>()
        val intent = context.packageManager
            .getLaunchIntentForPackage(SAMPLE_PACKAGE)
        intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK) // Clear out any previous instances
        context.startActivity(intent)

        // Wait for the app to appear
        device.wait(
            Until.hasObject(
                By.pkg(SAMPLE_PACKAGE).depth(0)
            ), LAUNCH_TIMEOUT
        )
    }

    @Test
    fun checkMainActivityCorrectQuery() {
        checkMainActivity(message_correct_query.toString(), CORRECT_QUERY, true)
    }

    @Test
    fun checkMainActivityIncorrectQuery() {
        checkMainActivity(message_incorrect_query.toString(), INCORRECT_QUERY, false)
    }

    private fun checkMainActivity(message: String, query: String, type: Boolean) {
        val search: UiObject = device.findObject(
            UiSelector().className("android.widget.SearchView")
        )
        assertNotNull(message_sw_not_found.toString(), search)
        search.legacySetText(query)
        val contactList = device.wait(
            Until.findObject(
                By.clazz(androidx.recyclerview.widget.RecyclerView::class.java)
            ), 10
        )
        assertNotNull(message_rw_not_found.toString(), contactList)
        val filters = contactList.findObjects(By.clazz(LinearLayout::class.java).depth(2))
        if (type)
            assertNotEquals(message, 0, filters.size)
        else
            assertEquals(message, 0, filters.size)
        device.pressHome()
    }

    private fun getLauncherPackageName(): String {
        // Create launcher Intent
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)

        // Use PackageManager to get the launcher package name
        val pm = getApplicationContext<Context>().packageManager
        val resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return resolveInfo!!.activityInfo.packageName
    }
}
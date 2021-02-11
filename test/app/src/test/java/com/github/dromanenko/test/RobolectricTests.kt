package com.github.dromanenko.test

import android.app.Activity
import android.content.Context
import android.database.Cursor
import android.os.Build
import android.provider.ContactsContract
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.SearchView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.test.rule.GrantPermissionRule
import kotlinx.android.synthetic.main.activity_main.*
import org.hamcrest.CoreMatchers.sameInstance
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config
import org.robolectric.fakes.RoboCursor


@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class RobolectricTests {
    companion object {
        private val CONTACTS_COLUMNS = listOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        private lateinit var activity: Activity
        private lateinit var activityController: ActivityController<MainActivity>

    }

    @get:Rule
    val permissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(android.Manifest.permission.READ_CONTACTS)

    @Before
    fun setUp() {
        activityController = Robolectric.buildActivity(MainActivity::class.java)
        activity = activityController.get()
        shadowOf(activity.contentResolver).setCursor(RoboCursor().apply {
            setColumnNames(CONTACTS_COLUMNS)
            setResults(
                testContacts.map {
                    arrayOf(it.name, it.phoneNumber)
                }.toTypedArray()
            )
        })
    }

    @After
    fun tearDown() {
        activityController.stop()
    }

    @Test
    fun activityCorrectlyShown() {
        activity = activityController.create().get()
        assertNotNull(activity)
    }

    @Test
    fun searchViewCorrectlyShown() {
        activity = activityController.create().get()
        val searchView = activity.sw
        assertNotNull(searchView)
        assertTrue(searchView is SearchView)
        assertTrue(searchView.isVisible)
    }

    @Test
    fun rwNotNull() {
        activity = activityController.create().get()
        assertNotNull(activity.rw)
    }

    @Test
    fun rwAdapterNotNull() {
        activity = activityController.create().get()
        val checkNotNull = activity.findViewById<RecyclerView?>(R.id.rw)
            .checkNotNull().adapter
            .checkNotNull()
        assertTrue(checkNotNull is ContactAdapter)
    }

    @Test
    fun emptyRwAdapterAfterStart() {
        activity = activityController.create().get()
        checkRW(emptyList())
    }

    @Test
    fun emptyQueryOnStart() {
        search(EMPTY_QUERY)
    }

    @Test
    fun correctRussianQuery() {
        search(CORRECT_RUSSIAN_QUERY)
    }

    @Test
    fun incorrectRussianQuery() {
        search(INCORRECT_RUSSIAN_QUERY)
    }

    @Test
    fun correctEnglishQuery() {
        search(CORRECT_ENGLISH_QUERY)
    }

    @Test
    fun incorrectEnglishQuery() {
        search(INCORRECT_ENGLISH_QUERY)
    }

    @Test
    fun incorrectQuery() {
        search(INCORRECT_QUERY)
    }

    @Test
    fun correctPhoneNumberQuery() {
        search(CORRECT_PHONE_NUMBER)
    }

    @Test
    fun incorrectPhoneNumberQuery() {
        search(INCORRECT_PHONE_NUMBER)
    }

    @Test
    fun substringNameQuery() {
        search(SUBSTRING_NAME_QUERY)
    }

    @Test
    fun substringPhoneNumberQuery() {
        search(SUBSTRING_PHONE_NUMBER_QUERY)
    }

    @Test
    fun canGetAndSetQuery() {
        activity = activityController.create().get()
        activity.sw.checkNotNull().setQuery("android", false)
        assertEquals(activity.sw.checkNotNull().query.toString(), "android")
    }

    @Test
    fun canGetAndSetSuggestionsAdapter() {
        activity = activityController.create().get()
        val adapter: CursorAdapter = object : CursorAdapter(activity, null, false) {
            override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?): View? {
                return null
            }

            override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
            }
        }
        activity.sw.checkNotNull().suggestionsAdapter = adapter
        assertThat(
            activity.sw.checkNotNull().suggestionsAdapter,
            sameInstance(adapter)
        )
    }

    @Test
    fun canGetAndSetIconified() {
        activity = activityController.create().get()
        val searchView = activity.sw.checkNotNull()
        assertTrue(searchView.isIconified)
        searchView.isIconified = false
        assertFalse(searchView.isIconified)
    }

    @Test
    fun recreateActivity() {
        activity = activityController.create().get()
        activity.recreate()
        checkRW(emptyList())
    }

    private fun <T> T?.checkNotNull(): T {
        assertNotNull(this)
        return this!!
    }

    private fun search(query: String) {
        activity = activityController.create().get()
        activity.sw.checkNotNull().setQuery(query, false)
        checkRW(findContacts(testContacts, query))
    }

    private fun checkRW(expected: List<Contact>) {
        val adapter = activity.findViewById<RecyclerView?>(R.id.rw)
            .checkNotNull().adapter
            .checkNotNull()
        assertTrue(adapter is ContactAdapter)
        assertEquals((adapter as ContactAdapter).users, expected)
    }
}
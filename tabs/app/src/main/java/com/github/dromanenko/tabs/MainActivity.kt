package com.github.dromanenko.tabs

import android.os.Bundle
import android.util.SparseArray
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.github.dromanenko.tabs.fragments.ContainerFragment
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    companion object {
        const val KEY_NAME = "key_name"
        const val KEY_ARRAY = "key_array"
        const val KEY_ID = "key_id"
    }

    private var curId = R.id.nav_home
    private var order = Stack<Int>()
    private var fragmentsArray = SparseArray<Fragment.SavedState>()

    private val idToStr = mapOf(
        R.id.nav_home to "Home",
        R.id.nav_dashboard to "Dashboard",
        R.id.nav_notifications to "Notifications"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            order = savedInstanceState.getSerializable("kes") as Stack<Int>
            fragmentsArray = savedInstanceState.getSparseParcelableArray(KEY_ARRAY) ?: SparseArray()
            curId = savedInstanceState.getInt(KEY_ID)
            main_bottom_nav?.selectedItemId = curId
        }
        setContentView(R.layout.activity_main)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        main_bottom_nav?.setOnNavigationItemSelectedListener(::listenerFragment)
        main_side_nav?.setNavigationItemSelectedListener(::listenerFragment)
        val fragmentName = idToStr[curId]
        fragmentName?.let { nextFragment(fragmentName, curId) }
    }

    private fun listenerFragment(item: MenuItem): Boolean {
        return when (val name = idToStr[item.itemId]) {
            null -> false
            else -> {
                nextFragment(name, item.itemId)
                return true
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("kes", order)
        outState.putSparseParcelableArray(KEY_ARRAY, fragmentsArray)
        outState.putInt(KEY_ID, curId)
    }

    private fun nextFragment(name: String, itemId: Int) {
        if (supportFragmentManager.findFragmentByTag(name) == null) {
            val curFragment = supportFragmentManager.findFragmentById(R.id.container_fl)
            if (curFragment != null) {
                fragmentsArray.put(
                    curId, supportFragmentManager
                        .saveFragmentInstanceState(curFragment)
                )
            }

            replace(name, itemId)
            pushInStack(itemId)
            curId = itemId
        }
    }

    private fun replace(name: String, nextId: Int): Int {
        return supportFragmentManager.beginTransaction()
            .replace(R.id.container_fl, ContainerFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_NAME, name)
                }
                setInitialSavedState(fragmentsArray[nextId])
            }, name)
            .commit()
    }

    private fun pushInStack(itemId: Int) {
        if (order.isNotEmpty()) {
            if (order.peek() == itemId) {
                return
            }
            if (order.size == idToStr.size) {
                val help = Stack<Int>()
                while (order.isNotEmpty() && order.peek() != itemId) {
                    help.push(order.pop())
                }
                if (order.isNotEmpty()) {
                    order.pop()
                }
                while (help.isNotEmpty()) {
                    order.push(help.pop())
                }
            }
        }
        order.push(itemId)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.fragments[0]
        if (fragment != null) {
            if (fragment.childFragmentManager.backStackEntryCount > 0) {
                fragment.childFragmentManager.popBackStack()
                return
            } else if (fragment.arguments != null) {
                order.pop()
                if (order.isEmpty()) {
                    finish()
                    return
                }
                val nextId: Int = order.peek()
                main_bottom_nav?.selectedItemId = nextId
                idToStr[nextId]?.let { name ->
                    if (fragmentsArray.indexOfKey(nextId) >= 0) {
                        curId = nextId
                        replace(name, nextId)
                    } else {
                        nextFragment(name, nextId)
                    }
                }
                return
            }
        }
        super.onBackPressed()
    }
}

package com.github.dromanenko.test

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_CODE = 38
        private const val PERMISSION_FOR_CONTACTS = Manifest.permission.READ_CONTACTS
    }

    private var contactList: List<Contact> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        updateContacts()

        sw.setOnCloseListener {
            sw.clearFocus()
            updateContacts()
            true
        }

        sw.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                updateDisplay(findContacts(contactList, query.toString()))
                sw.clearFocus()
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                updateDisplay(findContacts(contactList, query.toString()))
                return true
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    displayContacts()
                } else {
                    Toast.makeText(
                        this,
                        R.string.read_contacts_perm,
                        Toast.LENGTH_LONG
                    ).show()
                }
                return
            }
        }
    }

    private fun displayContacts() {
        val viewManager = LinearLayoutManager(this)
        contactList = fetchAllContacts()
        val contactAdapter = ContactAdapter(fetchAllContacts()) {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${it.phoneNumber}")
            startActivity(intent)
        }
        rw.apply {
            adapter = contactAdapter
            layoutManager = viewManager
        }
        Toast.makeText(
            this,
            resources.getQuantityString(
                R.plurals.contacts_toast,
                contactAdapter.itemCount,
                contactAdapter.itemCount
            ),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun updateDisplay(list: List<Contact>) {
        (rw.adapter as ContactAdapter).apply {
            users = list
            notifyDataSetChanged()
        }
    }

    private fun updateContacts() {
        if (!checkPermission(PERMISSION_FOR_CONTACTS)) {
            requestPermission(PERMISSION_FOR_CONTACTS)
        } else {
            displayContacts()
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this@MainActivity,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission(permission: String) = ActivityCompat.requestPermissions(
        this@MainActivity,
        arrayOf(permission),
        REQUEST_CODE
    )
}
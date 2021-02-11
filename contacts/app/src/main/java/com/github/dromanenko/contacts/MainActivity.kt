package com.github.dromanenko.contacts

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import android.content.Intent
import android.net.Uri
import kotlinx.android.synthetic.main.activity_main.rw

class MainActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_CODE = 38
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.READ_CONTACTS),
                REQUEST_CODE
            )
        } else {
            displayContacts()
        }
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
}
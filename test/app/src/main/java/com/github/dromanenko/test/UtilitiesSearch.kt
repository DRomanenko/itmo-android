package com.github.dromanenko.test

import android.content.Context
import android.provider.ContactsContract

data class Contact(val name: String, val phoneNumber: String)

fun Context.fetchAllContacts(): List<Contact> {
    contentResolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        null,
        null,
        null,
        null
    )
        .use { cursor ->
            if (cursor == null) return emptyList()
            val builder = ArrayList<Contact>()
            while (cursor.moveToNext()) {
                val name =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                        ?: "N/A"
                val phoneNumber =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        ?: "N/A"

                builder.add(Contact(name, phoneNumber))
            }
            return builder
        }
}

fun isName(name: String?): Boolean =
    name != null && Regex("^[А-Яа-яA-Za-z. ()]*$")
        .matches(name)

fun isNumber(phoneNumber: String?): Boolean =
    phoneNumber != null && Regex("^[+()\\-\\d ]*$").matches(phoneNumber)

fun findContacts(contacts: List<Contact>, query: String): List<Contact> =
    if (!(isName(query) || isNumber(query))) emptyList() else contacts.filter { contact ->
        var found = false
        (contact.name.split(' ')).forEach {
            found = found || it.startsWith(query, true)
        }
        (contact.phoneNumber.split(' ')).forEach {
            found = found || it.startsWith(query, true)
        }
        return@filter found
    }

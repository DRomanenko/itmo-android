package com.github.dromanenko.test

import org.junit.Assert
import org.junit.Test

class UnitFinderTests {
    @Test
    fun emptyQuery() {
        Assert.assertEquals(testContacts, findContacts(testContacts, ""))
    }

    @Test
    fun correctRussianQuery() {
        assertList(CORRECT_RUSSIAN_QUERY, listOf(2, 4, 5, 8))
    }

    @Test
    fun incorrectRussianQuery() {
        assertList(INCORRECT_RUSSIAN_QUERY, emptyList())
    }

    @Test
    fun correctEnglishQuery() {
        assertList(CORRECT_ENGLISH_QUERY, listOf(0, 1))
    }

    @Test
    fun incorrectEnglishQuery() {
        assertList(INCORRECT_ENGLISH_QUERY, emptyList())
    }

    @Test
    fun incorrectQuery() {
        assertList(INCORRECT_QUERY, emptyList())
    }

    @Test
    fun correctPhoneNumberQuery() {
        assertList(CORRECT_PHONE_NUMBER, listOf(4, 8))
    }

    @Test
    fun incorrectPhoneNumberQuery() {
        assertList(INCORRECT_PHONE_NUMBER, emptyList())
    }

    @Test
    fun substringNameQuery() {
        assertList(SUBSTRING_NAME_QUERY, listOf(3))
    }

    @Test
    fun substringPhoneNumberQuery() {
        assertList(SUBSTRING_PHONE_NUMBER_QUERY, listOf(6))
    }
}
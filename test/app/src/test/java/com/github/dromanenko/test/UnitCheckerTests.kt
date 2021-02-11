package com.github.dromanenko.test

import com.github.dromanenko.test.isName
import com.github.dromanenko.test.isNumber
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UnitCheckerTests {
    private var query: String? = ""

    @Test
    fun nullNameQuery() {
        query = null
        assertFalse(isName(query))
    }

    @Test
    fun correctNameQuery() {
        query = "Yuri Гагарин (космонавт)."
        assertTrue(isName(query))
    }

    @Test
    fun incorrectNameQuery() {
        query = "99Boris-ka73"
        assertFalse(isName(query))
    }

    @Test
    fun nullPhoneNumberQuery() {
        query = null
        assertFalse(isNumber(query))
    }

    @Test
    fun correctPhoneNumberQuery() {
        query = "+7 (926) 123-45-67"
        assertTrue(isNumber(query))
    }

    @Test
    fun incorrectPhoneNumberQuery() {
        query = "+9 (926) bor-is-67"
        assertFalse(isNumber(query))
    }
}
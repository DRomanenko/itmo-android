package com.github.dromanenko.test

import org.junit.Assert

const val EMPTY_QUERY = ""
const val CORRECT_RUSSIAN_QUERY = "б"
const val INCORRECT_RUSSIAN_QUERY = ".!ъ"
const val CORRECT_ENGLISH_QUERY = "j"
const val INCORRECT_ENGLISH_QUERY = "a?$"
const val INCORRECT_QUERY = "JosephineБаланс880084545454"
const val CORRECT_PHONE_NUMBER = "+7"
const val INCORRECT_PHONE_NUMBER = "7-8-4-5-4-5-4-8-7-8-7-8"
val SUBSTRING_NAME_QUERY = "Мой номер".subSequence(4, 6) as String
val SUBSTRING_PHONE_NUMBER_QUERY = "7 845 454 87 87".subSequence(2, 5) as String

fun assertList(query: String, expected: List<Int>) {
    val actualList = mutableListOf<Contact>()
    expected.forEach {
        actualList.add(testContacts[it])
    }
    Assert.assertEquals(actualList, findContacts(testContacts, query))
}

val testContacts = listOf(
    Contact(
        "James Butt",
        "504-621-8927"
    ),
    Contact(
        "Josephine Darakjy",
        "317-234-1135"
    ),
    Contact(
        "Баланс",
        "*100#"
    ),
    Contact(
        "Мой номер",
        "*123#"
    ),
    Contact(
        "Андрей Бреслав",
        "+7 926 123 45 67"
    ),
    Contact(
        "Билл Гейтс",
        "8(926)123-45-67"
    ),
    Contact(
        "Лев Толстой",
        "7 845 454 87 87"
    ),
    Contact(
        "Валера",
        "880084545454"
    ),
    Contact(
        "Борис (дача)",
        "+79855310868"
    )
)
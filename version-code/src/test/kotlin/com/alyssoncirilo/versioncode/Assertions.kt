package com.alyssoncirilo.versioncode

import io.kotest.assertions.withClue
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.comparables.shouldNotBeEqualComparingTo
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.equals.shouldNotBeEqual

fun <T : Comparable<T>> comparisonChecks(a: T, relationship: String, b: T) {
    when (relationship) {
        ">" -> greaterThanChecks(a, b)
        "<" -> lessThanChecks(a, b)
        "==" -> equalityChecks(a, b)
        "!=" -> inequalityChecks(a, b)
        else -> throw IllegalArgumentException("Unknown relationship: $relationship")
    }
}

private fun <T : Comparable<T>> greaterThanChecks(a: T, b: T) {
    inequalityChecks(a, b)
    a shouldBeGreaterThan b
    b shouldBeLessThan a
}

private fun <T : Comparable<T>> lessThanChecks(a: T, b: T) {
    inequalityChecks(a, b)
    a shouldBeLessThan b
    b shouldBeGreaterThan a
}

private fun <T : Comparable<T>> equalityChecks(a: T, b: T) {
    a shouldBeEqualComparingTo b
    b shouldBeEqualComparingTo a
    a shouldBeEqual b
    b shouldBeEqual a
    withClue("hashCode of $a should be the same as $b. ${a.hashCode()} != ${b.hashCode()}") {
        a.hashCode() shouldBeEqual b.hashCode()
    }
}

private fun <T : Comparable<T>> inequalityChecks(a: T, b: T) {
    a shouldNotBeEqualComparingTo b
    b shouldNotBeEqualComparingTo a
    a shouldNotBeEqual b
    b shouldNotBeEqual a
}

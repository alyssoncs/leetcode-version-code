package com.alyssoncirilo.versioncode

import kotlin.math.pow

class VersionCode private constructor(
    private val components: List<VersionComponent>,
) : Comparable<VersionCode> {

    init {
        components.forEach(::checkVersionComponent)
    }

    val value: Int = run {
        val reversedComponents = components.reversed()
        val shifts = reversedComponents.scan(0) { acc, component ->
            acc + component.bits
        }
        reversedComponents.zip(shifts).fold(0) { acc, (component, shift) ->
            acc or (component.value shl shift)
        }
    }

    override fun compareTo(other: VersionCode): Int {
        return this.value - other.value
    }

    override fun toString(): String {
        return "$value (${components.joinToString(separator = ".") { it.value.toString() }})"
    }

    operator fun get(component: String): Int? {
        return components.firstOrNull { it.displayName == component }?.value
    }

    private fun checkVersionComponent(component: VersionComponent) {
        require(component.isValid(component.value)) {
            val violation = if (component.value < 0)
                "not be negative"
            else
                "be no more than ${component.maxValue} (2^${component.bits}-1)"

            "${component.displayName} should $violation, but is ${component.value}"
        }
    }

    private class VersionComponent(
        val displayName: String,
        val bits: Int,
        val value: Int,
    ) {
        val maxValue = (2 toThe bits) - 1

        fun isValid(version: Int) = version in 0..maxValue

        private infix fun Int.toThe(exponent: Int): Int {
            return toDouble().pow(exponent).toInt()
        }
    }

    class Factory(private vararg val schema: ComponentSchema) {
        fun create(vararg values: Int): VersionCode {
            val components = values.zip(schema).map { (component, componentSchema) ->
                VersionComponent(
                    displayName = componentSchema.displayName,
                    bits = componentSchema.bits.value,
                    value = component,
                )
            }
            return VersionCode(components)
        }
    }

    class ComponentSchema private constructor(internal val displayName: String, internal val bits: Bits) {
        companion object {
            infix fun String.takes(bits: Bits): ComponentSchema = ComponentSchema(displayName = this, bits = bits)
        }
    }

    @JvmInline
    value class Bits private constructor(internal val value: Int) {
        companion object {
            val Int.bits: Bits get() = Bits(this)
        }
    }
}

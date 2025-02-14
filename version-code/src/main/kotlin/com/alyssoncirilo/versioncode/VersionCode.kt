package com.alyssoncirilo.versioncode

import com.alyssoncirilo.versioncode.VersionCode.ComponentSchema.Companion.takes
import kotlin.math.max

class VersionCode private constructor(
    private val components: List<VersionComponent>,
) : Comparable<VersionCode> {

    val value: Int = run {
        val reversedComponents = components.reversed()
        val shifts = reversedComponents.scan(0) { acc, component ->
            acc + component.bits
        }
        reversedComponents.zip(shifts).fold(0) { acc, (component, shift) ->
            acc or (component.value shl shift)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is VersionCode) return false

        return this.compareTo(other) == 0
    }

    override fun hashCode(): Int {
        return components.map { it.value }.hashCode()
    }

    override fun compareTo(other: VersionCode): Int {
        return components.zip(other.components)
            .firstOrNull { (a, b) -> a.value != b.value }
            ?.let { (a, b) -> a.value - b.value }
            ?: 0
    }

    override fun toString(): String {
        return "$value (${components.joinToString(separator = ".") { it.value.toString() }})"
    }

    operator fun get(component: String): Int? {
        return components.firstOrNull { it.bits != 0 && it.displayName == component }?.value
    }

    private data class VersionComponent(
        val displayName: String,
        val bits: Int,
        val value: Int,
    ) {
        val maxValue = run {
            val shift = max(0, bits - 1)
            (2 shl shift) - 1
        }

        init {
            require(value >= 0) { "$displayName should not be negative, but is $value" }
            require(value <= maxValue) { "$displayName should be no more than $maxValue (2^$bits-1), but is $value" }
        }

        companion object {
            val EMPTY = VersionComponent(displayName = "", bits = 0, value = 0)
        }
    }

    class Factory(private vararg val schema: ComponentSchema) {

        constructor(vararg componentBits: Bits) : this(
            *componentBits.mapIndexed { idx, bits ->
                "Component $idx" takes bits
            }.toTypedArray(),
        )

        init {
            validateSchema()
        }

        fun create(vararg values: Int): VersionCode {
            validateComponents(values)
            val components = values.zip(schema).map { (component, componentSchema) ->
                VersionComponent(
                    displayName = componentSchema.displayName,
                    bits = componentSchema.bits.value,
                    value = component,
                )
            }
            val normalizedComponents = components + List((Int.SIZE_BITS - 1) - components.size) {
                VersionComponent.EMPTY
            }
            return VersionCode(normalizedComponents)
        }

        private fun validateSchema() {
            require(schema.isNotEmpty()) {
                "Schema should not be empty"
            }

            val componentsTotalSize = schema.sumOf { it.bits.value }
            require(componentsTotalSize < Int.SIZE_BITS) {
                "All components combined should not take more than 31 bits, but total is $componentsTotalSize"
            }

            require(schema.size == schema.distinctBy { it.displayName }.size) {
                "No component should have duplicate names"
            }
        }

        private fun validateComponents(values: IntArray) {
            require(values.size == schema.size) {
                if (values.size < schema.size)
                    missingComponentMessage(numberOfMissingComponents = schema.size - values.size)
                else
                    tooManyComponentsMessage(values.size)
            }
        }

        private fun tooManyComponentsMessage(componentsSize: Int): String {
            return "Expected ${schema.size} components, but got $componentsSize"
        }

        private fun missingComponentMessage(numberOfMissingComponents: Int): String {
            return "Missing value for: ${missingComponentsString(numberOfMissingComponents)}"
        }

        private fun missingComponentsString(numberOfMissingComponents: Int): String {
            val missingComponents = schema.takeLast(numberOfMissingComponents)
            val lastMissing = missingComponents.last()
            return if (missingComponents.size == 1) {
                lastMissing.displayName
            } else {
                "${
                    missingComponents.dropLast(1).joinToString(separator = ", ") { it.displayName }
                } and ${lastMissing.displayName}"
            }
        }
    }

    class ComponentSchema private constructor(internal val displayName: String, internal val bits: Bits) {
        init {
            validate()
        }

        private fun validate() {
            require(displayName.isNotBlank()) {
                "No component should have blank name"
            }

            require(bits.value >= 0) {
                "No component should have negative size, but $displayName is ${bits.value}"
            }

            require(bits.value != 0) {
                "All components should have positive sizes, but $displayName is zero"
            }
        }

        companion object {
            infix fun String.takes(bits: Bits): ComponentSchema = ComponentSchema(displayName = this, bits = bits)
        }
    }

    class Bits private constructor(internal val value: Int) {
        companion object {
            val Int.bits: Bits get() = bit
            val Int.bit: Bits get() = Bits(this)
        }
    }
}

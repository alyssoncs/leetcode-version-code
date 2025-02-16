package com.alyssoncirilo.versioncode

import com.alyssoncirilo.versioncode.VersionCode.ComponentSchema.Companion.takes
import kotlin.math.max

/**
 * Represents a version composed of several components.
 *
 * Each component occupies a user-defined number of bits. The components together encode the version as a single
 * integer value. Note that the total number of bits allocated for all components must be less than [Int.SIZE_BITS]
 * (i.e. only 31 bits are available for version components).
 *
 * Instances of [VersionCode] are created using the nested [Factory] class.
 *
 * Example:
 * ```
 * // Define a version with three components: "Major", "Minor", and "Patch"
 * val factory = VersionCode.Factory(
 *     "Major" takes 6.bits,
 *     "Minor" takes 21.bits,
 *     "Patch" takes 4.bits,
 * )
 *
 * // Create a version code for version 1.2.3.
 * val version = factory.create(1, 2, 3)
 *
 * defaultConfig {
 *     versionCode = version.value
 * }
 * ```
 */
class VersionCode private constructor(
    private val components: List<VersionComponent>,
) : Comparable<VersionCode> {

    /**
     * The encoded version number.
     */
    val value: Int = run {
        val reversedComponents = components.reversed()
        val shifts = reversedComponents.scan(0) { acc, component ->
            acc + component.bits
        }
        reversedComponents.zip(shifts).fold(0) { acc, (component, shift) ->
            acc or (component.value shl shift)
        }
    }

    /**
     * Compares this [VersionCode] with another object for equality.
     *
     * The comparison follows the same logic as [compareTo]
     *
     * @param other the object to compare with.
     * @return `true` if this [VersionCode] is equal to [other], `false` otherwise.
     */
    override fun equals(other: Any?): Boolean {
        if (other !is VersionCode) return false

        return this.compareTo(other) == 0
    }

    /**
     * Returns a hash code value for this [VersionCode].
     *
     * The hash code is computed based on the values of the version components.
     * Versions that are considered equal according to [equals] will produce the same hash.
     *
     * @return the hash code value for this [VersionCode].
     */
    override fun hashCode(): Int {
        return components.map { it.value }.hashCode()
    }

    /**
     * Compares this [VersionCode] with another version code.
     *
     * The comparison is performed component-by-component in the order they are stored. Only the numerical value
     * of each component is considered. Bit allocation details or display names are not taken into account.
     * This design allows version codes created using different underlying schemas (i.e. with different bit
     * allocations or numbers of components) to be compared.
     *
     * We compare version codes by identifying the most significant component where both version differ, assuming
     * missing components are 0; this component alone determines which version is greater.
     * If all components are equal, the versions are considered the same.
     *
     * Examples:
     * ```
     * // Example 1: Comparing versions with the same schema
     * val versionA = factory.create(1, 5)  // e.g., Major 1, Minor 5
     * val versionB = factory.create(1, 10) // e.g., Major 1, Minor 10
     *
     * println(versionA < versionB) // true, because 5 < 10
     *
     * // Example 2: Comparing versions with different schemas
     * // Suppose we have two factories:
     * // factoryC is defined with a schema for Major and Minor components.
     * // factoryD is defined with a schema for Major, Minor, and Patch components.
     * val versionC = factoryC.create(2, 0) // When comparing with the next version, Patch will be considered 0
     * val versionD = factoryD.create(2, 0, 3) // Patch is explicitly 3
     *
     * println(versionC < versionD)  // true, since versionC's Patch (default 0) is less than 3
     * ```
     *
     * @param other the [VersionCode] to compare with.
     * @return a negative integer if this version is less than [other], zero if equal, or a positive integer if greater.
     */
    override fun compareTo(other: VersionCode): Int {
        return components.zip(other.components)
            .firstOrNull { (a, b) -> a.value != b.value }
            ?.let { (a, b) -> a.value - b.value }
            ?: 0
    }

    /**
     * Returns a string representation of the version.
     *
     * The string includes both the encoded value and the component values.
     */
    override fun toString(): String {
        return "$value (${components.joinToString(separator = ".") { it.value.toString() }})"
    }

    /**
     * Returns the value of a version component, identified by its display name.
     *
     * @param component the display name of the version component.
     * @return the value of the specified component, or `null` if no such component exists.
     */
    operator fun get(component: String): Int? {
        return components.firstOrNull { it.bits != 0 && it.displayName == component }?.value
    }

    /**
     * Factory for creating [VersionCode] instances.
     *
     * The factory is initialized with a schema that defines the display name and bit allocation for each version
     * component.
     *
     * @throws IllegalArgumentException if any of the * following conditions are not met:
     * - **Non-Empty Schema:** The schema must not be empty.
     * - **Valid Component Names:** Each component's display name must be non-blank and unique.
     * - **Bit Allocation Limit:** The total bits allocated across all components must be less than [Int.SIZE_BITS]
     * (i.e. only 31 bits are available)
     *
     * Example:
     * ```
     * // Create a factory with a custom schema.
     * val factory = VersionCode.Factory("Major" takes 4.bits, "Minor" takes 4.bits, "Patch" takes 4.bits)
     *
     * VersionCode.Factory() // Throws exception (empty schema)
     * VersionCode.Factory("" takes 2.bits) // Throws exception (blank component name)
     * VersionCode.Factory("a" takes 2.bits, "a" takes 3.bits) // Throws exception (duplicated component name)
     * VersionCode.Factory("a" takes 20.bits, "b" takes 20.bits) // Throws exception (exceeded 31 bits limit)
     * ```
     */
    class Factory(private vararg val schema: ComponentSchema) {

        /**
         * Secondary constructor for creating a factory using only bit sizes.
         *
         * In this case, display names are auto-generated (e.g., "Component 0", "Component 1", etc.).
         *
         * Example:
         * ```
         * // Create a factory with three components
         * val factory = VersionCode.Factory(1.bit, 20.bits, 10.bits)
         * ```
         */
        constructor(vararg componentBits: Bits) : this(
            *componentBits.mapIndexed { idx, bits ->
                "Component $idx" takes bits
            }.toTypedArray(),
        )

        init {
            validateSchema()
        }

        /**
         * Creates a new [VersionCode] instance using the provided component values.
         *
         * The number of values must exactly match the number of components defined in the factory's schema.
         * In addition, each provided value must be within the allowed range for its corresponding component.
         * For a component allocated N bits, the allowed range is from 0 to 2^N - 1.
         *
         * @param values the version component values in the order defined by the factory's schema.
         * @return a new [VersionCode] instance representing the encoded version.
         * @throws IllegalArgumentException if the number of values does not equal the number of schema components,
         * or if any provided value is not within the allowed range for its component.
         *
         * Example:
         * ```
         * // For a schema with three components.
         * val version = factory.create(1, 2, 3)
         * ```
         */
        fun create(vararg values: Int): VersionCode {
            validateComponents(values)
            val components = values.zip(schema).map { (component, componentSchema) ->
                VersionComponent(
                    displayName = componentSchema.displayName,
                    bits = componentSchema.bits.value,
                    value = component,
                )
            }
            val normalizedComponents = components + List(COMPONENTS_LIMIT - components.size) {
                VersionComponent.EMPTY
            }
            return VersionCode(normalizedComponents)
        }

        private fun validateSchema() {
            require(schema.isNotEmpty()) {
                "Schema should not be empty"
            }

            val componentsTotalSize = schema.sumOf { it.bits.value }
            require(componentsTotalSize <= COMPONENTS_LIMIT) {
                "All components combined should not take more than $COMPONENTS_LIMIT bits, " +
                    "but total is $componentsTotalSize"
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

        companion object {
            private const val COMPONENTS_LIMIT = Int.SIZE_BITS - 1
        }
    }

    /**
     * Defines a schema for a version component.
     *
     * Each schema entry specifies a component's display name and the number of bits allocated for that component.
     *
     * @throws IllegalArgumentException if any of the following constraints is violated:
     * - The display name must be non-blank.
     * - The bit allocation must be a positive number.
     *
     * Use the infix function [ComponentSchema.Companion.takes] to create a new schema.
     *
     * Example:
     * ```
     * val majorSchema = "Major" takes 8.bits // Valid
     *
     * val invalidSchema = "" takes 4.bits // Throws exception
     * val negativeBits = "Minor" takes (-2).bits // Throws exception
     * val zeroBits = "Patch" takes 0.bits // Throws exception
     * ```
     */

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

    /**
     * Represents the number of bits allocated to a version component.
     *
     * Use the extension properties [Int.bit] or [Int.bits] to create an instance.
     *
     * Example:
     * ```
     * val oneBit = 1.bit
     * val fourBits = 4.bits
     * ```
     */
    class Bits private constructor(internal val value: Int) {
        companion object {
            val Int.bits: Bits get() = bit
            val Int.bit: Bits get() = Bits(this)
        }
    }

    private data class VersionComponent(
        val displayName: String,
        val bits: Int,
        val value: Int,
    ) {
        private val maxValue = run {
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
}

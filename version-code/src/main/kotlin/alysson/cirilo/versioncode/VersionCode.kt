package alysson.cirilo.versioncode

import kotlin.math.pow

class VersionCode(
    schema: List<Pair<String, Int>>,
    vararg values: Int,
) : Comparable<VersionCode> {

    private val components = values.zip(schema).map { (component, componentSchema) ->
        VersionComponent(displayName = componentSchema.first, bits = componentSchema.second, value = component)
    }

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
}

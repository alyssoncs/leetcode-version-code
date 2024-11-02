package alysson.cirilo.versioncode

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.AnnotationBasedArgumentsProvider
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsSource
import java.util.stream.IntStream
import java.util.stream.Stream

@Repeatable
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ArgumentsSource(IntRangeArgumentsProvider::class)
annotation class IntRangeSource(val start: Int, val end: Int)

private class IntRangeArgumentsProvider : AnnotationBasedArgumentsProvider<IntRangeSource>() {
    override fun provideArguments(context: ExtensionContext?, annotation: IntRangeSource): Stream<out Arguments> {
        return IntStream
            .rangeClosed(annotation.start, annotation.end)
            .mapToObj(Arguments::of)
    }
}

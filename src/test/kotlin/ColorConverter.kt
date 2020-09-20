import javafx.scene.paint.Color
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.params.converter.ArgumentConversionException
import org.junit.jupiter.params.converter.ArgumentConverter


internal class ColorConverter : ArgumentConverter {
    @Throws(ArgumentConversionException::class)
    override fun convert(source: Any, context: ParameterContext): Any {
        require(source is String) { "The argument should be a string: $source" }
        return try {
            Color.web(source)
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to convert", e)
        }
    }
}
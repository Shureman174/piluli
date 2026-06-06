import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

fun calculateAverage(numbers: List<Double>): Double {
    if (numbers.isEmpty()) {
        throw IllegalArgumentException("The list of numbers cannot be empty.")
    }

    val sum = numbers.sumOf { it }
    return sum / numbers.size
}

class MainTest {
    @Test
    fun testCalculateAverage() {
    val numbers = listOf(10.0, 20.0, 30.0, 40.0)
        assertEquals(25.0, calculateAverage(numbers))
}
}

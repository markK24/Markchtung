import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sign
import kotlin.math.sin

fun calculateArc(start: Pair<Float, Float>, radius: Double, heading: Double, angle: Double): Pair<Float, Float> {
    val headingTowardsCenter = heading + sign(angle) * PI / 2

    val (x, y) = start
    val cX = x + cos(headingTowardsCenter) * radius
    val cY = y + sin(headingTowardsCenter) * radius
    val x1 = cX - cos(headingTowardsCenter + angle) * radius
    val y1 = cY - sin(headingTowardsCenter + angle) * radius


    return x1.toFloat() to y1.toFloat()
}
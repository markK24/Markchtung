
import Constants.MOVE_PER_TICK
import Constants.TURN_PER_TICK
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.PathNode
import androidx.compose.ui.graphics.vector.toPath
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class Player private constructor(val name: String, val color: Color) {
    companion object {
        @Suppress("SpellCheckingInspection")
        val list = arrayOf(
            Player("Goldy", Color(0xFFDAA520)), // Gold
            Player("Indiego", Color(0xFF4B0082)), // Indigo
            Player("Malachite", Color(0xFF0BDA51)), // Malachite
            Player("Minthryl", Color(0xFF3EB489)), // Mint
            Player("Bloody", Color(0xFF660000)), // Blood-red
            Player("Manghoost", Color(0xFFF8F8FF)), // Ghost white
        )
    }

    class State {
        var score by mutableStateOf(0)
        var speed by mutableStateOf(1.0)

        private var turning by mutableStateOf(0)
        private val manoeuvre = (Random.nextDouble(50.0, 450.0) to Random.nextDouble(50.0, 450.0)).let { (x, y) ->
            mutableStateListOf(
                PathNode.MoveTo(x.toFloat(), y.toFloat()),
                PathNode.LineTo(x.toFloat(), y.toFloat())
            )}
        private var headingAtManoeuvreStart by mutableStateOf(Random.nextDouble(2 * PI))
        private var manoeuvreTicks by mutableStateOf(0.0)
        private var fullCircle by mutableStateOf(false)

        val manoeuvrePath by derivedStateOf { manoeuvre.toPath() }

        private val manoeuvreStart by derivedStateOf {
            manoeuvre[manoeuvre.lastIndex-1].let {
                when (it) {
                    is PathNode.MoveTo -> it.x to it.y
                    is PathNode.LineTo -> it.x to it.y
                    is PathNode.ArcTo -> it.arcStartX to it.arcStartY
                    else -> error("Unsupported path node: ${it::class}")
                }
            }
        }
        val location by derivedStateOf {
            manoeuvre.last().let {
                when (it) {
                    is PathNode.MoveTo -> it.x to it.y
                    is PathNode.LineTo -> it.x to it.y
                    is PathNode.ArcTo -> it.arcStartX to it.arcStartY
                    else -> error("Unsupported path node: ${it::class}")
                }
            }
        }

        val heading by derivedStateOf {
            if (turning == 0) headingAtManoeuvreStart else headingAtManoeuvreStart + TURN_PER_TICK * manoeuvreTicks / speed * turning
        }


        fun update(dticks: Int) {
            manoeuvreTicks += dticks.toDouble() * speed

            if (turning == 0) {
                val x =
                    manoeuvreStart.first + cos(headingAtManoeuvreStart) * MOVE_PER_TICK * manoeuvreTicks
                val y =
                    manoeuvreStart.second + sin(headingAtManoeuvreStart) * MOVE_PER_TICK * manoeuvreTicks

                manoeuvre.removeLast()
                manoeuvre.add(PathNode.LineTo(x.toFloat(), y.toFloat()))
            } else {
                val radius = (MOVE_PER_TICK / TURN_PER_TICK) * speed // If speed changes, start a new manoeuvre!!!

                val angle = heading - headingAtManoeuvreStart

                val (x, y) = calculateArc(manoeuvreStart, radius, headingAtManoeuvreStart, angle)

                manoeuvre.removeLast()

                if (abs(angle) > PI * 2 && !fullCircle) {
                    manoeuvre.addAll(
                        listOf(
                            PathNode.ArcTo(
                                horizontalEllipseRadius = radius.toFloat(),
                                verticalEllipseRadius = radius.toFloat(),
                                theta = 0f,
                                isPositiveArc = turning > 0,
                                isMoreThanHalf = abs(angle % (2 * PI)) > PI,
                                arcStartX = x,
                                arcStartY = y,
                            ),
                            PathNode.ArcTo(
                                horizontalEllipseRadius = radius.toFloat(),
                                verticalEllipseRadius = radius.toFloat(),
                                theta = 0f,
                                isPositiveArc = turning > 0,
                                isMoreThanHalf = abs(angle % (2 * PI)) < PI,
                                arcStartX = location.first,
                                arcStartY = location.second,
                            ),
                        )
                    )
                    fullCircle = true
                }

                manoeuvre.add(
                    PathNode.ArcTo(
                        horizontalEllipseRadius = radius.toFloat(),
                        verticalEllipseRadius = radius.toFloat(),
                        theta = 0f,
                        isPositiveArc = turning > 0,
                        isMoreThanHalf = abs(angle % (2 * PI)) > PI,
                        arcStartX = x,
                        arcStartY = y
                    )
                )
            }

            collisionDetection()
        }

        private fun collisionDetection() {
            // TODO
        }

        private fun splitManoeuvre() {
            headingAtManoeuvreStart = heading
            manoeuvreTicks = 0.0
            fullCircle = false

            manoeuvre.add(PathNode.LineTo(location.first, location.second))
        }


        fun startTurn(left: Boolean) {
            if ((left && turning != 1) || (!left && turning != -1)) {
                splitManoeuvre()
                turning = if (left) 1 else -1
            }
        }

        fun endTurn(left: Boolean) {
            if ((left && turning == 1) || (!left && turning == -1)) {
                splitManoeuvre()
                turning = 0
            }
        }
    }
}

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.PathNode
import androidx.compose.ui.graphics.vector.toPath
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.browser.window
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import markchtung.composeapp.generated.resources.PoetsenOne_Regular
import markchtung.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.Font
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

val KEYMAP = arrayOf(
    "", // [0]
    "", // [1]
    "", // [2]
    "CANCEL", // [3]
    "", // [4]
    "", // [5]
    "HELP", // [6]
    "", // [7]
    "BACK_SPACE", // [8]
    "TAB", // [9]
    "", // [10]
    "", // [11]
    "CLEAR", // [12]
    "ENTER", // [13]
    "ENTER_SPECIAL", // [14]
    "", // [15]
    "SHIFT", // [16]
    "CONTROL", // [17]
    "ALT", // [18]
    "PAUSE", // [19]
    "CAPS_LOCK", // [20]
    "KANA", // [21]
    "EISU", // [22]
    "JUNJA", // [23]
    "FINAL", // [24]
    "HANJA", // [25]
    "", // [26]
    "ESCAPE", // [27]
    "CONVERT", // [28]
    "NONCONVERT", // [29]
    "ACCEPT", // [30]
    "MODECHANGE", // [31]
    "SPACE", // [32]
    "PAGE_UP", // [33]
    "PAGE_DOWN", // [34]
    "END", // [35]
    "HOME", // [36]
    "LEFT", // [37]
    "UP", // [38]
    "RIGHT", // [39]
    "DOWN", // [40]
    "SELECT", // [41]
    "PRINT", // [42]
    "EXECUTE", // [43]
    "PRINTSCREEN", // [44]
    "INSERT", // [45]
    "DELETE", // [46]
    "", // [47]
    "0", // [48]
    "1", // [49]
    "2", // [50]
    "3", // [51]
    "4", // [52]
    "5", // [53]
    "6", // [54]
    "7", // [55]
    "8", // [56]
    "9", // [57]
    "COLON", // [58]
    "SEMICOLON", // [59]
    "LESS_THAN", // [60]
    "EQUALS", // [61]
    "GREATER_THAN", // [62]
    "QUESTION_MARK", // [63]
    "AT", // [64]
    "A", // [65]
    "B", // [66]
    "C", // [67]
    "D", // [68]
    "E", // [69]
    "F", // [70]
    "G", // [71]
    "H", // [72]
    "I", // [73]
    "J", // [74]
    "K", // [75]
    "L", // [76]
    "M", // [77]
    "N", // [78]
    "O", // [79]
    "P", // [80]
    "Q", // [81]
    "R", // [82]
    "S", // [83]
    "T", // [84]
    "U", // [85]
    "V", // [86]
    "W", // [87]
    "X", // [88]
    "Y", // [89]
    "Z", // [90]
    "OS_KEY", // [91] Windows Key (Windows) or Command Key (Mac)
    "", // [92]
    "CONTEXT_MENU", // [93]
    "", // [94]
    "SLEEP", // [95]
    "NUMPAD0", // [96]
    "NUMPAD1", // [97]
    "NUMPAD2", // [98]
    "NUMPAD3", // [99]
    "NUMPAD4", // [100]
    "NUMPAD5", // [101]
    "NUMPAD6", // [102]
    "NUMPAD7", // [103]
    "NUMPAD8", // [104]
    "NUMPAD9", // [105]
    "MULTIPLY", // [106]
    "ADD", // [107]
    "SEPARATOR", // [108]
    "SUBTRACT", // [109]
    "DECIMAL", // [110]
    "DIVIDE", // [111]
    "F1", // [112]
    "F2", // [113]
    "F3", // [114]
    "F4", // [115]
    "F5", // [116]
    "F6", // [117]
    "F7", // [118]
    "F8", // [119]
    "F9", // [120]
    "F10", // [121]
    "F11", // [122]
    "F12", // [123]
    "F13", // [124]
    "F14", // [125]
    "F15", // [126]
    "F16", // [127]
    "F17", // [128]
    "F18", // [129]
    "F19", // [130]
    "F20", // [131]
    "F21", // [132]
    "F22", // [133]
    "F23", // [134]
    "F24", // [135]
    "", // [136]
    "", // [137]
    "", // [138]
    "", // [139]
    "", // [140]
    "", // [141]
    "", // [142]
    "", // [143]
    "NUM_LOCK", // [144]
    "SCROLL_LOCK", // [145]
    "WIN_OEM_FJ_JISHO", // [146]
    "WIN_OEM_FJ_MASSHOU", // [147]
    "WIN_OEM_FJ_TOUROKU", // [148]
    "WIN_OEM_FJ_LOYA", // [149]
    "WIN_OEM_FJ_ROYA", // [150]
    "", // [151]
    "", // [152]
    "", // [153]
    "", // [154]
    "", // [155]
    "", // [156]
    "", // [157]
    "", // [158]
    "", // [159]
    "CIRCUMFLEX", // [160]
    "EXCLAMATION", // [161]
    "DOUBLE_QUOTE", // [162]
    "HASH", // [163]
    "DOLLAR", // [164]
    "PERCENT", // [165]
    "AMPERSAND", // [166]
    "UNDERSCORE", // [167]
    "OPEN_PAREN", // [168]
    "CLOSE_PAREN", // [169]
    "ASTERISK", // [170]
    "PLUS", // [171]
    "PIPE", // [172]
    "HYPHEN_MINUS", // [173]
    "OPEN_CURLY_BRACKET", // [174]
    "CLOSE_CURLY_BRACKET", // [175]
    "TILDE", // [176]
    "", // [177]
    "", // [178]
    "", // [179]
    "", // [180]
    "VOLUME_MUTE", // [181]
    "VOLUME_DOWN", // [182]
    "VOLUME_UP", // [183]
    "", // [184]
    "", // [185]
    "SEMICOLON", // [186]
    "EQUALS", // [187]
    "COMMA", // [188]
    "MINUS", // [189]
    "PERIOD", // [190]
    "SLASH", // [191]
    "BACK_QUOTE", // [192]
    "", // [193]
    "", // [194]
    "", // [195]
    "", // [196]
    "", // [197]
    "", // [198]
    "", // [199]
    "", // [200]
    "", // [201]
    "", // [202]
    "", // [203]
    "", // [204]
    "", // [205]
    "", // [206]
    "", // [207]
    "", // [208]
    "", // [209]
    "", // [210]
    "", // [211]
    "", // [212]
    "", // [213]
    "", // [214]
    "", // [215]
    "", // [216]
    "", // [217]
    "", // [218]
    "OPEN_BRACKET", // [219]
    "BACK_SLASH", // [220]
    "CLOSE_BRACKET", // [221]
    "QUOTE", // [222]
    "", // [223]
    "META", // [224]
    "ALTGR", // [225]
    "", // [226]
    "WIN_ICO_HELP", // [227]
    "WIN_ICO_00", // [228]
    "", // [229]
    "WIN_ICO_CLEAR", // [230]
    "", // [231]
    "", // [232]
    "WIN_OEM_RESET", // [233]
    "WIN_OEM_JUMP", // [234]
    "WIN_OEM_PA1", // [235]
    "WIN_OEM_PA2", // [236]
    "WIN_OEM_PA3", // [237]
    "WIN_OEM_WSCTRL", // [238]
    "WIN_OEM_CUSEL", // [239]
    "WIN_OEM_ATTN", // [240]
    "WIN_OEM_FINISH", // [241]
    "WIN_OEM_COPY", // [242]
    "WIN_OEM_AUTO", // [243]
    "WIN_OEM_ENLW", // [244]
    "WIN_OEM_BACKTAB", // [245]
    "ATTN", // [246]
    "CRSEL", // [247]
    "EXSEL", // [248]
    "EREOF", // [249]
    "PLAY", // [250]
    "ZOOM", // [251]
    "", // [252]
    "PA1", // [253]
    "WIN_OEM_CLEAR", // [254]
    "", // [255]
)

@Suppress("SpellCheckingInspection")
val PLAYERS = mapOf(
    "Goldy" to Color(0xFFDAA520), // Gold
    "Indiego" to Color(0xFF4B0082), // Indigo
    "Malachite" to Color(0xFF0BDA51), // Malachite
    "Minthryl" to Color(0xFF3EB489), // Mint
    "Bloody" to Color(0xFF660000), // Blood-red
    "Manghoost" to Color(0xFFF8F8FF), // Ghost white
)

val MAX_PLAYERS = PLAYERS.size

@Composable
fun App() {
    Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
        var inGame by remember { mutableStateOf(false) }

        val players = remember { mutableStateListOf("Goldy", "Indiego") }
        val keys = remember { mutableStateMapOf<Key, Pair<String, Boolean>>() }

        if (!inGame) {
            ChoosePlayers(
                players = players,
                keys = keys,
                addPlayer = {
                    val freeName = PLAYERS.keys.firstOrNull { it !in players }
                    if (freeName != null) {
                        players.add(freeName)
                    }
                },
                removePlayer = { players.remove(it) },
                bindKey = { key, left, player ->
                    keys.filterValues { it.first == player && it.second == left }.keys.forEach { keys.remove(it) }
                    keys[key] = player to left
                },
                startGame = { inGame = true }
            )
        } else {
            Game(
                players = players,
                keys = keys,
                exitGame = { inGame = false })
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChoosePlayers(
    players: List<String>,
    keys: Map<Key, Pair<String, Boolean>>,
    addPlayer: () -> Unit,
    removePlayer: (String) -> Unit,
    bindKey: (Key, Boolean, String) -> Unit,
    startGame: () -> Unit
) {
    val poetsenOneFont = FontFamily(
        Font(Res.font.PoetsenOne_Regular)
    )

    val leftKeys by derivedStateOf {
        buildMap {
            keys.forEach { (key, p) -> if (p.second) set(p.first, key) }
        }
    }
    val rightKeys by derivedStateOf {
        buildMap {
            keys.forEach { (key, p) -> if (!p.second) set(p.first, key) }
        }
    }

    var editing: String? by remember { mutableStateOf(null) }
    var editingLeft by remember { mutableStateOf(true) }

    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = Modifier.focusRequester(focusRequester).focusable().onPreviewKeyEvent { event ->
            if (editing == null) return@onPreviewKeyEvent false
            if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false

            bindKey(event.key, editingLeft, editing!!)

            editing = null
            focusRequester.freeFocus()

            return@onPreviewKeyEvent true
        }.padding(horizontal = 60.dp, vertical = 160.dp).fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "Markchtung", fontSize = 96.sp, color = Color.White, fontFamily = poetsenOneFont)
        Spacer(modifier = Modifier.weight(1f))

        players.forEach { name ->
            val color = PLAYERS.getValue(name)
            Row(
                modifier = Modifier.fillMaxWidth(0.6f),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                var strikethrough by remember { mutableStateOf(false) }
                Text(
                    modifier = Modifier.weight(1f).onPointerEvent(
                        eventType = PointerEventType.Enter,
                        onEvent = { strikethrough = players.size > 2 }
                    ).onPointerEvent(
                        eventType = PointerEventType.Exit,
                        onEvent = { strikethrough = false }
                    ).clickable(enabled = players.size > 2, onClickLabel = "Remove player", role = Role.Button) {
                        if (editing == name) editing = null
                        removePlayer(name)
                    },
                    textAlign = TextAlign.Center,
                    text = name,
                    color = color,
                    fontSize = 48.sp,
                    fontFamily = poetsenOneFont,
                    textDecoration = if (strikethrough) TextDecoration.LineThrough else null,
                )
                Spacer(modifier = Modifier.weight(0.8f))
                Text(
                    modifier = Modifier.weight(1f).clickable(enabled = editing == null, role = Role.Button) {
                        editing = name
                        editingLeft = true
                        focusRequester.requestFocus()
                        focusRequester.captureFocus()
                    },
                    textAlign = TextAlign.Center,
                    text = leftKeys[name]?.keyCode?.let { KEYMAP.getOrNull(it.toInt()) } ?: "Select key",
                    color = color,
                    fontSize = 48.sp,
                    fontFamily = poetsenOneFont,
                    fontStyle = if (editing == name && editingLeft) FontStyle.Italic else FontStyle.Normal,
                )
                Text(
                    modifier = Modifier.weight(1f).clickable(enabled = editing == null, role = Role.Button) {
                        editing = name
                        editingLeft = false
                        focusRequester.requestFocus()
                        focusRequester.captureFocus()
                    },
                    textAlign = TextAlign.Center,
                    text = rightKeys[name]?.keyCode?.let { KEYMAP.getOrNull(it.toInt()) } ?: "Select key",
                    color = color,
                    fontSize = 48.sp,
                    fontFamily = poetsenOneFont,
                    fontStyle = if (editing == name && !editingLeft) FontStyle.Italic else FontStyle.Normal,
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(modifier = Modifier.fillMaxWidth(0.6f), horizontalArrangement = Arrangement.SpaceAround) {
            Text(
                modifier = Modifier.weight(1f)
                    .clickable(enabled = players.size < MAX_PLAYERS, onClick = addPlayer, role = Role.Button),
                textAlign = TextAlign.Center,
                text = "Add Player",
                color = if (players.size < MAX_PLAYERS) Color.White else Color.Gray,
                fontSize = 48.sp,
                fontFamily = poetsenOneFont,
            )
            Text(
                modifier = Modifier.weight(1f).clickable(onClick = startGame, role = Role.Button),
                textAlign = TextAlign.Center,
                text = "Start Game",
                color = Color.White,
                fontSize = 48.sp,
                fontFamily = poetsenOneFont,
            )
        }
    }
}


//fun calculateRectangle(startLocation: Pair<Double, Double>, endLocation: Pair<Double, Double>, radians: Double): Rect {
//    val (x1, y1) = startLocation
//    val (x2, y2) = endLocation
//
//    val h = sqrt((x2 - x1).pow(2) + (y2 - y1).pow(2))
//
//    val r = (h / 2) / sin(radians / 2)
//
//    val xA = (x2 - x1) / 2
//    val yA = (y2 - y1) / 2
//
//    val x0 = (x1 + xA)
//    val y0 = (y1 + yA)
//
//    val o = if (radians > 0) {
//        Offset((x0 + (1 / tan(radians / 2)) * yA).toFloat(), (y0 - (1 / tan(radians / 2)) * xA).toFloat())
//    } else {
//        Offset((x0 - (1 / tan(radians / 2)) * yA).toFloat(), (y0 + (1 / tan(radians / 2)) * xA).toFloat())
//    }
//
//    return Rect(center = o, radius = r.toFloat())
//}


const val MOVE_PER_TICK = 1.25
const val TURN_PER_TICK = 0.04 // radians

@Composable
fun Game(players: List<String>, keys: Map<Key, Pair<String, Boolean>>, exitGame: () -> Unit) {
    val poetsenOneFont = FontFamily(
        Font(Res.font.PoetsenOne_Regular)
    )

    val scores = remember { mutableStateMapOf<String, Int>() }
    val speeds = remember { mutableStateMapOf<String, Double>() }
    val headings = remember { mutableStateMapOf<String, Double>() }
    val locations = remember { mutableStateMapOf<String, Pair<Double, Double>>() }

    val manoeuvreStart = remember { mutableStateMapOf<String, Pair<Double, Double>>() }
    val headingStart = remember { mutableStateMapOf<String, Double>() }
    val turning = remember { mutableStateMapOf<String, Int>() }
    val fullCircle = remember { mutableStateMapOf<String, Boolean>() }
//    val distanceTicks = remember { mutableStateMapOf<String, Double>() }
//    val turnTicks = remember { mutableStateMapOf<String, Double>() }
    val manoeuvreTicks = remember { mutableStateMapOf<String, Double>() }

    val pathStack = remember { mutableStateListOf<Pair<Color, Path>>() }
//    val manoeuvreStack = remember { mutableStateMapOf<String, Path>() }
//    val lastManoeuvre = remember { mutableStateMapOf<String, Path>() }
    val manoeuvre = remember { mutableMapOf<String, SnapshotStateList<PathNode>>() }

    var paused by remember { mutableStateOf(true) }
    var lastTime: ULong by remember { mutableStateOf(0U) }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(true) {
        withContext(Dispatchers.Default) {
            // Initialize state
            if (scores.keys.size != players.size) {
                players.forEach { scores[it] = 0 }
            }

            if (speeds.keys.size != players.size) {
                players.forEach { speeds[it] = 1.0 }
            }

            if (headings.keys.size != players.size) {
                players.forEach { headings[it] = Random.nextDouble(0.0, 2 * PI) }
            }

            if (locations.keys.size != players.size) {
                players.forEach { locations[it] = Random.nextDouble(50.0, 450.0) to Random.nextDouble(50.0, 450.0) }
            }

            if (manoeuvreStart.keys.size != players.size) {
                players.forEach { manoeuvreStart[it] = locations[it]!! }
            }

            if (headingStart.keys.size != players.size) {
                players.forEach { headingStart[it] = headings[it]!! }
            }

            if (turning.keys.size != players.size) {
                players.forEach { turning[it] = 0 }
            }

            if (fullCircle.keys.size != players.size) {
                players.forEach { fullCircle[it] = false }
            }

//            if (distanceTicks.keys.size != players.size) {
//                players.forEach { distanceTicks[it] = 0.0 }
//            }
//
//            if (turnTicks.keys.size != players.size) {
//                players.forEach { turnTicks[it] = 0.0 }
//            }

            if (manoeuvreTicks.keys.size != players.size) {
                players.forEach { manoeuvreTicks[it] = 0.0 }
            }

//            players.forEach { player ->
//                manoeuvreStack[player] = Path().apply {
//                    moveTo(locations[player]!!.first.toFloat(), locations[player]!!.second.toFloat())
//                }
//                lastManoeuvre[player] = manoeuvreStack[player]!!
//            }

            players.forEach { player ->
                manoeuvre[player] = mutableStateListOf(
                    PathNode.MoveTo(locations[player]!!.first.toFloat(), locations[player]!!.second.toFloat()),
                    PathNode.LineTo(locations[player]!!.first.toFloat(), locations[player]!!.second.toFloat()),
                )
            }

            focusRequester.requestFocus()
            focusRequester.captureFocus()


            // Game loop
            while (true) {
                val time = window.performance.now().toULong()
                val dticks = (time / 20U - lastTime / 20U)
                lastTime = time

                if (paused) {
                    delay(100)
                    continue
                }


                players.forEach { player ->
//                    distanceTicks[player] = distanceTicks[player]!! + dticks.toDouble() * speeds[player]!!
//                    turnTicks[player] = turnTicks[player]!! + dticks.toDouble()
                    manoeuvreTicks[player] = manoeuvreTicks[player]!! + dticks.toDouble() * speeds[player]!!

                    val turn = turning[player]!!

                    if (turn == 0) {
                        val x =
                            manoeuvreStart[player]!!.first + cos(headings[player]!!) * MOVE_PER_TICK * manoeuvreTicks[player]!!
                        val y =
                            manoeuvreStart[player]!!.second + sin(headings[player]!!) * MOVE_PER_TICK * manoeuvreTicks[player]!!

                        locations[player] = x to y

                        manoeuvre[player]!!.removeLast()
                        manoeuvre[player]!!.add(PathNode.LineTo(x.toFloat(), y.toFloat()))
                    } else {
//                        val angle = TURN_PER_TICK * turnTicks[player]!! * turn
//
//                        headings[player] = headingStart[player]!! + angle
//
//                        val radius = (MOVE_PER_TICK * distanceTicks[player]!!) / abs(angle)
//
//                        val angleTowardsCenter = headingStart[player]!! + turn * PI / 2
//
//                        val cX = manoeuvreStart[player]!!.first + cos(angleTowardsCenter) * radius
//                        val cY = manoeuvreStart[player]!!.second + sin(angleTowardsCenter) * radius
//
//                        val x = cX + cos(headings[player]!!) * radius
//                        val y = cY + sin(headings[player]!!) * radius
//
//                        locations[player] = x to y
//
//                        lastManoeuvre[player] = Path().apply {
//                            addPath(manoeuvreStack[player]!!)
//                            arcToRad(
//                                rect = Rect(center = Offset(cX.toFloat(), cY.toFloat()), radius = radius.toFloat()),
//                                startAngleRadians = -headingStart[player]!!.toFloat(),
//                                sweepAngleRadians = -angle.toFloat(),
//                                forceMoveTo = false
//                            )
//                        }

                        val radius =
                            (MOVE_PER_TICK / TURN_PER_TICK) * speeds[player]!! // If speed changes, start a new manoeuvre!!!

                        val angle = manoeuvreTicks[player]!! * TURN_PER_TICK / (speeds[player]!!) * turn

                        val aOld = headingStart[player]!! + turn * PI / 2
                        val aNew = aOld + angle

                        val (x, y) = manoeuvreStart[player]!!
                        val cX = x + cos(aOld) * radius
                        val cY = y + sin(aOld) * radius
                        val x1 = cX - cos(aNew) * radius
                        val y1 = cY - sin(aNew) * radius

                        locations[player] = x1 to y1

                        headings[player] = headingStart[player]!! + angle

                        manoeuvre[player]!!.removeLast()

                        if (abs(angle) > PI * 2 && !fullCircle[player]!!) {
                            manoeuvre[player]!!.addAll(
                                listOf(
                                    PathNode.ArcTo(
                                        horizontalEllipseRadius = radius.toFloat(),
                                        verticalEllipseRadius = radius.toFloat(),
                                        theta = 0f,
                                        isPositiveArc = turn > 0,
                                        isMoreThanHalf = false,
                                        arcStartX = (x + 2 * cos(aOld) * radius).toFloat(),
                                        arcStartY = (y + 2 * sin(aOld) * radius).toFloat()
                                    ),
                                    PathNode.ArcTo(
                                        horizontalEllipseRadius = radius.toFloat(),
                                        verticalEllipseRadius = radius.toFloat(),
                                        theta = 0f,
                                        isPositiveArc = turn > 0,
                                        isMoreThanHalf = false,
                                        arcStartX = x.toFloat(),
                                        arcStartY = y.toFloat()
                                    ),
                                )
                            )
                            fullCircle[player] = true
                        }

                        manoeuvre[player]!!.add(
                            PathNode.ArcTo(
                                horizontalEllipseRadius = radius.toFloat(),
                                verticalEllipseRadius = radius.toFloat(),
                                theta = 0f,
                                isPositiveArc = turn > 0,
                                isMoreThanHalf = abs(angle % (2 * PI)) > PI,
                                arcStartX = x1.toFloat(),
                                arcStartY = y1.toFloat()
                            )
                        )

//                        println("player: $player, turn: $turn, angle: $angle, radius: $radius, center: ($cX, $cY), loc: ($x, $y)")
//                        println(
//                            """
//                            player: $player,
//                            turn: $turn,
//                            turnTicks: ${turnTicks[player]},
//                            moveTicks: ${distanceTicks[player]},
//                            angle: $angle,
//                            startHeading: ${headingStart[player]},
//                            newHeading: ${headings[player]},
//                            radius: $radius,
//                            angleTowardsCenter: $angleTowardsCenter,
//                            center: ($cX, $cY),
//                            start: ${manoeuvreStart[player]},
//                            newLoc: ($x, $y)
//
//                            Path: ${lastManoeuvre[player]?.asSkiaPath()?.dump()}
//                        """.trimIndent()
//                        )

//                        println("""
//                            player: $player,
//
//                            turn: $turn,
//                            angle: $angle,
//                            radius: $radius,
//
//                            startHeading: ${headingStart[player]},
//                            newHeading: ${headings[player]},
//
//                            start: ($x, $y),
//                            center: ($cX, $cY),
//                            newLoc: ($x1, $y1)
//                        """.trimIndent())
                    }

                    // TODO: collision detection
                }

                delay(20)
            }
        }
    }

    Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.weight(2f).fillMaxHeight(), contentAlignment = Alignment.Center) {
            Canvas(
                modifier = Modifier.focusRequester(focusRequester).focusable().onPreviewKeyEvent {
                    when {
                        it.key in keys.keys -> {
                            val (player, left) = keys.getValue(it.key)

//                            println("Key event: $player, type: $it.type, left: $left, location: ${locations[player]}")
                            if (it.type == KeyEventType.KeyDown && !paused && turning[player] != if (left) 1 else -1) {
                                turning[player] = if (left) 1 else -1
                            } else if (it.type == KeyEventType.KeyUp) {
                                turning[player] = when {
                                    turning[player] == 1 && left -> 0
                                    turning[player] == -1 && !left -> 0
                                    else -> return@onPreviewKeyEvent true
                                }
                            } else {
                                return@onPreviewKeyEvent false
                            }

//                            manoeuvreStack[player] = lastManoeuvre[player]!!
                            manoeuvre[player]?.add(
                                PathNode.LineTo(
                                    locations[player]!!.first.toFloat(),
                                    locations[player]!!.second.toFloat()
                                )
                            )
                            fullCircle[player] = false

                            headingStart[player] = headings[player]!!
                            manoeuvreStart[player] = locations[player]!!
//                            distanceTicks[player] = 0.0
//                            turnTicks[player] = 0.0
                            manoeuvreTicks[player] = 0.0

                            true
                        }

                        it.type != KeyEventType.KeyDown -> false
                        it.key == Key.Spacebar -> {
                            paused = !paused
                            true
                        }

                        it.key == Key.Escape && paused -> {
                            exitGame()
                            true
                        }

                        else -> false
                    }
                }.fillMaxSize(0.75f).aspectRatio(1f).border(5.dp, Color.White)
            ) {
//                println("Drawing")
                withTransform({
                    scale(scaleX = 1f, scaleY = -1f)
                    scale(
                        scaleX = size.width / 500,
                        scaleY = size.height / 500,
                        pivot = Offset(0f, 0f)
                    )
                }) {
//                      println("${lines.size} lines")
                    pathStack.forEach { (color, path) ->
                        drawPath(path, color, style = Stroke(width = 8f))
                    }

                    players.forEach { player ->
                        drawPath(
                            manoeuvre[player]?.toPath() ?: return@forEach,
                            PLAYERS.getValue(player),
                            style = Stroke(width = 8f)
                        )

                        val (x, y) = locations[player] ?: return@forEach
                        val color = PLAYERS.getValue(player)
                        drawCircle(color, center = Offset(x.toFloat(), y.toFloat()), radius = 5f)
                    }
                }
            }
        }
        Column(modifier = Modifier.weight(1f).fillMaxHeight()) { }
    }
}
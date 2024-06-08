import Constants.BOARD_SIZE
import Constants.HEAD_RADIUS
import Constants.KEYMAP
import Constants.MAX_PLAYERS
import Constants.MILLIS_PER_TICK
import Constants.SLEEP_MILLIS
import Constants.TRAIL_WIDTH
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
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

@Composable
fun App() {
    Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
        var inGame by remember { mutableStateOf(false) }
        val players = remember { mutableStateListOf(*Player.list.copyOfRange(0, 2)) }
        val keys = remember { mutableStateMapOf<Key, Pair<Player, Boolean>>() }

        if (!inGame) {
            ChoosePlayers(players = players, keys = keys, addPlayer = {
                val free = Player.list.firstOrNull { it !in players }
                if (free != null) {
                    players.add(free)
                }
            }, removePlayer = { players.remove(it) }, bindKey = { key, left, player ->
                keys.filterValues { it.first == player && it.second == left }.keys.forEach { keys.remove(it) }
                keys[key] = player to left
            }, startGame = { inGame = true })
        } else {
            Game(players = players, keys = keys, exitGame = { inGame = false })
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChoosePlayers(
    players: List<Player>,
    keys: Map<Key, Pair<Player, Boolean>>,
    addPlayer: () -> Unit,
    removePlayer: (Player) -> Unit,
    bindKey: (Key, Boolean, Player) -> Unit,
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

    var editing: Player? by remember { mutableStateOf(null) }
    var editingLeft by remember { mutableStateOf(true) }

    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = Modifier.focusRequester(focusRequester).focusable().onPreviewKeyEvent { event ->
            if (editing == null) return@onPreviewKeyEvent false
            if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
            if (event.key == Key.Spacebar) return@onPreviewKeyEvent false
            if (event.key == Key.Escape) {
                editing = null
                focusRequester.freeFocus()
                return@onPreviewKeyEvent true
            }

            bindKey(event.key, editingLeft, editing!!)

            if (!editingLeft) {
                editing = null
                focusRequester.freeFocus()
            } else {
                editingLeft = false
            }

            return@onPreviewKeyEvent true
        }.padding(horizontal = 60.dp, vertical = 160.dp).fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "Markchtung", fontSize = 96.sp, color = Color.White, fontFamily = poetsenOneFont)
        Spacer(modifier = Modifier.weight(1f))

        players.forEach { p ->
            Row(
                modifier = Modifier.fillMaxWidth(0.6f), horizontalArrangement = Arrangement.SpaceAround
            ) {
                var strikethrough by remember { mutableStateOf(false) }
                Text(
                    modifier = Modifier.weight(1f).onPointerEvent(eventType = PointerEventType.Enter,
                        onEvent = { strikethrough = players.size > 2 })
                        .onPointerEvent(eventType = PointerEventType.Exit, onEvent = { strikethrough = false })
                        .clickable(enabled = players.size > 2, onClickLabel = "Remove player", role = Role.Button) {
                            if (editing == p) editing = null
                            removePlayer(p)
                        },
                    textAlign = TextAlign.Center,
                    text = p.name,
                    color = p.color,
                    fontSize = 48.sp,
                    fontFamily = poetsenOneFont,
                    textDecoration = if (strikethrough) TextDecoration.LineThrough else null,
                )
                Spacer(modifier = Modifier.weight(0.8f))
                Text(
                    modifier = Modifier.weight(1f).clickable(role = Role.Button) {
                        editing = p
                        editingLeft = true
                        focusRequester.requestFocus()
                        focusRequester.captureFocus()
                    },
                    textAlign = TextAlign.Center,
                    text = leftKeys[p]?.keyCode?.let { KEYMAP.getOrNull(it.toInt()) } ?: "Select key",
                    color = p.color,
                    fontSize = 48.sp,
                    fontFamily = poetsenOneFont,
                    fontStyle = if (editing == p && editingLeft) FontStyle.Italic else FontStyle.Normal,
                )
                Text(
                    modifier = Modifier.weight(1f).clickable(role = Role.Button) {
                        editing = p
                        editingLeft = false
                        focusRequester.requestFocus()
                        focusRequester.captureFocus()
                    },
                    textAlign = TextAlign.Center,
                    text = rightKeys[p]?.keyCode?.let { KEYMAP.getOrNull(it.toInt()) } ?: "Select key",
                    color = p.color,
                    fontSize = 48.sp,
                    fontFamily = poetsenOneFont,
                    fontStyle = if (editing == p && !editingLeft) FontStyle.Italic else FontStyle.Normal,
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
                modifier = Modifier.weight(1f).clickable(onClick = {
                    focusRequester.freeFocus()
                    startGame()
                }, role = Role.Button),
                textAlign = TextAlign.Center,
                text = "Start Game",
                color = Color.White,
                fontSize = 48.sp,
                fontFamily = poetsenOneFont,
            )
        }
    }
}

@Composable
fun Game(players: List<Player>, keys: Map<Key, Pair<Player, Boolean>>, exitGame: () -> Unit) {
    println("Compose")
    val poetsenOneFont = FontFamily(
        Font(Res.font.PoetsenOne_Regular)
    )

    val playerStates = remember { mutableMapOf(*players.map { it to Player.State() }.toTypedArray()) }

    val pathStack = remember { mutableStateListOf<Pair<Color, Path>>() }

    var paused by remember { mutableStateOf(true) }
    var lastTime: ULong by remember { mutableStateOf(0U) }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(true) {
        withContext(Dispatchers.Default) {
            // Initialize state
            focusRequester.requestFocus()
            focusRequester.captureFocus()


            // Game loop
            while (true) {
                val time = window.performance.now().toULong()
                val dticks = (time / MILLIS_PER_TICK - lastTime / MILLIS_PER_TICK).toInt()
                lastTime = time

                if (paused) {
                    delay(SLEEP_MILLIS.toLong())
                    continue
                }


                players.forEach {
                    playerStates.getValue(it).update(dticks)
                }

                delay(MILLIS_PER_TICK.toLong())
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

                            if (it.type == KeyEventType.KeyDown && !paused) {
                                playerStates.getValue(player).startTurn(left)
                            } else if (it.type == KeyEventType.KeyUp) {
                                playerStates.getValue(player).endTurn(left)
                            } else {
                                return@onPreviewKeyEvent false
                            }

                            true
                        }

                        it.type != KeyEventType.KeyDown -> false
                        it.key == Key.Spacebar -> {
                            println("Spacebar pressed")
                            paused = !paused
                            true
                        }

                        it.key == Key.Escape && paused -> {
                            focusRequester.freeFocus()
                            exitGame()
                            true
                        }

                        else -> false
                    }
                }.fillMaxSize(0.75f).aspectRatio(1f).border(5.dp, Color.White)
            ) {
                withTransform({
                    scale(scaleX = 1f, scaleY = -1f)
                    scale(
                        scaleX = size.width / BOARD_SIZE, scaleY = size.height / BOARD_SIZE, pivot = Offset(0f, 0f)
                    )
                }) {
                    pathStack.forEach { (color, path) ->
                        drawPath(path, color, style = Stroke(width = TRAIL_WIDTH))
                    }

                    players.forEach { player ->
                        drawPath(
                            playerStates.getValue(player).manoeuvrePath,
                            player.color,
                            style = Stroke(width = TRAIL_WIDTH)
                        )

                        val (x, y) = playerStates.getValue(player).location
                        drawCircle(player.color, center = Offset(x, y), radius = HEAD_RADIUS)
                    }
                }
            }
        }
        Column(modifier = Modifier.weight(1f).fillMaxHeight()) { }
    }
}
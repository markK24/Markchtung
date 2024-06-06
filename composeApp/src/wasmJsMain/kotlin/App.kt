
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import markchtung.composeapp.generated.resources.PoetsenOne_Regular
import markchtung.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.Font

val MAX_PLAYERS = 6

@Suppress("SpellCheckingInspection")
val PLAYERS = mapOf(
    "Goldy" to Color(0xFFDAA520), // Gold
    "Indiego" to Color(0xFF4B0082), // Indigo
    "Malachite" to Color(0xFF0BDA51), // Malachite
    "Minthryl" to Color(0xFF3EB489), // Mint
    "Bloody" to Color(0xFF660000), // Blood-red
    "Manghoost" to Color(0xFFF8F8FF), // Ghost white
)

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
                bindKey = { _, _, _ -> TODO() },
                startGame = { inGame = true }
            )
        } else {
//            MainMenu(onStartGame = { inGame = true })
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

    val leftKeys = remember(keys) {
        buildMap {
            keys.forEach { (key, p) -> if (p.second) set(p.first, key) }
        }
    }
    val rightKeys = remember(keys) {
        buildMap {
            keys.forEach { (key, p) -> if (!p.second) set(p.first, key) }
        }
    }

    Column(
        modifier = Modifier.padding(horizontal = 60.dp, vertical = 160.dp).fillMaxSize(),
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
                    ).clickable(enabled = players.size > 2, onClickLabel = "Remove player", role = Role.Button) { removePlayer(name) },
                    textAlign = TextAlign.Center,
                    text = name,
                    color = color,
                    fontSize = 48.sp,
                    fontFamily = poetsenOneFont,
                    textDecoration = if (strikethrough) TextDecoration.LineThrough else null,
                )
                Spacer(modifier = Modifier.weight(0.8f))
                Text(
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    text = leftKeys[name]?.toString() ?: "<-",
                    color = color,
                    fontSize = 48.sp,
                    fontFamily = poetsenOneFont,
                )
                Text(
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    text = rightKeys[name]?.toString() ?: "->",
                    color = color,
                    fontSize = 48.sp,
                    fontFamily = poetsenOneFont,
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
package studio.astroturf.quizzi.ui.screen.rooms

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import studio.astroturf.quizzi.domain.model.GameRoom
import studio.astroturf.quizzi.domain.model.RoomState
import studio.astroturf.quizzi.ui.R
import studio.astroturf.quizzi.ui.theme.Accent1
import studio.astroturf.quizzi.ui.theme.Black
import studio.astroturf.quizzi.ui.theme.BodySmallMedium
import studio.astroturf.quizzi.ui.theme.BodyXLarge
import studio.astroturf.quizzi.ui.theme.BodyXSmallMedium
import studio.astroturf.quizzi.ui.theme.Grey2
import studio.astroturf.quizzi.ui.theme.Grey5
import studio.astroturf.quizzi.ui.theme.Heading3
import studio.astroturf.quizzi.ui.theme.Primary
import studio.astroturf.quizzi.ui.theme.Secondary
import studio.astroturf.quizzi.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomsScreen(
    onNavigateToRoom: (RoomIntent) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RoomsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    RoomsScreenContent(
        modifier = modifier,
        currentUsername = uiState.currentUsername,
        rooms = uiState.rooms,
        isConnected = uiState.isConnected,
        error = uiState.error,
        onCreateRoom = {
            onNavigateToRoom(RoomIntent.CreateRoom)
        },
        onJoinRoom = { roomId ->
            onNavigateToRoom(RoomIntent.JoinRoom(roomId))
        },
    )
}

@Composable
private fun RoomsScreenContent(
    currentUsername: String,
    rooms: List<GameRoom>,
    isConnected: Boolean,
    error: String?,
    onCreateRoom: () -> Unit,
    onJoinRoom: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Primary),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (!isConnected) {
            Text(
                text = "Connecting...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
            )
        }

        Row(
            modifier =
                Modifier
                    .height(60.dp)
                    .padding(top = 16.dp, start = 24.dp, end = 24.dp)
                    .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier =
                    Modifier
                        .wrapContentWidth()
                        .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    modifier = Modifier.wrapContentHeight(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_sun),
                        contentDescription = "Quizzi Logo",
                        modifier = Modifier.height(20.dp),
                    )

                    Text(
                        modifier = Modifier.wrapContentHeight().align(Alignment.CenterVertically),
                        text = "GOOD MORNING",
                        textAlign = TextAlign.Center,
                        style = BodyXSmallMedium.copy(color = Accent1, textAlign = TextAlign.Center),
                    )
                }

                Text(
                    text = currentUsername,
                    style = Heading3.copy(color = White),
                )
            }

            Image(
                painter = painterResource(id = R.drawable.avatar),
                contentDescription = "User Avatar",
                modifier = Modifier.size(56.dp),
            )

            error?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }

        Box(
            modifier =
                Modifier
                    .padding(top = 40.dp)
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
                    .height(242.dp)
                    .background(Secondary, RoundedCornerShape(20.dp)),
        )

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 20.dp,
                    ).weight(1f)
                    .background(White, RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .padding(horizontal = 24.dp)
                    .padding(top = 32.dp),
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier =
                        Modifier
                            .height(28.dp)
                            .wrapContentWidth(),
                    text = "Live Rooms",
                    style = BodyXLarge.copy(color = Black),
                    textAlign = TextAlign.Center,
                )

                Text(
                    modifier =
                        Modifier
                            .height(20.dp)
                            .wrapContentWidth(),
                    text = "See all",
                    style = BodySmallMedium.copy(color = Primary),
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            RoomsList(
                rooms = rooms,
                onJoinRoom = onJoinRoom,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
fun RoomsList(
    rooms: List<GameRoom>,
    onJoinRoom: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        rooms.forEach { room ->
            RoomItem(
                room = room,
                onJoinRoom = { onJoinRoom(room.id) },
            )
        }
    }
}

@Composable
private fun RoomItem(
    room: GameRoom,
    onJoinRoom: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .border(2.dp, Grey5, RoundedCornerShape(20.dp))
                .fillMaxWidth()
                .height(80.dp)
                .background(White)
                .padding(vertical = 8.dp)
                .padding(start = 8.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier.padding(1.dp).size(64.dp),
            painter = painterResource(id = R.drawable.game_mode_resistence),
            contentDescription = "image description",
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.fillMaxHeight().weight(1f),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                modifier = Modifier.height(24.dp).wrapContentWidth(),
                text = room.players.firstOrNull() + "'s Room",
                style = BodySmallMedium.copy(color = Black),
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                modifier = Modifier.height(18.dp).wrapContentWidth(),
                text = room.roomState.name, // TODO: Change to game mode
                style = BodySmallMedium.copy(color = Grey2),
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Image(
            modifier =
                Modifier
                    .padding(1.dp)
                    .size(24.dp),
            painter = painterResource(id = R.drawable.ic_chevron_right),
            contentDescription = "image description",
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RoomsScreenContentPreview() {
    MaterialTheme {
        RoomsScreenContent(
            currentUsername = "Player1",
            rooms =
                listOf(
                    GameRoom(
                        id = "Room1",
                        roomState = RoomState.Waiting,
                        players = listOf("Player1", "Player2"),
                    ),
                    GameRoom(
                        id = "Room2",
                        roomState = RoomState.Playing,
                        players = listOf("Player3", "Player4", "Player5"),
                    ),
                ),
            isConnected = true,
            error = null,
            onCreateRoom = {},
            onJoinRoom = {},
        )
    }
}

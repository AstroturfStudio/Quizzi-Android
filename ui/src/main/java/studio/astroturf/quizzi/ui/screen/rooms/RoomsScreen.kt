package studio.astroturf.quizzi.ui.screen.rooms

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import studio.astroturf.quizzi.domain.model.Category
import studio.astroturf.quizzi.domain.model.CategoryId
import studio.astroturf.quizzi.domain.model.GameRoom
import studio.astroturf.quizzi.domain.model.RoomState
import studio.astroturf.quizzi.ui.R
import studio.astroturf.quizzi.ui.theme.Accent1
import studio.astroturf.quizzi.ui.theme.Black
import studio.astroturf.quizzi.ui.theme.BodyNormalRegular
import studio.astroturf.quizzi.ui.theme.BodySmallMedium
import studio.astroturf.quizzi.ui.theme.BodyXLarge
import studio.astroturf.quizzi.ui.theme.BodyXSmallMedium
import studio.astroturf.quizzi.ui.theme.Grey2
import studio.astroturf.quizzi.ui.theme.Grey3
import studio.astroturf.quizzi.ui.theme.Grey5
import studio.astroturf.quizzi.ui.theme.Heading3
import studio.astroturf.quizzi.ui.theme.Primary
import studio.astroturf.quizzi.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomsScreen(
    onNavigateToRoom: (GameRoom) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RoomsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    DisposableEffect(Unit) {
        viewModel.startPeriodicRoomUpdates()
        onDispose {
            viewModel.stopPeriodicRequests()
        }
    }

    RoomsScreenContent(
        currentUsername = uiState.currentUsername,
        rooms = uiState.filteredRooms,
        isConnected = uiState.isConnected,
        searchText = uiState.searchText,
        error = uiState.error,
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.refresh() },
        onSearch = { viewModel.onSearch(it) },
        onJoinRoom = { room ->
            viewModel.stopPeriodicRequests()
            onNavigateToRoom(room)
        },
        modifier = modifier,
    )
}

@Composable
private fun RoomsScreenContent(
    currentUsername: String,
    rooms: List<GameRoom>,
    isConnected: Boolean,
    searchText: String,
    error: String?,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onSearch: (String) -> Unit,
    onJoinRoom: (GameRoom) -> Unit,
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
                text = stringResource(R.string.connecting),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
            )
        }

        Row(
            modifier =
                Modifier
                    .height(64.dp)
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
                        modifier =
                            Modifier
                                .wrapContentHeight()
                                .align(Alignment.CenterVertically),
                        text = stringResource(R.string.good_morning),
                        textAlign = TextAlign.Center,
                        style =
                            BodyXSmallMedium.copy(
                                color = Accent1,
                                textAlign = TextAlign.Center,
                            ),
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

        SearchField(
            modifier =
                Modifier
                    .padding(horizontal = 24.dp)
                    .padding(top = 16.dp)
                    .fillMaxWidth()
                    .height(56.dp),
            searchText = searchText,
            onSearch = { onSearch(it) },
        )

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 24.dp,
                        start = 8.dp,
                        end = 8.dp,
                    ).weight(1f)
                    .background(
                        White,
                        NotchedShape(
                            notchWidth = 128.dp,
                            notchHeight = 12.dp,
                            cornerRadius = 32.dp,
                        ),
                    ).padding(top = 32.dp)
                    .padding(horizontal = 16.dp),
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
                    text = stringResource(R.string.rooms),
                    style = BodyXLarge.copy(color = Black),
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            RoomsList(
                rooms = rooms,
                onJoinRoom = onJoinRoom,
                isRefreshing = isRefreshing,
                onRefresh = onRefresh,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
            )
        }
    }
}

private class NotchedShape(
    private val notchWidth: Dp,
    private val notchHeight: Dp,
    private val cornerRadius: Dp,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline =
        Outline.Generic(
            path =
                Path().apply {
                    // Convert Dp to pixels
                    val cornerRadiusPx = with(density) { cornerRadius.toPx() }
                    val notchWidthPx = with(density) { notchWidth.toPx() }
                    val notchHeightPx = with(density) { notchHeight.toPx() }

                    // Start from top-left with rounded corner
                    moveTo(0f, cornerRadiusPx)
                    quadraticTo(0f, 0f, cornerRadiusPx, 0f)

                    // Line to start of notch
                    lineTo(size.width / 2 - notchWidthPx / 2, 0f)

                    // Create outward notch curve
                    cubicTo(
                        size.width / 2 - notchWidthPx / 4,
                        0f, // First control point
                        size.width / 2 - notchWidthPx / 6,
                        -notchHeightPx, // Second control point (negative height for outward)
                        size.width / 2,
                        -notchHeightPx, // End point (negative height for outward)
                    )
                    cubicTo(
                        size.width / 2 + notchWidthPx / 6,
                        -notchHeightPx, // First control point (negative height for outward)
                        size.width / 2 + notchWidthPx / 4,
                        0f, // Second control point
                        size.width / 2 + notchWidthPx / 2,
                        0f, // End point
                    )

                    // Line to top-right corner
                    lineTo(size.width - cornerRadiusPx, 0f)

                    // Top-right rounded corner
                    quadraticTo(size.width, 0f, size.width, cornerRadiusPx)

                    // Complete the rectangle
                    lineTo(size.width, size.height)
                    lineTo(0f, size.height)
                    close()
                },
        )
}

@Composable
fun SearchField(
    searchText: String,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .background(
                    color = Black.copy(alpha = 0.16f),
                    shape = RoundedCornerShape(size = 20.dp),
                ).padding(16.dp)
                .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(R.drawable.ic_search),
            tint = White,
            contentDescription = null,
        )

        BasicTextField(
            value = searchText,
            onValueChange = { onSearch(it) },
            textStyle = BodyNormalRegular.copy(color = White),
            decorationBox = { innerTextField ->
                Box {
                    if (searchText.isEmpty()) {
                        Text(
                            text = stringResource(R.string.search_rooms),
                            style = BodyNormalRegular.copy(color = Grey3),
                        )
                    }
                    innerTextField()
                }
            },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun RoomsList(
    rooms: List<GameRoom>,
    onJoinRoom: (GameRoom) -> Unit,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)

        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = onRefresh,
        ) {
            if (!isRefreshing) {
                if (rooms.isEmpty()) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(R.string.no_rooms_available),
                        style = BodySmallMedium.copy(color = Grey2),
                        textAlign = TextAlign.Center,
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        items(rooms) { room ->
                            RoomItem(
                                room = room,
                                onJoinRoom = { onJoinRoom(room) },
                            )
                        }
                    }
                }
            } else {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Primary,
                )
            }
        }
    }
}

@Composable
private fun RoomItem(
    room: GameRoom,
    onJoinRoom: (GameRoom) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .border(2.dp, Grey5, RoundedCornerShape(20.dp))
                .fillMaxWidth()
                .height(80.dp)
                .background(White)
                .padding(vertical = 8.dp)
                .padding(start = 8.dp, end = 16.dp)
                .clickable { onJoinRoom(room) },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier =
                Modifier
                    .padding(1.dp)
                    .size(64.dp),
            painter = painterResource(id = R.drawable.game_mode_resistence),
            contentDescription = "image description",
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .weight(1f),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                modifier =
                    Modifier
                        .height(24.dp)
                        .wrapContentWidth(),
                text = room.name,
                style = BodySmallMedium.copy(color = Black),
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                modifier =
                    Modifier
                        .height(18.dp)
                        .wrapContentWidth(),
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
                        name = "Room1",
                        roomState = RoomState.Waiting,
                        players = listOf("Player1", "Player2"),
                        gameType = "ResistanceGame",
                        category =
                            Category(
                                id = CategoryId.COUNTRY_FLAGS,
                                name = "Flag Quiz",
                            ),
                    ),
                    GameRoom(
                        id = "Room2",
                        name = "Room2",
                        roomState = RoomState.Playing,
                        players = listOf("Player3", "Player4", "Player5"),
                        gameType = "ResistanceGame",
                        category =
                            Category(
                                id = CategoryId.COUNTRY_FLAGS,
                                name = "Flag Quiz",
                            ),
                    ),
                    GameRoom(
                        id = "Room2",
                        name = "Room2",
                        roomState = RoomState.Playing,
                        players = listOf("Player3", "Player4", "Player5"),
                        gameType = "ResistanceGame",
                        category =
                            Category(
                                id = CategoryId.COUNTRY_FLAGS,
                                name = "Flag Quiz",
                            ),
                    ),
                    GameRoom(
                        id = "Room2",
                        name = "Room2",
                        roomState = RoomState.Playing,
                        players = listOf("Player3", "Player4", "Player5"),
                        gameType = "ResistanceGame",
                        category =
                            Category(
                                id = CategoryId.COUNTRY_FLAGS,
                                name = "Flag Quiz",
                            ),
                    ),
                    GameRoom(
                        id = "Room2",
                        name = "Room2",
                        roomState = RoomState.Playing,
                        players = listOf("Player3", "Player4", "Player5"),
                        gameType = "ResistanceGame",
                        category =
                            Category(
                                id = CategoryId.COUNTRY_FLAGS,
                                name = "Flag Quiz",
                            ),
                    ),
                ),
            isConnected = true,
            searchText = "Alican",
            error = null,
            isRefreshing = false,
            onRefresh = {},
            onSearch = {},
            onJoinRoom = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RoomsScreenContentPreview_NoRooms() {
    MaterialTheme {
        RoomsScreenContent(
            currentUsername = "Player1",
            rooms = emptyList(),
            isConnected = true,
            searchText = "Alican",
            error = null,
            isRefreshing = false,
            onRefresh = {},
            onSearch = {},
            onJoinRoom = {},
        )
    }
}

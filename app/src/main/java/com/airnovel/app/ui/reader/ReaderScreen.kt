package com.airnovel.app.ui.reader

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airnovel.app.ui.theme.ReaderBgDay
import com.airnovel.app.ui.theme.ReaderBgNight
import com.airnovel.app.ui.theme.ReaderTextDay
import com.airnovel.app.ui.theme.ReaderTextNight
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    bookId: String,
    chapterId: String,
    bookTitle: String,
    chapterIndex: Int,
    chapterIdList: List<String>,
    onNavigateBack: () -> Unit,
    viewModel: ReaderViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showControls by remember { mutableStateOf(true) }
    var showSettings by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(bookId, chapterId) {
        viewModel.initialize(bookId, chapterId, bookTitle, chapterIndex, chapterIdList)
    }

    LaunchedEffect(scrollState.value) {
        val maxScroll = (scrollState.maxValue).coerceAtLeast(1)
        val progress = if (maxScroll > 0) scrollState.value.toFloat() / maxScroll else 0f
        viewModel.updateScrollProgress(progress)
    }

    val bgColor = if (uiState.isNightMode) ReaderBgNight else ReaderBgDay
    val textColor = if (uiState.isNightMode) ReaderTextNight else ReaderTextDay
    val uiColor = if (uiState.isNightMode) Color.White.copy(alpha = 0.8f) else Color(0xFF5A5A5A)
    val fontFamily = if (uiState.useSerif) FontFamily.Serif else FontFamily.Default

    // Settings Bottom Sheet
    if (showSettings) {
        ModalBottomSheet(
            onDismissRequest = { showSettings = false },
            sheetState = sheetState,
            containerColor = if (uiState.isNightMode) Color(0xFF2A2A2A) else Color(0xFFFFF8EF),
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            ReaderSettingsPanel(
                fontSize = uiState.fontSize,
                lineSpacing = uiState.lineSpacing,
                useSerif = uiState.useSerif,
                onFontSizeChange = { viewModel.updateFontSize(it) },
                onLineSpacingChange = { viewModel.updateLineSpacing(it) },
                onSerifToggle = { viewModel.toggleSerif() },
                onNightModeToggle = { viewModel.toggleNightMode() },
                isNightMode = uiState.isNightMode
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = { },
                        onHorizontalDrag = { _, dragAmount ->
                            if (dragAmount < -60) {
                                scope.launch {
                                    viewModel.goToNextChapter()
                                    scrollState.scrollTo(0)
                                }
                            } else if (dragAmount > 60) {
                                scope.launch {
                                    viewModel.goToPrevChapter()
                                    scrollState.scrollTo(0)
                                }
                            }
                        }
                    )
                }
        ) {
            // Top bar
            AnimatedVisibility(
                visible = showControls,
                enter = slideInVertically { -it } + fadeIn(),
                exit = slideOutVertically { -it } + fadeOut()
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = bgColor.copy(alpha = 0.96f),
                    shadowElevation = if (uiState.isNightMode) 0.dp else 1.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 4.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "返回",
                                tint = uiColor
                            )
                        }

                        Text(
                            text = uiState.chapterTitle.ifEmpty { "加载中..." },
                            style = TextStyle(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Medium,
                                fontSize = 15.sp,
                                color = uiColor
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(onClick = { showSettings = true }) {
                            Icon(
                                Icons.Default.FormatSize,
                                contentDescription = "阅读设置",
                                tint = uiColor
                            )
                        }

                        IconButton(onClick = { viewModel.toggleNightMode() }) {
                            Icon(
                                if (uiState.isNightMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "夜间模式",
                                tint = uiColor
                            )
                        }
                    }
                }
            }

            // Content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            awaitPointerEvent()
                            showControls = !showControls
                        }
                    }
            ) {
                when {
                    uiState.isLoading -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp),
                                strokeWidth = 3.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("加载中...", color = textColor)
                        }
                    }

                    uiState.error != null -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.ErrorOutline,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                uiState.error ?: "加载失败",
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            FilledTonalButton(onClick = {
                                val idx = uiState.currentChapterIndex
                                if (idx < chapterIdList.size) {
                                    viewModel.loadChapter(chapterIdList[idx])
                                }
                            }) {
                                Text("重试")
                            }
                        }
                    }

                    else -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                                .padding(horizontal = 28.dp)
                                .padding(top = 20.dp, bottom = 20.dp)
                        ) {
                            // Chapter title with decorative line
                            Text(
                                text = uiState.chapterTitle,
                                style = TextStyle(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = textColor,
                                    lineHeight = 28.sp
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 24.dp),
                                textAlign = TextAlign.Center
                            )

                            // Decorative divider
                            Box(
                                modifier = Modifier
                                    .width(60.dp)
                                    .height(2.dp)
                                    .background(textColor.copy(alpha = 0.2f))
                                    .align(Alignment.CenterHorizontally)
                            )
                            Spacer(modifier = Modifier.height(24.dp))

                            // Content
                            BasicText(
                                text = uiState.content,
                                style = TextStyle(
                                    fontFamily = fontFamily,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = uiState.fontSize.sp,
                                    lineHeight = (uiState.fontSize * uiState.lineSpacing).sp,
                                    color = textColor
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(40.dp))

                            // Chapter nav
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = textColor.copy(alpha = 0.05f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    TextButton(
                                        onClick = {
                                            viewModel.goToPrevChapter()
                                            scope.launch { scrollState.scrollTo(0) }
                                        },
                                        enabled = uiState.hasPrevChapter
                                    ) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("上一章")
                                    }

                                    TextButton(
                                        onClick = {
                                            viewModel.goToNextChapter()
                                            scope.launch { scrollState.scrollTo(0) }
                                        },
                                        enabled = uiState.hasNextChapter
                                    ) {
                                        Text("下一章")
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }

            // Bottom bar
            AnimatedVisibility(
                visible = showControls,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut()
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = bgColor.copy(alpha = 0.96f),
                    shadowElevation = if (uiState.isNightMode) 0.dp else 1.dp
                ) {
                    Column {
                        LinearProgressIndicator(
                            progress = { uiState.scrollProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.dp),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = textColor.copy(alpha = 0.1f)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .navigationBarsPadding()
                                .padding(horizontal = 20.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "第${uiState.currentChapterIndex + 1}/${uiState.totalChapters}章",
                                style = TextStyle(
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 12.sp,
                                    color = textColor.copy(alpha = 0.6f)
                                )
                            )
                            Text(
                                text = "${(uiState.scrollProgress * 100).toInt()}%",
                                style = TextStyle(
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 12.sp,
                                    color = textColor.copy(alpha = 0.6f)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReaderSettingsPanel(
    fontSize: Float,
    lineSpacing: Float,
    useSerif: Boolean,
    onFontSizeChange: (Float) -> Unit,
    onLineSpacingChange: (Float) -> Unit,
    onSerifToggle: () -> Unit,
    onNightModeToggle: () -> Unit,
    isNightMode: Boolean
) {
    val textColor = if (isNightMode) Color(0xFFD0CEC4) else Color(0xFF3A3A3A)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            "阅读设置",
            style = MaterialTheme.typography.titleLarge.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            ),
            color = textColor
        )

        // Font size
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("字号", style = MaterialTheme.typography.bodyLarge, color = textColor)
                Text(
                    "${fontSize.toInt()}sp",
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor.copy(alpha = 0.6f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("A", fontSize = 14.sp, color = textColor)
                Slider(
                    value = fontSize,
                    onValueChange = onFontSizeChange,
                    valueRange = 12f..32f,
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
                Text("A", fontSize = 24.sp, color = textColor)
            }
        }

        // Line spacing
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("行距", style = MaterialTheme.typography.bodyLarge, color = textColor)
                Text(
                    "%.1f".format(lineSpacing),
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor.copy(alpha = 0.6f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("紧凑", fontSize = 13.sp, color = textColor.copy(alpha = 0.5f))
                Slider(
                    value = lineSpacing,
                    onValueChange = onLineSpacingChange,
                    valueRange = 1.0f..2.5f,
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
                Text("宽松", fontSize = 13.sp, color = textColor.copy(alpha = 0.5f))
            }
        }

        // Toggle row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { onNightModeToggle() }
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    if (isNightMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = textColor
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("深色模式", style = MaterialTheme.typography.bodyLarge, color = textColor)
            }
            Switch(
                checked = isNightMode,
                onCheckedChange = { onNightModeToggle() }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { onSerifToggle() }
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.TextFields,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = textColor
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("衬线字体", style = MaterialTheme.typography.bodyLarge, color = textColor)
            }
            Switch(
                checked = useSerif,
                onCheckedChange = { onSerifToggle() }
            )
        }
    }
}

package com.airnovel.app.ui.reader

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.airnovel.app.ui.theme.ReaderDayBg
import com.airnovel.app.ui.theme.ReaderNightBg
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

    LaunchedEffect(bookId, chapterId) {
        viewModel.initialize(bookId, chapterId, bookTitle, chapterIndex, chapterIdList)
    }

    // Track scroll progress
    LaunchedEffect(scrollState.value) {
        val maxScroll = (scrollState.maxValue).coerceAtLeast(1)
        val progress = if (maxScroll > 0) scrollState.value.toFloat() / maxScroll else 0f
        viewModel.updateScrollProgress(progress)
    }

    val bgColor = if (uiState.isNightMode) ReaderNightBg else ReaderDayBg
    val textColor = if (uiState.isNightMode) ReaderTextNight else ReaderTextDay
    val fontFamily = if (uiState.useSerif) FontFamily.Serif else FontFamily.Default

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        // Main content area with swipe detection
        Column(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = { /* handled by threshold */ },
                        onHorizontalDrag = { _, dragAmount ->
                            if (dragAmount < -50) {
                                scope.launch {
                                    viewModel.goToNextChapter()
                                    scrollState.scrollTo(0)
                                }
                            } else if (dragAmount > 50) {
                                scope.launch {
                                    viewModel.goToPrevChapter()
                                    scrollState.scrollTo(0)
                                }
                            }
                        }
                    )
                }
        ) {
            // Top bar (auto-hide)
            AnimatedVisibility(
                visible = showControls,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = bgColor.copy(alpha = 0.95f),
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "返回",
                                tint = textColor
                            )
                        }

                        Text(
                            text = uiState.chapterTitle.ifEmpty { "加载中..." },
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif
                            ),
                            color = textColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(onClick = { showSettings = !showSettings }) {
                            Icon(
                                Icons.Default.TextFields,
                                contentDescription = "阅读设置",
                                tint = textColor
                            )
                        }

                        IconButton(onClick = { viewModel.toggleNightMode() }) {
                            Icon(
                                if (uiState.isNightMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "夜间模式",
                                tint = textColor
                            )
                        }
                    }
                }
            }

            // Content area
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
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("加载中...", color = textColor)
                        }
                    }

                    uiState.error != null -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                uiState.error ?: "加载失败",
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = {
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
                                .padding(horizontal = 24.dp)
                                .padding(top = 16.dp, bottom = 16.dp)
                        ) {
                            // Chapter title
                            Text(
                                text = uiState.chapterTitle,
                                style = TextStyle(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp,
                                    color = textColor
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 20.dp),
                                textAlign = TextAlign.Center
                            )

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

                            Spacer(modifier = Modifier.height(32.dp))

                            // Navigation buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
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
                                        modifier = Modifier.size(20.dp)
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
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            // Bottom progress bar
            AnimatedVisibility(
                visible = showControls,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = bgColor.copy(alpha = 0.95f),
                    shadowElevation = 2.dp
                ) {
                    Column {
                        LinearProgressIndicator(
                            progress = { uiState.scrollProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.dp),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "第${uiState.currentChapterIndex + 1}/${uiState.totalChapters}章",
                                style = MaterialTheme.typography.bodySmall,
                                color = textColor.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "${(uiState.scrollProgress * 100).toInt()}%",
                                style = MaterialTheme.typography.bodySmall,
                                color = textColor.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }

        // Settings panel
        AnimatedVisibility(
            visible = showSettings,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Card(
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = if (uiState.isNightMode)
                        Color(0xFF333333)
                    else
                        Color(0xFFFFF8EF)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "阅读设置",
                        style = MaterialTheme.typography.titleMedium,
                        color = textColor
                    )

                    // Font size
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("字号", color = textColor, modifier = Modifier.width(40.dp))
                        IconButton(
                            onClick = { viewModel.updateFontSize((uiState.fontSize - 2f).coerceAtLeast(12f)) }
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "减小", tint = textColor)
                        }
                        Text(
                            "${uiState.fontSize.toInt()}",
                            color = textColor,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        IconButton(
                            onClick = { viewModel.updateFontSize((uiState.fontSize + 2f).coerceAtMost(32f)) }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "增大", tint = textColor)
                        }
                    }

                    // Line spacing
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("间距", color = textColor, modifier = Modifier.width(40.dp))
                        Slider(
                            value = uiState.lineSpacing,
                            onValueChange = { viewModel.updateLineSpacing(it) },
                            valueRange = 1.0f..2.5f,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            "%.1f".format(uiState.lineSpacing),
                            color = textColor,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    // Font toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("衬线字体", color = textColor)
                        Switch(
                            checked = uiState.useSerif,
                            onCheckedChange = { viewModel.toggleSerif() }
                        )
                    }

                    // Close button
                    Button(
                        onClick = { showSettings = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("关闭")
                    }
                }
            }
        }
    }
}

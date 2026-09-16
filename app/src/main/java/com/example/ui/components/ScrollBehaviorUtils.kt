package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier

/**
 * Custom scroll direction observer for LazyListState:
 * - Scrolling Down -> hides header smoothly
 * - Scrolling Up -> shows header smoothly
 * - At the top (index 0, offset <= 25) -> always shows header
 *
 * Designed with a sensitive yet jitter-free threshold (12px)
 * ensuring natural gesture response with zero touch conflicts.
 */
@Composable
fun rememberScrollHeaderVisibility(
    listState: LazyListState,
    thresholdPx: Int = 12,
    onVisibilityChanged: ((Boolean) -> Unit)? = null
): State<Boolean> {
    val isVisible = remember { mutableStateOf(true) }
    var previousIndex by remember { mutableIntStateOf(0) }
    var previousScrollOffset by remember { mutableIntStateOf(0) }

    LaunchedEffect(listState) {
        snapshotFlow {
            Triple(
                listState.firstVisibleItemIndex,
                listState.firstVisibleItemScrollOffset,
                listState.isScrollInProgress
            )
        }.collect { (currentIndex, currentOffset, isScrolling) ->
            if (currentIndex == 0 && currentOffset <= 25) {
                if (!isVisible.value) {
                    isVisible.value = true
                    onVisibilityChanged?.invoke(true)
                }
                previousIndex = currentIndex
                previousScrollOffset = currentOffset
                return@collect
            }

            if (!isScrolling) {
                previousIndex = currentIndex
                previousScrollOffset = currentOffset
                return@collect
            }

            if (currentIndex > previousIndex) {
                // Scrolled downwards past item boundaries
                if (isVisible.value) {
                    isVisible.value = false
                    onVisibilityChanged?.invoke(false)
                }
            } else if (currentIndex < previousIndex) {
                // Scrolled upwards past item boundaries
                if (!isVisible.value) {
                    isVisible.value = true
                    onVisibilityChanged?.invoke(true)
                }
            } else {
                // Moving within the same item
                val delta = currentOffset - previousScrollOffset
                if (delta > thresholdPx && isVisible.value) {
                    isVisible.value = false
                    onVisibilityChanged?.invoke(false)
                } else if (delta < -thresholdPx && !isVisible.value) {
                    isVisible.value = true
                    onVisibilityChanged?.invoke(true)
                }
            }

            previousIndex = currentIndex
            previousScrollOffset = currentOffset
        }
    }

    return isVisible
}

/**
 * Standard lightweight animated visibility wrapper for collapsible headers.
 * Uses fast 180ms-200ms tweens for instant, fluid transitions.
 */
@Composable
fun CollapsibleHeaderContainer(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = expandVertically(
            animationSpec = tween(durationMillis = 200)
        ) + fadeIn(
            animationSpec = tween(durationMillis = 150)
        ),
        exit = shrinkVertically(
            animationSpec = tween(durationMillis = 200)
        ) + fadeOut(
            animationSpec = tween(durationMillis = 150)
        ),
        modifier = modifier,
        content = content
    )
}

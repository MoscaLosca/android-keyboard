package org.futo.inputmethod.latin.uix

import android.text.InputType
import android.view.inputmethod.EditorInfo
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.uix.theme.LocalCompatEmojiFamily
import org.futo.inputmethod.latin.uix.theme.LocalCompatEmojiTypeface
import org.futo.inputmethod.latin.uix.theme.UixThemeWrapper
import org.futo.inputmethod.latin.uix.theme.emojiNeedsCompat
import org.futo.inputmethod.latin.uix.theme.presets.DefaultDarkScheme
import org.futo.inputmethod.latin.uix.theme.presets.DefaultLightScheme

const val EmojiBarHeightDp = 36.0f
val EmojiBarHeight = EmojiBarHeightDp.dp

private val EmojiBarItemSize = 34.dp
private val EmojiBarTextSize = 22.sp

/**
 * Emoji are only useful in text fields that are not password fields.
 * Number, phone, datetime and null input classes are rejected by the class check.
 */
fun EditorInfo?.supportsEmojiBar(): Boolean {
    if (this == null) return false
    if (inputType and InputType.TYPE_MASK_CLASS != InputType.TYPE_CLASS_TEXT) return false

    return when (inputType and InputType.TYPE_MASK_VARIATION) {
        InputType.TYPE_TEXT_VARIATION_PASSWORD,
        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD,
        InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD -> false
        else -> true
    }
}

@Composable
fun EmojiBar(
    emojis: List<String>,
    onEmojiSelected: (String) -> Unit,
    onOpenEmojiPanel: () -> Unit,
    modifier: Modifier = Modifier,
    drawBottomSeparator: Boolean = false,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(EmojiBarHeight)
            .testTag("EmojiBar")
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.0f),
            color = actionBarColor()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .safeKeyboardPadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LazyRow(
                    modifier = Modifier
                        .weight(1.0f)
                        .fillMaxHeight(),
                    contentPadding = PaddingValues(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(emojis, key = { it }) { emoji ->
                        EmojiBarItem(emoji = emoji, onEmojiSelected = onEmojiSelected)
                    }
                }

                EmojiBarOpenButton(onOpenEmojiPanel = onOpenEmojiPanel)
            }
        }

        if (drawBottomSeparator) {
            ActionSep()
        }
    }
}

@Composable
private fun EmojiBarItem(emoji: String, onEmojiSelected: (String) -> Unit) {
    val compatTypeface = LocalCompatEmojiTypeface.current
    val compatFamily = LocalCompatEmojiFamily.current
    val fontFamily = remember(emoji, compatTypeface) {
        if (emojiNeedsCompat(emoji, compatTypeface)) compatFamily else null
    }

    Box(
        modifier = Modifier
            .size(EmojiBarItemSize)
            .clip(CircleShape)
            .clickable(role = Role.Button) { onEmojiSelected(emoji) }
            .semantics { contentDescription = emoji }
            .testTag("EmojiBarItem"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emoji,
            style = TextStyle(
                fontSize = EmojiBarTextSize,
                fontFamily = fontFamily,
                color = LocalKeyboardScheme.current.onSurface,
                textAlign = TextAlign.Center
            ),
            maxLines = 1,
            softWrap = false
        )
    }
}

@Composable
private fun EmojiBarOpenButton(onOpenEmojiPanel: () -> Unit) {
    val description = stringResource(R.string.action_emoji_title)
    val color = LocalKeyboardScheme.current.onSurfaceVariant

    Box(
        modifier = Modifier
            .size(EmojiBarItemSize)
            .clip(CircleShape)
            .clickable(role = Role.Button, onClick = onOpenEmojiPanel)
            .semantics { contentDescription = description }
            .testTag("EmojiBarOpenEmoji"),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.smile),
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Preview
@Composable
fun PreviewEmojiBar() {
    UixThemeWrapper(DefaultDarkScheme.obtainColors(LocalContext.current)) {
        EmojiBar(
            emojis = listOf("😀", "😂", "❤️", "👍", "🔥"),
            onEmojiSelected = {},
            onOpenEmojiPanel = {}
        )
    }
}

@Preview
@Composable
fun PreviewEmojiBarLight() {
    UixThemeWrapper(DefaultLightScheme.obtainColors(LocalContext.current)) {
        EmojiBar(
            emojis = listOf("😀", "😂", "❤️", "👍", "🔥"),
            onEmojiSelected = {},
            onOpenEmojiPanel = {}
        )
    }
}

@Preview
@Composable
fun PreviewEmojiBarEmpty() {
    UixThemeWrapper(DefaultDarkScheme.obtainColors(LocalContext.current)) {
        EmojiBar(
            emojis = emptyList(),
            onEmojiSelected = {},
            onOpenEmojiPanel = {}
        )
    }
}

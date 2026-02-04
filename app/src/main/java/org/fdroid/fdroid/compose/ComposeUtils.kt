package org.fdroid.fdroid.compose

import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation  // 新增导入（如果编译报错，可删）
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.android.material.color.MaterialColors

object ComposeUtils {
    @Composable
    fun FDroidButton(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        imageVector: ImageVector? = null,
    ) {
        Button(
            onClick = onClick,
            shape = RoundedCornerShape(0.dp),
            // 关键：强制 elevation = 0.dp，无阴影 → 避免 GraphicsLayer outline 计算
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp,
                focusedElevation = 0.dp,
                hoveredElevation = 0.dp,
                disabledElevation = 0.dp
            ),
            // 可选 hack：添加 graphicsLayer alpha <1 强制某些 bug 路径绕过（常见 WebView/Compose crash 修复）
            modifier = modifier
                .heightIn(min = ButtonDefaults.MinHeight)
                .graphicsLayer { alpha = 0.999f },  // <1 的 alpha 常用于绕过某些 layer bug
        ) {
            if (imageVector != null) {
                Icon(
                    imageVector = imageVector,
                    contentDescription = text,
                    modifier = Modifier.size(ButtonDefaults.IconSize),
                )
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            }
            Text(text = text)
        }
    }

    @Composable
    fun FDroidOutlineButton(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        imageVector: ImageVector? = null,
        color: Color = MaterialTheme.colorScheme.primary,
    ) {
        OutlinedButton(
            onClick = onClick,
            shape = RoundedCornerShape(0.dp),
            // 关键：OutlinedButton 默认无 elevation，但强制 0.dp 以防
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp,
                focusedElevation = 0.dp,
                hoveredElevation = 0.dp,
                disabledElevation = 0.dp
            ),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = color),
            // 同上 hack
            modifier = modifier
                .heightIn(min = ButtonDefaults.MinHeight)
                .graphicsLayer { alpha = 0.999f },
        ) {
            if (imageVector != null) {
                Icon(
                    imageVector = imageVector,
                    contentDescription = text,
                    modifier = Modifier.size(ButtonDefaults.IconSize),
                )
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            }
            Text(text = text, maxLines = 1)
        }
    }

    // LifecycleEventListener 和 CaptionText 函数保持不变
    @Composable
    fun LifecycleEventListener(onEvent: (owner: LifecycleOwner, event: Lifecycle.Event) -> Unit) {
        val eventHandler = rememberUpdatedState(onEvent)
        val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)
        DisposableEffect(lifecycleOwner.value) {
            val lifecycle = lifecycleOwner.value.lifecycle
            val observer = LifecycleEventObserver { owner, event ->
                eventHandler.value(owner, event)
            }
            lifecycle.addObserver(observer)
            onDispose {
                lifecycle.removeObserver(observer)
            }
        }
    }

    @Composable
    fun CaptionText(text: String) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(0.dp, 16.dp, 0.dp, 4.dp)
        )
    }
}

@Composable
@ColorInt
@ReadOnlyComposable
fun colorAttribute(@AttrRes attrColor: Int): Color {
    val color = MaterialColors.getColor(LocalContext.current, attrColor, 0)
    return Color(color)
}
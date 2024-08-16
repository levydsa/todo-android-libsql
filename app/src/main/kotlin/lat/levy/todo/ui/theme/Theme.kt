package lat.levy.todo.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0XFF4FF8D2),

    primaryContainer = Color(0XFF4FF8D2),
    onPrimaryContainer = Color(0xFF00483b),

    secondaryContainer = Color(0xFFCC454F),
    onSecondaryContainer = Color(0xFFFFFFFF),

    surfaceVariant = Color(0xFF15262B),

    // background = Color(0xFF121b22),
    // onBackground = Color(0xFFfafafa),
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
)

@Composable
fun TodoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = when {
            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        },
        typography = Typography,
        content = content,
    )
}
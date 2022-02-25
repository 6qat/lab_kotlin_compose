import androidx.compose.material.MaterialTheme
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlin.coroutines.coroutineContext

private const val API_KEY = "c856cbcaf6a04de2b64141129222002"

// https://www.raywenderlich.com/26791460-compose-for-desktop-get-your-weather

fun main() = application {
    Window(title = "Sunny Desk", onCloseRequest = ::exitApplication) {
        val repository = Repository(API_KEY)
        MaterialTheme {
            WeatherScreen(repository)
        }
    }
}

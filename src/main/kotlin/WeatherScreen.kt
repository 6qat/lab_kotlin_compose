import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun WeatherScreen(repository: Repository) {

    var queriedCity by remember { mutableStateOf("") }
    var weatherState by remember { mutableStateOf<Lce<WeatherResults>?>(null) }

    /**
     * Use this scope to launch jobs in response to callback events such as clicks
     * or other user interaction where the response to that event needs to unfold
     * over time and be cancelled if the composable managing that process leaves the
     * composition. Jobs should never be launched into any coroutine scope as a
     * side effect of composition itself. For scoped ongoing jobs initiated by composition,
     * see LaunchedEffect.
     */
    val scope = rememberCoroutineScope()

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            TextField(
                value = queriedCity,
                onValueChange = { queriedCity = it },
                modifier = Modifier.padding(end = 16.dp).weight(1f, true),
                placeholder = { Text("Any city, really...") },
                label = { Text(text = "Search for a city") },
                leadingIcon = { Icon(Icons.Filled.LocationOn, "Location") },
            )
            Button(onClick = {
                weatherState = Lce.Loading
                scope.launch {
                    println("From Button onClick(): ${this.coroutineContext == coroutineContext}")
                    weatherState = repository.weatherForCity(queriedCity)
                }
            }) {
                Icon(Icons.Outlined.Search, "Search")
            }
        }
        when (val state = weatherState) {
            is Lce.Loading -> LoadingUI()
            is Lce.Error -> ErrorUI()
            is Lce.Content -> ContentUI(state.data)
        }
    }
}

fun CoroutineScope.doThis() {
    launch {
        println("I'm fine")
    }
}

fun doThatIn(scope: CoroutineScope) {
    scope.launch {
        println("I'm fine, too")
    }
}

@Composable
fun LoadingUI() {

    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(
            modifier = Modifier
                .align(alignment = Alignment.Center)
                .defaultMinSize(minWidth = 96.dp, minHeight = 96.dp)
        )
    }
}

@Composable
fun ContentUI(data: WeatherResults) {
    var imageState by remember { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(data.currentWeather.iconUrl) {
        imageState = ImageDownloader.downloadImage(data.currentWeather.iconUrl)
    }
    Text(
        text = "Current weather",
        modifier = Modifier.padding(all = 16.dp),
        style = MaterialTheme.typography.h6,
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 72.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = data.currentWeather.condition,
                style = MaterialTheme.typography.h6,
            )

            imageState?.let { bitmap ->
                Image(
                    bitmap = bitmap,
                    contentDescription = null,
                    modifier = Modifier
                        .defaultMinSize(minWidth = 128.dp, minHeight = 128.dp)
                        .padding(top = 8.dp)
                )
            }

            Text(
                text = "Temperature in °C: ${data.currentWeather.temperature}",
                modifier = Modifier.padding(all = 8.dp),
            )
            Text(
                text = "Feels like: ${data.currentWeather.feelsLike}",
                style = MaterialTheme.typography.caption,
            )
        }
    }
    Divider(
        color = MaterialTheme.colors.primary,
        modifier = Modifier.padding(all = 16.dp),
    )
    Text(
        text = "Forecast",
        modifier = Modifier.padding(all = 16.dp),
        style = MaterialTheme.typography.h6,
    )
    LazyRow(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth(1f)
    ) {
        items(data.forecast) { weatherCard ->
            ForecastUI(weatherCard)
        }
    }

}

@Composable
fun ForecastUI(weatherCard: WeatherCard) {
    var imageState by remember { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(weatherCard.iconUrl) {
        imageState = ImageDownloader.downloadImage(weatherCard.iconUrl)
    }

    Card(modifier = Modifier.padding(all = 4.dp)) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = weatherCard.condition,
                style = MaterialTheme.typography.h6
            )

            imageState?.let { bitmap ->
                Image(
                    bitmap = bitmap,
                    contentDescription = null,
                    modifier = Modifier
                        .defaultMinSize(minWidth = 64.dp, minHeight = 64.dp)
                        .padding(top = 8.dp)
                )
            }

            val chanceOfRainText = String.format(
                "Chance of rain: %.2f%%", weatherCard.chanceOfRain
            )

            Text(
                text = chanceOfRainText,
                style = MaterialTheme.typography.caption,
            )
        }
    }
}

@Composable
fun ErrorUI() {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Something went wrong, try again in a few minutes. ¯\\_(ツ)_/¯",
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 72.dp, vertical = 72.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h6,
            color = MaterialTheme.colors.error,
        )
    }
}
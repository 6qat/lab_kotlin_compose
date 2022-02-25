/**
 * Domain model data structures
 */

data class WeatherCard(
    val condition: String,
    val iconUrl: String,
    val temperature: Double,
    val feelsLike: Double, // used only on Forecast
    val chanceOfRain: Double? = null, // used only on Forecast
)

data class WeatherResults(
    val currentWeather: WeatherCard,
    val forecast: List<WeatherCard>,
)

class WeatherTransformer {
    private fun extractCurrentWeatherFrom(response: WeatherResponse): WeatherCard {
        return WeatherCard(
            condition = response.current.condition.text,
            iconUrl = "https:" + response.current.condition.icon.replace("64x64", "128x128"),
            temperature = response.current.tempC,
            feelsLike = response.current.feelslikeC,
        )
    }

    private fun extractForecastWeatherFrom(response: WeatherResponse): List<WeatherCard> {
        return response.forecast.forecastday.map { forecastDay ->
            WeatherCard(
                condition = forecastDay.day.condition.text,
                iconUrl = "https:" + forecastDay.day.condition.icon,
                temperature = forecastDay.day.avgtempC,
                feelsLike = avgFeelsLike(forecastDay),
                chanceOfRain = avgChanceOfRain(forecastDay),
            )
        }
    }

    private fun avgFeelsLike(forecastDay: Forecastday): Double =
        forecastDay.hour.map(Hour::feelslikeC).average()
    // forecastDay.hour.map{h -> h.feelslikeC}.average()

    private fun avgChanceOfRain(forecastDay: Forecastday): Double =
        forecastDay.hour.map { h ->
            h.chanceOfRain
        }.average()
    //forecastDay.hour.map(Hour::chanceOfRain).average()

    fun transform(response: WeatherResponse): WeatherResults {
        val current = extractCurrentWeatherFrom(response)
        val forecast = extractForecastWeatherFrom(response)

        return WeatherResults(
            currentWeather = current,
            forecast = forecast,
        )
    }
}








import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Job
import kotlin.coroutines.coroutineContext

class Repository(
    private val apiKey: String,
    private val client: HttpClient = defaultHttpClient,
) {

    private val transformer = WeatherTransformer()

    suspend fun weatherForCity(city: String): Lce<WeatherResults> {
        println("My CONTEXT: ${coroutineContext + CoroutineName("weatherForCity")}")
        println("My CONTEXT Job: ${coroutineContext[Job]}")
        return try {
            val result = getWeatherForCity(city)
            val content: WeatherResults = transformer.transform(result)
            Lce.Content(content)
        } catch (e: Exception) {
            e.printStackTrace()
            Lce.Error(e)
        }
    }

    private suspend fun getWeatherForCity(city: String): WeatherResponse {
        println("My CONTEXT: $coroutineContext")
        return client.get(
            "https://api.weatherapi.com/v1/forecast.json" +
                    "?key=$apiKey" +
                    "&q=$city" +
                    "&days=5" +
                    "&aqi=no" +
                    "&alerts=no"
        )
    }

    companion object {
        val defaultHttpClient = HttpClient(CIO) {
            install(JsonFeature) {
                serializer = KotlinxSerializer(
                    json = kotlinx.serialization.json.Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
        }
    }
}

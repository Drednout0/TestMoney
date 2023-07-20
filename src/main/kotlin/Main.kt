import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.runBlocking
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*

data class CurrencyRate(
    @JsonProperty("CharCode")
    val charCode: String,

    @JsonProperty("Name")
    val name: String,

    @JsonProperty("Value")
    val value: String
)

data class ValCurs(
    @JsonProperty("Date")
    val date: String,

    @JsonProperty("name")
    val name: String,

    @JsonProperty("Valute")
    val valute: Map<String, CurrencyRate>
)

interface CbrApi {
    @GET("XML_daily.asp")
    suspend fun getCurrencyRates(@Query("date_req") date: String): ValCurs
}

fun main() {
    val scanner = Scanner(System.`in`)

    println("Введите код валюты (например, USD):")
    val currencyCode = scanner.nextLine()

    println("Введите дату (в формате dd/MM/yyyy):")
    val dateStr = scanner.nextLine()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://www.cbr.ru/scripts/")
        .addConverterFactory(JacksonConverterFactory.create(jacksonObjectMapper()))
        .build()

    val cbrApi = retrofit.create(CbrApi::class.java)

    runBlocking {
        try {
            val response = cbrApi.getCurrencyRates(dateStr)

            if (response.valute.containsKey(currencyCode)) {
                val currencyRate = response.valute[currencyCode]!!
                println("${currencyRate.charCode} (${currencyRate.name}): ${currencyRate.value}")
            } else {
                println("Код валюты не найден.")
            }
        } catch (e: Exception) {
            println("Произошла ошибка: ${e.message}")
        }
    }
}

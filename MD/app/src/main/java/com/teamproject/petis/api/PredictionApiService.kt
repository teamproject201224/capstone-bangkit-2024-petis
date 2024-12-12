import com.teamproject.petis.api.PredictionResponse
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.util.concurrent.TimeUnit

interface PredictionApiService {
    @Multipart
    @POST("/predict")
    fun predictImage(@Part image: MultipartBody.Part): Call<PredictionResponse>

    companion object {
        private const val BASE_URL = "https://venv-256962535268.asia-southeast2.run.app"

        fun create(): PredictionApiService {
            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(PredictionApiService::class.java)
        }
    }
}
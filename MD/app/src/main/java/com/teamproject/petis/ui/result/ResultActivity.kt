package com.teamproject.petis.ui.result

import PredictionApiService
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.teamproject.petis.adapter.RecommendedProductAdapter
import com.teamproject.petis.api.PredictionResponse
import com.teamproject.petis.api.RecommendedProductResponse
import com.teamproject.petis.databinding.ActivityResultBinding
import com.teamproject.petis.db.HistoryEntity
import com.teamproject.petis.db.HistoryDatabase
import com.teamproject.petis.db.HistoryRepository
import com.teamproject.petis.helper.ImageClassifier
import com.teamproject.petis.ui.detail.DetailRecommendedProductActivity
import com.teamproject.petis.ui.history.HistoryViewModel
import com.teamproject.petis.ui.history.HistoryViewModelFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

class ResultActivity : AppCompatActivity() {
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var binding: ActivityResultBinding
    private lateinit var imageClassifier: ImageClassifier
    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var apiService: PredictionApiService
    private lateinit var recommendedProductAdapter: RecommendedProductAdapter

    companion object {
        private const val TAG = "ResultActivity"
        var shouldAutoStartCamera = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi komponen
        initializeComponents()

        // Proses data dari intent
        processIntentData()
    }

    override fun onBackPressed() {
        // Set flag sebelum kembali
        shouldAutoStartCamera = true
        super.onBackPressed()
    }

    private fun initializeComponents() {
        // Inisialisasi ImageClassifier
        imageClassifier = ImageClassifier(this)

        // Inisialisasi API Service
        apiService = PredictionApiService.create()

        // Setup ViewModel
        setupViewModel()

        // Setup UI Listeners
        setupUIListeners()

        // Setup RecyclerView
        setupRecyclerView()
    }

    private fun setupViewModel() {
        val historyDao = HistoryDatabase.getDatabase(application).historyDao()
        val historyRepository = HistoryRepository(historyDao)
        historyViewModel = ViewModelProvider(
            this,
            HistoryViewModelFactory(historyRepository)
        ).get(HistoryViewModel::class.java)
    }

    private fun setupUIListeners() {
        // Gunakan toolbar untuk navigasi kembali
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        recommendedProductAdapter = RecommendedProductAdapter(emptyList()) { product ->
            // Navigasi ke halaman detail produk
            val intent = Intent(this, DetailRecommendedProductActivity::class.java).apply {
                putExtra(DetailRecommendedProductActivity.EXTRA_PRODUCT, product)
            }
            startActivity(intent)
        }

        binding.rvRecommendedProducts.apply {
            layoutManager = LinearLayoutManager(
                this@ResultActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = recommendedProductAdapter
            setHasFixedSize(true)
        }
    }

    private fun processIntentData() {
        val prediction = intent.getStringExtra("PREDICTION") ?: "Unknowm"
        val confidenceString = intent.getStringExtra("CONFIDENCE") ?: "0.0"
        val description = intent.getStringExtra("DESCRIPTION") ?: prediction
        val imageUriString = intent.getStringExtra("IMAGE_URI")
        val imageUri = imageUriString?.let { Uri.parse(it) }

        imageUri?.let { uri ->
            displayImageAndPrediction(uri, description, confidenceString)
            uploadImageForRecommendation(uri)
        } ?: run {
            binding.predictionTextView.text = "No images selected"
        }
    }

    private fun displayImageAndPrediction(imageUri: Uri, description: String, confidenceString: String) {
        // Tampilkan gambar
        Glide.with(this)
            .load(imageUri)
            .centerCrop()
            .into(binding.resultImageView)

        // Tampilkan hasil prediksi
        binding.predictionTextView.text = description

        // Konversi dan tampilkan confidence
        val confidencePercentage = (confidenceString.toFloatOrNull() ?: 0f) * 100
        binding.confidenceTextView.text = "${String.format("%.2f", confidencePercentage)}%"
        binding.confidenceProgressBar.progress = confidencePercentage.toInt()

        // Set tingkat kepercayaan
        binding.confidenceLevelText.text = when {
            confidencePercentage >= 80 -> "High"
            confidencePercentage >= 50 -> "Medium"
            else -> "Low"
        }

        // Set rekomendasi
        binding.recommendationTextView.text = getRecommendation(description)

        // Simpan ke history
        saveToHistory(description, imageUri, confidencePercentage)
    }

    private fun saveToHistory(description: String, imageUri: Uri, confidence: Float = 0f) {
        if (description.isNotEmpty()) {
            val history = HistoryEntity(
                prediction = description,
                date = getCurrentDate(),
                imageUrl = imageUri.toString(),
                confidence = confidence
            )
            historyViewModel.insert(history)
        }
    }

    private fun uploadImageForRecommendation(imageUri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(imageUri)
            val tempFile = File(cacheDir, "temp_image.jpg")

            // Kompresi gambar
            inputStream?.use { input ->
                tempFile.outputStream().use { output ->
                    val bitmap = BitmapFactory.decodeStream(input)
                    val outputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
                    output.write(outputStream.toByteArray())
                }
            }

            if (tempFile.exists()) {
                val requestFile = tempFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imagePart = MultipartBody.Part.createFormData("image", tempFile.name, requestFile)

                val call = apiService.predictImage(imagePart)
                call.enqueue(object : Callback<PredictionResponse> {
                    override fun onResponse(
                        call: Call<PredictionResponse>,
                        response: Response<PredictionResponse>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let { predictionResponse ->
                                Log.d(TAG, "Prediction: ${predictionResponse.predictedClass}")
                                Log.d(TAG, "Confidence: ${predictionResponse.confidence}")
                                Log.d(TAG, "Products count: ${predictionResponse.products.size}")

                                runOnUiThread {
                                    updateRecommendedProducts(predictionResponse.products)
                                }
                            } ?: run {
                                Log.e(TAG, "Response body is null")
                                showErrorMessage("Failed to load data")
                            }
                        } else {
                            try {
                                val errorBody = response.errorBody()?.string()
                                Log.e(TAG, "Error Response: $errorBody")
                                showErrorMessage("Failed to load recommendations")
                            } catch (e: Exception) {
                                Log.e(TAG, "Error parsing error body", e)
                                showErrorMessage("There is an error")
                            }
                        }
                    }

                    override fun onFailure(call: Call<PredictionResponse>, t: Throwable) {
                        Log.e(TAG, "Network Error", t)
                        showErrorMessage("Failed to load recommendations: ${t.localizedMessage}")
                    }
                })
            } else {
                Log.e(TAG, "Temp file does not exist")
                showErrorMessage("Failed to load image")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error processing image", e)
            showErrorMessage("There is an error")
        }
    }

    private fun showErrorMessage(message: String) {
        runOnUiThread {
            Toast.makeText(this@ResultActivity, message, Toast.LENGTH_SHORT).show()
            binding.recommendedProductsCardView.visibility = View.GONE
        }
    }

    private fun updateRecommendedProducts(products: List<RecommendedProductResponse>) {
        Log.d(TAG, "Number of products: ${products.size}")

        // Filter produk unik berdasarkan nama
        val uniqueProducts = products.distinctBy { it.name }

        recommendedProductAdapter.updateProducts(uniqueProducts)

        // Tampilkan atau sembunyikan CardView berdasarkan jumlah produk
        binding.recommendedProductsCardView.visibility =
            if (uniqueProducts.isNotEmpty()) View.VISIBLE else View.GONE

        // Log detail produk untuk debugging
        uniqueProducts.forEachIndexed { index, product ->
            Log.d(TAG, "Product $index: ${product.name}, Image: ${product.image}")
        }
    }

    private fun navigateToProductDetail(product: RecommendedProductResponse) {
        val intent = Intent(this, DetailRecommendedProductActivity::class.java).apply {
            putExtra(DetailRecommendedProductActivity.EXTRA_PRODUCT, product)
        }
        startActivity(intent)
    }

    // Fungsi getRecommendation dan getCurrentDate tetap sama seperti sebelumnya
    private fun getRecommendation(prediction: String): String {
        return when {
            prediction.contains("Cashew anthracnose", ignoreCase = true) ->
                "Use copper-based fungicides such as copper oxychloride at a dose of 2-3 grams per liter of water. Prune infected plant parts using sterilized tools and burn the remains to prevent the spread. Ensure proper planting distance to improve air circulation."

            prediction.contains("Cashew gumosis", ignoreCase = true) ->
                "Prune infected plant parts using a sharp sterilized knife. Afterward, apply fungicide solutions such as thiophanate-methyl to the wounds. Ensure good drainage around the plants by creating water channels and avoid wounding the trunk during cultivation activities."

            prediction.contains("Cashew healthy", ignoreCase = true) ->
                "Continue maintenance by applying NPK fertilizer with a 15:15:15 ratio at 200 grams per tree every 3 months. Prune overly dense branches to prevent diseases, and keep the area around the plants free of weeds."

            prediction.contains("Cashew leaf miner", ignoreCase = true) ->
                "Use systemic insecticides such as abamectin at a dose of 0.5-1 ml per liter of water. Collect and burn infected leaves to prevent larvae spread. Monitor weekly, especially during the rainy season."

            prediction.contains("Cashew red rust", ignoreCase = true) ->
                "Spray fungicides such as mancozeb at a dose of 2 grams per liter of water on all parts of the plant. Ensure optimal soil drainage to avoid excess humidity, and prune leaves showing infection symptoms, then burn them."

            prediction.contains("Cassava bacterial blight", ignoreCase = true) ->
                "Use resistant varieties such as Adira 4 or Malang 6. Destroy infected plants by uprooting and burning all parts of the plant. Ensure proper field sanitation before replanting, and avoid excessive irrigation."

            prediction.contains("Cassava brown spot", ignoreCase = true) ->
                "Apply fungicides with active ingredients like copper hydroxide at a dose of 2 grams per liter of water. Rotate crops with non-host plants such as corn or peanuts. Clean up plant debris from the field after harvesting to prevent re-infection."

            prediction.contains("Cassava green mite", ignoreCase = true) ->
                "Spray neem oil at a concentration of 1-2% to control green mites. Add organic mulch to maintain soil moisture and avoid synthetic pesticides that can harm natural predators of the mites."

            prediction.contains("Cassava healthy", ignoreCase = true) ->
                "Ensure proper maintenance by applying organic fertilizer at 5 kg per plant every 6 months. Control weeds manually or with selective herbicides. Maintain field sanitation to keep plants healthy."

            prediction.contains("Cassava mosaic", ignoreCase = true) ->
                "Use certified, virus-free seeds, such as Taj Mahal or Sri Kepe varieties. Destroy infected plants immediately after symptoms appear, control aphid vectors using imidacloprid, and maintain a safe distance between plants to prevent virus spread."

            else -> "Consult with agricultural experts or local extension officers for further and more specific treatment based on your plant's condition."
        }
    }


    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    override fun onDestroy() {
        super.onDestroy()
        imageClassifier.close()
    }
}


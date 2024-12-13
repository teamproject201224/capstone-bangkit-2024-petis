package com.teamproject.petis.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.teamproject.petis.databinding.ActivityDetailHistoryBinding
import com.teamproject.petis.db.HistoryEntity

class DetailHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil data dari intent
        val historyEntity = intent.getParcelableExtra<HistoryEntity>("HISTORY_ENTITY")

        // Tampilkan data dari history
        historyEntity?.let { history ->
            Glide.with(this)
                .load(history.imageUrl)
                .centerCrop()
                .into(binding.detailImageView)

            binding.historyDate.text = history.date // Gunakan history.date
            binding.predictionTextView.text = history.prediction // Gunakan history.prediction

            val confidencePercentage = history.confidence * 1
            binding.confidenceTextView.text = "${String.format("%.2f", confidencePercentage)}%"
            binding.confidenceProgressBar.progress = confidencePercentage.toInt()
            binding.confidenceLevelText.text = getConfidenceLevel(history.confidence) // Gunakan history.confidence

            binding.recommendationTextView.text = getRecommendation(history.prediction)
        }

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    // Fungsi untuk mendapatkan tingkat kepercayaan
    private fun getConfidenceLevel(confidence: Float): String {
        return when {
            confidence >= 0.8 -> "High"
            confidence >= 0.5 -> "Medium"
            else -> "Low"
        }
    }

    // Fungsi untuk mendapatkan rekomendasi
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
}
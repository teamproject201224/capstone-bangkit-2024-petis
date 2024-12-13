package com.teamproject.petis.ui.detail

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.teamproject.petis.R
import com.teamproject.petis.databinding.ActivityDetailPestBinding
import com.teamproject.petis.response.PestResponseItem

class DetailPestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailPestBinding

    companion object {
        const val EXTRA_PEST = "extra_pest"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pest = intent.getParcelableExtra<PestResponseItem>(EXTRA_PEST)

        pest?.let {
            updateUI(it)
        } ?: run {
            Toast.makeText(this, "Pest data not found", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun updateUI(pest: PestResponseItem) {
        binding.apply {
            Glide.with(this@DetailPestActivity)
                .load(pest.image)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(imgPest)

            tvPestName.text = pest.name
            tvPestDescription.text = pest.description
            tvPestSolution.text = pest.solution
        }
    }
}

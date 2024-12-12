package com.teamproject.petis.ui.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.teamproject.petis.R
import com.teamproject.petis.ui.auth.LoginActivity
import com.teamproject.petis.ui.main.MainActivity
import com.teamproject.petis.utils.SharedPreferencesManager
import com.teamproject.petis.utils.SharedViewModel

class SettingsActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var radioGroupGender: RadioGroup
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private lateinit var lottieAnimationView: LottieAnimationView
    private lateinit var tvUserName: TextView
    private lateinit var tvUserEmail: TextView
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        // Inisialisasi SharedPreferences PALING AWAL
        sharedPreferencesManager = SharedPreferencesManager(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Inisialisasi view-view yang dibutuhkan
        initializeViews()

        // Setup action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Settings"

        // Inisialisasi Firebase Authentication
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser ?: run {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        loadUserProfile()

        // Set informasi pengguna
        setUserInfo()

        // Setup Lottie Animation
        setupLottieAnimation()

        // Setup Gender RadioGroup
        setupGenderRadioGroup()

        // Setup click listeners
        setupClickListeners()
    }

    private fun initializeViews() {
        tvUserName = findViewById(R.id.tvUserName)
        tvUserEmail = findViewById(R.id.tvUserEmail)
        lottieAnimationView = findViewById(R.id.lottieAnimationView)
        radioGroupGender = findViewById(R.id.radioGroupGender)
    }

    private fun setUserInfo() {
        // Ambil nama dari SharedPreferences atau Firebase
        val userName = user.displayName
            ?: sharedPreferencesManager.getUserName()
            ?: "User"

        tvUserName.text = userName
        tvUserEmail.text = user.email ?: "Email tidak tersedia"
    }

    private fun setupGenderRadioGroup() {
        // Set default selection based on saved preference
        val savedGender = sharedPreferencesManager.getUserGender()
        when (savedGender) {
            "female" -> radioGroupGender.check(R.id.radioFemale)
            else -> radioGroupGender.check(R.id.radioMale)
        }

        // Set listener untuk RadioGroup
        radioGroupGender.setOnCheckedChangeListener { _, checkedId ->
            val selectedGender = when (checkedId) {
                R.id.radioFemale -> "female"
                R.id.radioMale -> "male"
                else -> "male"
            }

            // Simpan pilihan gender
            sharedPreferencesManager.saveUserGender(selectedGender)

            // Update animasi
            updateProfileAnimation(checkedId)
        }
    }

    private fun setupClickListeners() {
        findViewById<TextView>(R.id.btnEditProfile).setOnClickListener {
            showEditProfileDialog()
        }

        findViewById<TextView>(R.id.btnChangePassword).setOnClickListener {
            showChangePasswordDialog()
        }

        findViewById<TextView>(R.id.btnDeleteAccount).setOnClickListener {
            showDeleteAccountDialog()
        }

        findViewById<MaterialButton>(R.id.btnLogout).setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun setupLottieAnimation() {
        try {
            val animationRes = when (sharedPreferencesManager.getUserGender()) {
                "female" -> R.raw.profile_female
                else -> R.raw.profile
            }

            lottieAnimationView.apply {
                setAnimation(animationRes)
                repeatCount = LottieDrawable.INFINITE
                playAnimation()
            }
        } catch (e: Exception) {
            Log.e("LottieAnimation", "Error loading animation", e)
            lottieAnimationView.visibility = View.GONE
        }
    }

    private fun updateProfileAnimation(checkedId: Int) {
        val animationRes = when (checkedId) {
            R.id.radioFemale -> R.raw.profile_female
            R.id.radioMale -> R.raw.profile
            else -> R.raw.profile
        }

        lottieAnimationView.setAnimation(animationRes)
        lottieAnimationView.playAnimation()
    }

    private fun updateProfileName(newName: String) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(newName)
            .build()

        user.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Simpan nama di SharedPreferences
                    sharedPreferencesManager.saveUserName(newName)

                    // Update ViewModel
                    val sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
                    sharedViewModel.updateUsername(newName)

                    // Update TextView di Settings
                    findViewById<TextView>(R.id.tvUserName).text = newName

                    Toast.makeText(this, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Gagal memperbarui profil", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun showEditProfileDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_profile, null)
        val editNameEt = dialogView.findViewById<TextInputEditText>(R.id.etEditName)

        // Set current name
        editNameEt.setText(user.displayName ?: sharedPreferencesManager.getUserName())

        AlertDialog.Builder(this)
            .setTitle("Edit Profil")
            .setView(dialogView)
            .setPositiveButton("Simpan") { _, _ ->
                val newName = editNameEt.text.toString().trim()
                updateProfileName(newName)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    @SuppressLint("MissingInflatedId")
    private fun showChangePasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_change_password, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        // Temukan view
        val etCurrentPassword = dialogView.findViewById<TextInputEditText>(R.id.etCurrentPassword)
        val etNewPassword = dialogView.findViewById<TextInputEditText>(R.id.etNewPassword)
        val etConfirmPassword = dialogView.findViewById<TextInputEditText>(R.id.etConfirmPassword)
        val btnCancel = dialogView.findViewById<MaterialButton>(R.id.btnCancel)
        val btnChangePassword = dialogView.findViewById<MaterialButton>(R.id.btnChangePassword)
        val progressBar = dialogView.findViewById<ProgressBar>(R.id.progressBar)

        // Sembunyikan progress bar di awal
        progressBar.visibility = View.GONE

        // Tambahkan listener untuk validasi real-time
        etNewPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validatePassword(s.toString(), etNewPassword)
            }
        })

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnChangePassword.setOnClickListener {
            // Validasi input
            if (validateInputs(etCurrentPassword, etNewPassword, etConfirmPassword)) {
                // Nonaktifkan tombol dan tampilkan progress bar
                btnChangePassword.isEnabled = false
                progressBar.visibility = View.VISIBLE

                val currentPassword = etCurrentPassword.text.toString()
                val newPassword = etNewPassword.text.toString()

                // Proses ganti password
                changePasswordInFirebase(
                    currentPassword,
                    newPassword,
                    onSuccess = {
                        // Aktifkan kembali tombol dan sembunyikan progress bar
                        btnChangePassword.isEnabled = true
                        progressBar.visibility = View.GONE

                        // Tampilkan dialog sukses
                        showSuccessDialog("Password berhasil diubah")
                        dialog.dismiss()
                    },
                    onError = { errorMessage ->
                        // Aktifkan kembali tombol dan sembunyikan progress bar
                        btnChangePassword.isEnabled = true
                        progressBar.visibility = View.GONE

                        // Tampilkan pesan error
                        showErrorDialog(errorMessage)
                    }
                )
            }
        }

        dialog.show()
    }

    // Fungsi validasi password
    private fun validatePassword(password: String, etPassword: TextInputEditText) {
        when {
            password.length < 8 -> {
                etPassword.error = "Password minimal 8 karakter"
            }
            !password.matches(".*[A-Z].*".toRegex()) -> {
                etPassword.error = "Password harus mengandung huruf besar"
            }
            !password.matches(".*[a-z].*".toRegex()) -> {
                etPassword.error = "Password harus mengandung huruf kecil"
            }
            !password.matches(".*\\d.*".toRegex()) -> {
                etPassword.error = "Password harus mengandung angka"
            }
            else -> {
                etPassword.error = null
            }
        }
    }

    // Fungsi validasi input
    private fun validateInputs(
        etCurrentPassword: TextInputEditText,
        etNewPassword: TextInputEditText,
        etConfirmPassword: TextInputEditText
    ): Boolean {
        val currentPassword = etCurrentPassword.text.toString()
        val newPassword = etNewPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()

        var isValid = true

        // Validasi password saat ini
        if (currentPassword.isEmpty()) {
            etCurrentPassword.error = "Password saat ini tidak boleh kosong"
            isValid = false
        }

        // Validasi password baru
        if (newPassword.isEmpty()) {
            etNewPassword.error = "Password baru tidak boleh kosong"
            isValid = false
        } else {
            validatePassword(newPassword, etNewPassword)
            if (etNewPassword.error != null) {
                isValid = false
            }
        }

        // Validasi konfirmasi password
        if (confirmPassword.isEmpty()) {
            etConfirmPassword.error = "Konfirmasi password tidak boleh kosong"
            isValid = false
        } else if (newPassword != confirmPassword) {
            etConfirmPassword.error = "Konfirmasi password tidak cocok"
            isValid = false
        }

        return isValid
    }

    // Fungsi ganti password di Firebase
    private fun changePasswordInFirebase(
        currentPassword: String,
        newPassword: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val user = FirebaseAuth.getInstance().currentUser

        // Pastikan user tidak null
        if (user == null) {
            onError("Pengguna tidak terautentikasi")
            return
        }

        // Dapatkan email pengguna
        val email = user.email ?: run {
            onError("Email tidak ditemukan")
            return
        }

        // Proses re-autentikasi
        val credential = EmailAuthProvider.getCredential(email, currentPassword)
        user.reauthenticate(credential)
            .addOnCompleteListener { reAuthTask ->
                if (reAuthTask.isSuccessful) {
                    // Update password
                    user.updatePassword(newPassword)
                        .addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                onSuccess()
                            } else {
                                onError(updateTask.exception?.message
                                    ?: "Gagal mengubah password")
                            }
                        }
                } else {
                    onError(reAuthTask.exception?.message
                        ?: "Autentikasi gagal")
                }
            }
    }

    // Fungsi tambahan untuk menampilkan dialog sukses
    private fun showSuccessDialog(message: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Berhasil")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    // Fungsi tambahan untuk menampilkan dialog error
    private fun showErrorDialog(message: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun changePassword(newPassword: String) {
        // Pastikan user tidak null
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser == null) {
            // Jika user null, arahkan ke login
            Toast.makeText(this, "Sesi telah berakhir. Silakan login kembali.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Update password
        currentUser.updatePassword(newPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Password berhasil diubah
                    Toast.makeText(this, "Password berhasil diubah", Toast.LENGTH_SHORT).show()

                    // Opsional: Logout user setelah ganti password
                    FirebaseAuth.getInstance().signOut()

                    // Arahkan ke halaman login
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or
                            Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                } else {
                    // Gagal mengubah password
                    val errorMessage = task.exception?.message ?: "Gagal mengubah password"
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()

                    // Log error untuk debugging
                    Log.e("ChangePassword", "Password change failed", task.exception)
                }
            }
            .addOnFailureListener { exception ->
                // Tangani kegagalan dengan detail
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                Log.e("ChangePassword", "Password change error", exception)
            }
    }

    private fun showDeleteAccountDialog() {
        AlertDialog.Builder(this)
            .setTitle("Hapus Akun")
            .setMessage("Apakah Anda yakin ingin menghapus akun? Tindakan ini tidak dapat dibatalkan.")
            .setPositiveButton("Hapus") { _, _ ->
                showDeleteConfirmationDialog()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showDeleteConfirmationDialog() {
        // Inflate dialog layout
        val dialogView = layoutInflater.inflate(R.layout.dialog_delete_account, null)
        val passwordEt = dialogView.findViewById<TextInputEditText>(R.id.etPassword)

        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Hapus Akun")
            .setView(dialogView)
            .setPositiveButton("Hapus") { dialog, _ ->
                val password = passwordEt.text.toString().trim()

                if (password.isEmpty()) {
                    Toast.makeText(this, "Masukkan password", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                reauthenticateUser(password) {
                    deleteUserAccount()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun reauthenticateUser (password: String, onSuccess: () -> Unit) {
        val credential = EmailAuthProvider.getCredential(user.email!!, password)
        user.reauthenticate(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSuccess()
            } else {
                Toast.makeText(this, "Autentikasi ulang gagal", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Apakah Anda yakin ingin logout?")
            .setPositiveButton("Ya") { _, _ ->
                // Logout dari Firebase
                auth.signOut()

                // Hapus data lokal
                sharedPreferencesManager.clearUserData()

                // Arahkan ke layar login
                val intent = Intent(this, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or
                            Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Tidak", null)
            .show()
    }

    // Metode hapus akun
    private fun deleteUserAccount() {
        val userId = user.uid

        // Hapus data di Firestore
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .delete()
            .addOnCompleteListener { firestoreTask ->
                if (firestoreTask.isSuccessful) {
                    // Hapus akun Firebase
                    user.delete()
                        .addOnCompleteListener { authTask ->
                            if (authTask.isSuccessful) {
                                // Logout dan hapus data lokal
                                auth.signOut()
                                sharedPreferencesManager.clearUserData()

                                // Arahkan ke layar login
                                startActivity(Intent(this, LoginActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(this, "Gagal menghapus akun", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Gagal menghapus data pengguna", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Metode untuk mendapatkan nama pengguna
    private fun loadUserProfile() {
        // Ambil nama dari Firebase
        val displayName = user.displayName?.takeIf { it.isNotBlank() }

        // Jika nama dari Firebase null atau kosong, gunakan SharedPreferences
        val userName = displayName
            ?: sharedPreferencesManager.getUserName().takeIf { it.isNotBlank() }
            ?: "Pengguna"

        // Email dengan penanganan null
        val userEmail = user.email ?: "Email tidak tersedia"

        // Set teks dengan nilai yang aman
        findViewById<TextView>(R.id.tvUserName).text = userName
        findViewById<TextView>(R.id.tvUserEmail).text = userEmail
    }
}
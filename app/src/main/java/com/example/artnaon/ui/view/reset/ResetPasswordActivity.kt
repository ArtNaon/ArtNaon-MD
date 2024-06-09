package com.example.artnaon.ui.view.reset

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.example.artnaon.R
import com.example.artnaon.databinding.ActivityResetPasswordBinding
import com.example.artnaon.ui.ViewModelFactory

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResetPasswordBinding
    private val viewModel: ResetPasswordViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupAction()
        setupObserver()
    }

    private fun setupAction() {
        binding.btnResetPassword.setOnClickListener {
            val email = binding.edtResetPasswordEmail.text.toString()
            val newPassword = binding.edtResetPasswordPassword.text.toString()

            showLoading(true)

            if (email.isNotEmpty() && newPassword.isNotEmpty()) {
                viewModel.resetPassword(email, newPassword)
            } else {
                if (email.isEmpty()) {
                    binding.edlResetPasswordEmail.error = getString(R.string.error_message)
                    binding.edlResetPasswordEmail.errorIconDrawable = null
                } else {
                    binding.edlResetPasswordEmail.error = null
                }

                if (newPassword.isEmpty()) {
                    binding.edlResetPasswordPassword.error = getString(R.string.error_message)
                    binding.edlResetPasswordPassword.errorIconDrawable = null
                } else {
                    binding.edlResetPasswordPassword.error = null
                }
                showLoading(false)
            }
        }
    }

    private fun setupObserver() {
        viewModel.resetPasswordResponse.observe(this, Observer { response ->
            showLoading(false)
            if (response != null && response.status == "success") {
                showToast(response.message!!)
                finish()
            } else {
                errorMessage(response?.message ?: "Error resetting password")
            }
        })

        viewModel.errorResponse.observe(this, Observer { error ->
            showLoading(false)
            errorMessage(error ?: "Error resetting password")
        })
    }

    override fun onStop() {
        super.onStop()
        binding.edtResetPasswordEmail.text = null
        binding.edtResetPasswordPassword.text = null
    }

    private fun errorMessage(error: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Error")
            setMessage(error)
            setPositiveButton("OK", null)
            create()
            show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pgResetPassword.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
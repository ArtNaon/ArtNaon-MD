package com.example.artnaon.ui.view.signin

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.artnaon.R
import com.example.artnaon.data.pref.UserModel
import com.example.artnaon.databinding.ActivitySignInBinding
import com.example.artnaon.ui.ViewModelFactory
import com.example.artnaon.ui.view.main.MainActivity
import com.example.artnaon.ui.view.reset.ResetPasswordActivity
import com.example.artnaon.ui.view.signup.SignUpActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private val viewModel: SignInViewModel by viewModels<SignInViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupAction()
    }

    override fun onResume() {
        super.onResume()
        setupInitialViewStates()
        playAnimation()
    }

    override fun onStop() {
        super.onStop()
        binding.edtSignInEmail.text = null
        binding.edtSignInPassword.text = null
    }

    private fun setupInitialViewStates() {
        binding.tvSignInEmail.alpha = 0f
        binding.tvSignInEmail.translationY = 50f
        binding.edlSignInEmail.alpha = 0f
        binding.edlSignInEmail.translationY = 50f
        binding.tvSignInPassword.alpha = 0f
        binding.tvSignInPassword.translationY = 50f
        binding.edlSignInPassword.alpha = 0f
        binding.edlSignInPassword.translationY = 50f
        binding.tvSignInResetPassword.alpha = 0f
        binding.tvSignInResetPassword.translationY = 50f
        binding.btnSignIn.alpha = 0f
        binding.btnSignIn.translationY = 50f
        binding.layoutSignInSignUp.alpha = 0f
        binding.layoutSignInSignUp.translationY = 50f
    }

    private fun playAnimation() {
        val duration = 350L
        val delay = 50L

        val email = createAnimator(binding.tvSignInEmail, duration)
        val edlEmail = createAnimator(binding.edlSignInEmail, duration)
        val password = createAnimator(binding.tvSignInPassword, duration)
        val edlPassword = createAnimator(binding.edlSignInPassword, duration)
        val resetPassword = createAnimator(binding.tvSignInResetPassword, duration)
        val signin = createAnimator(binding.btnSignIn, duration)
        val signup = createAnimator(binding.layoutSignInSignUp, duration)

        AnimatorSet().apply {
            playSequentially(
                email.apply { startDelay = delay },
                edlEmail.apply { startDelay = delay },
                password.apply { startDelay = delay },
                edlPassword.apply { startDelay = delay },
                resetPassword.apply { startDelay = delay },
                signin.apply { startDelay = delay },
                signup.apply { startDelay = delay }
            )
            startDelay = 150
            start()
        }
    }

    private fun createAnimator(view: View, duration: Long): AnimatorSet {
        val alphaAnimator = ObjectAnimator.ofFloat(view, View.ALPHA, 1f).setDuration(duration)
        val translationYAnimator = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0f).setDuration(duration)
        return AnimatorSet().apply {
            playTogether(alphaAnimator, translationYAnimator)
        }
    }


    private fun setupAction() {
        binding.btnSignIn.setOnClickListener {
            val email = binding.edtSignInEmail.text.toString()
            val password = binding.edtSignInPassword.text.toString()

            showLoading(true)

            if (email.isNotEmpty() && password.isNotEmpty()) {
                lifecycleScope.launch {
                    try {
                        val result = viewModel.userSignIn(email, password)
                        if (result.isSuccess) {
                            val response = result.getOrNull()
                            showLoading(false)
                            if (response != null) {
                                val token = response.result?.token
                                viewModel.saveSession(UserModel(name = "", email = email, token = token ?: "", isLogin = true))
                                AlertDialog.Builder(this@SignInActivity).apply {
                                    setTitle("Asik!")
                                    setMessage("Selamat datang di ArtNaon")
                                    setPositiveButton("Lanjut") { _, _ ->
                                        intent =
                                            Intent(this@SignInActivity, MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    create()
                                    show()
                                }
                            }
                        } else {
                            val exception = result.exceptionOrNull()
                            exception?.message?.let {
                                showLoading(false)
                                AlertDialog.Builder(this@SignInActivity).apply {
                                    setTitle("Error")
                                    setMessage(it)
                                    setPositiveButton("OK", null)
                                    create()
                                    show()
                                }
                            }
                        }
                    } catch (e: Exception) {
                        showLoading(false)
                        AlertDialog.Builder(this@SignInActivity).apply {
                            setTitle("Error")
                            setMessage(getString(R.string.error_message))
                            setPositiveButton("OK", null)
                            create()
                            show()
                        }
                    }
                }
            } else {
                if (email.isEmpty()) {
                    binding.edlSignInEmail.error = getString(R.string.error_message)
                    binding.edlSignInEmail.errorIconDrawable = null
                } else {
                    binding.edlSignInEmail.error = null
                }

                if (password.isEmpty()) {
                    binding.edlSignInPassword.error = getString(R.string.error_message)
                    binding.edlSignInPassword.errorIconDrawable = null
                } else {
                    binding.edlSignInPassword.error = null
                }
            }
        }

        binding.tvSignInSignUp.setOnClickListener {
            startActivity(Intent(this@SignInActivity, SignUpActivity::class.java))
        }

        binding.tvSignInResetPassword.setOnClickListener {
            startActivity(Intent(this@SignInActivity, ResetPasswordActivity::class.java))
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pgSignIn.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}
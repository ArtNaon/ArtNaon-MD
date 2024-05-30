package com.example.artnaon.ui.view.signin

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.artnaon.R
import com.example.artnaon.databinding.ActivitySignInBinding
import com.example.artnaon.ui.view.main.MainActivity
import com.example.artnaon.ui.view.signup.SignUpActivity
import com.google.android.material.snackbar.Snackbar

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding

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
        binding.btnSignIn.alpha = 0f
        binding.btnSignIn.translationY = 50f
        binding.layoutSignInSignUp.alpha = 0f
        binding.layoutSignInSignUp.translationY = 50f
    }

    private fun playAnimation() {
        val duration = 550L

        val email = createAnimator(binding.tvSignInEmail, duration)
        val edlEmail = createAnimator(binding.edlSignInEmail, duration)
        val password = createAnimator(binding.tvSignInPassword, duration)
        val edlPassword = createAnimator(binding.edlSignInPassword, duration)
        val signin = createAnimator(binding.btnSignIn, duration)
        val signup = createAnimator(binding.layoutSignInSignUp, duration)

        AnimatorSet().apply {
            playSequentially(
                email,
                edlEmail,
                password,
                edlPassword,
                signin,
                signup
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

            if (email.isNotEmpty() && password.isNotEmpty()) {
                startActivity(Intent(this@SignInActivity, MainActivity::class.java))
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
    }
}
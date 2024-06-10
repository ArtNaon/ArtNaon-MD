package com.example.artnaon.ui.view.signup

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.artnaon.R
import com.example.artnaon.databinding.ActivitySignUpBinding
import com.example.artnaon.ui.ViewModelFactory
import com.example.artnaon.ui.view.main.MainActivity
import com.example.artnaon.ui.view.signin.SignInActivity
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private val viewModel: SignUpViewModel by viewModels<SignUpViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
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
        binding.edtSignUpUsername.text = null
        binding.edtSignUpEmail.text = null
        binding.edtSignUpPassword.text = null
    }

    private fun setupInitialViewStates() {
        binding.tvSignUpName.alpha = 0f
        binding.tvSignUpName.translationY = 50f
        binding.edlSignUpUsername.alpha = 0f
        binding.edlSignUpUsername.translationY = 50f
        binding.tvSignUpEmail.alpha = 0f
        binding.tvSignUpEmail.translationY = 50f
        binding.edlSignUpEmail.alpha = 0f
        binding.edlSignUpEmail.translationY = 50f
        binding.tvSignUpPassword.alpha = 0f
        binding.tvSignUpPassword.translationY = 50f
        binding.edlSignUpPassword.alpha = 0f
        binding.edlSignUpPassword.translationY = 50f
        binding.btnSignUp.alpha = 0f
        binding.btnSignUp.translationY = 50f
        binding.layoutSignUpSignIn.alpha = 0f
        binding.layoutSignUpSignIn.translationY = 50f
    }

    private fun playAnimation() {
        val duration = 550L

        val usernameAnim = createAnimator(binding.tvSignUpName, duration)
        val edlUsernameAnim = createAnimator(binding.edlSignUpUsername, duration)
        val emailAnim = createAnimator(binding.tvSignUpEmail, duration)
        val edlEmailAnim = createAnimator(binding.edlSignUpEmail, duration)
        val passwordAnim = createAnimator(binding.tvSignUpPassword, duration)
        val edlPasswordAnim = createAnimator(binding.edlSignUpPassword, duration)
        val signupAnim = createAnimator(binding.btnSignUp, duration)
        val signinAnim = createAnimator(binding.layoutSignUpSignIn, duration)

        AnimatorSet().apply {
            playSequentially(
                usernameAnim,
                edlUsernameAnim,
                emailAnim,
                edlEmailAnim,
                passwordAnim,
                edlPasswordAnim,
                signupAnim,
                signinAnim
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
        binding.btnSignUp.setOnClickListener {
            val username = binding.edtSignUpUsername.text.toString()
            val email = binding.edtSignUpEmail.text.toString()
            val password = binding.edtSignUpPassword.text.toString()

            showLoading(true)

            if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                lifecycleScope.launch {
                    try {
                        val message = viewModel.userSignUp(username, email, password).message
                        Log.d("Sign Up Success", "$message")
                        showLoading(false)
                        AlertDialog.Builder(this@SignUpActivity).apply {
                            setTitle("Asik")
                            setMessage("Akun $username sudah jadi nih. Yuk, mulai login.")
                            setPositiveButton("Lanjut") { _, _ ->
                                intent = Intent(this@SignUpActivity, SignInActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            create()
                            show()
                        }
                    } catch (e: Exception) {
                        val error = parseError(e.message)
                        Log.e("Sign Up Failed", "${e.message}")
                        showLoading(false)
                        AlertDialog.Builder(this@SignUpActivity).apply {
                            setTitle("Error")
                            setMessage(error)
                            setPositiveButton("OK", null)
                            create()
                            show()
                        }
                    }
                }
            } else {
                if (username.isEmpty()) {
                    binding.edlSignUpUsername.error = getString(R.string.error_message)
                    binding.edlSignUpUsername.errorIconDrawable = null
                } else {
                    binding.edlSignUpUsername.error = null
                }

                if (email.isEmpty()) {
                    binding.edlSignUpEmail.error = getString(R.string.error_message)
                    binding.edlSignUpEmail.errorIconDrawable = null
                } else {
                    binding.edlSignUpEmail.error = null
                }

                if (password.isEmpty()) {
                    binding.edlSignUpPassword.error = getString(R.string.error_message)
                    binding.edlSignUpPassword.errorIconDrawable = null
                } else {
                    binding.edlSignUpPassword.error = null
                }
            }
        }
        binding.tvSignUpSignIn.setOnClickListener {
            startActivity(Intent(this@SignUpActivity, SignInActivity::class.java))
        }
    }

    private fun parseError(message: String?): String {
        return try {
            val jsonObject = JSONObject(message.toString())
            jsonObject.getString("error")
        } catch (e: JSONException) {
            "Sepertinya email sudah digunakan nih."
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pgSignUp.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        alertDialog?.dismiss()
        lifecycleScope.cancel()
    }
}
package com.example.artnaon.ui.view.customize

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.artnaon.R
import com.google.android.material.textfield.TextInputLayout

class PasswordEditText : AppCompatEditText, View.OnTouchListener {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        setOnTouchListener(this)

        addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val parent = parent.parent as? TextInputLayout
                if (s.toString().length < 8) {
                    parent?.error = context.getString(R.string.error_password)
                    parent?.errorIconDrawable = null
                } else {
                    parent?.error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return false
    }
}


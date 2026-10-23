package com.example.bit230033_foodmanager.ui.base

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.example.bit230033_foodmanager.utils.LocaleHelper

abstract class BaseActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }
}

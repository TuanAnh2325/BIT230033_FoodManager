package com.example.bit230033_foodmanager.utils

import android.app.Activity
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.bit230033_foodmanager.R

object LanguageToggleHelper {

    fun setupToggle(
        activity: Activity,
        btnLangEn: TextView,
        btnLangVi: TextView,
        onLanguageChanged: (() -> Unit)? = null
    ) {
        val currentLang = LocaleHelper.getLanguage(activity)
        updateToggleUi(activity, btnLangEn, btnLangVi, currentLang)

        btnLangEn.setOnClickListener {
            if (LocaleHelper.getLanguage(activity) != Constants.LANG_ENGLISH) {
                LocaleHelper.setLocale(activity, Constants.LANG_ENGLISH)
                updateToggleUi(activity, btnLangEn, btnLangVi, Constants.LANG_ENGLISH)
                onLanguageChanged?.invoke() ?: activity.recreate()
            }
        }

        btnLangVi.setOnClickListener {
            if (LocaleHelper.getLanguage(activity) != Constants.LANG_VIETNAMESE) {
                LocaleHelper.setLocale(activity, Constants.LANG_VIETNAMESE)
                updateToggleUi(activity, btnLangEn, btnLangVi, Constants.LANG_VIETNAMESE)
                onLanguageChanged?.invoke() ?: activity.recreate()
            }
        }
    }

    fun updateToggleUi(
        activity: Activity,
        btnLangEn: TextView,
        btnLangVi: TextView,
        lang: String
    ) {
        val primaryColor = ContextCompat.getColor(activity, R.color.primary)
        val whiteColor = ContextCompat.getColor(activity, R.color.white)

        if (lang == Constants.LANG_VIETNAMESE) {
            btnLangVi.setBackgroundResource(R.drawable.bg_lang_active)
            btnLangVi.setTextColor(primaryColor)

            btnLangEn.background = null
            btnLangEn.setTextColor(whiteColor)
        } else {
            btnLangEn.setBackgroundResource(R.drawable.bg_lang_active)
            btnLangEn.setTextColor(primaryColor)

            btnLangVi.background = null
            btnLangVi.setTextColor(whiteColor)
        }
    }
}

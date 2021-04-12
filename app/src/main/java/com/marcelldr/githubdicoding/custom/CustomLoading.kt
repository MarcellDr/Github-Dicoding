package com.marcelldr.githubdicoding.custom

import android.app.Dialog
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.marcelldr.githubdicoding.R

class CustomLoading(activity: AppCompatActivity): Dialog(activity) {
    init {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.setCancelable(false)
        this.setContentView(R.layout.custom_loading)
    }
}
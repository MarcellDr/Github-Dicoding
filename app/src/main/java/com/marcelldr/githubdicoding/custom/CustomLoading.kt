package com.marcelldr.githubdicoding.custom

import android.app.Dialog
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.marcelldr.githubdicoding.R

class CustomLoading(activity: AppCompatActivity) {
    private var dialog: Dialog = Dialog(activity)

    init {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_loading)
    }

    fun show() {
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }
}
package com.repository.skeleton.extensions

import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.repository.skeleton.BuildConfig


/**
 *  Helper method to log debug events to logcat
 */
fun Fragment.log(msg: String) {
    if (BuildConfig.DEBUG) {
        Log.d(this.javaClass.simpleName, msg)
    }
}

fun Fragment.toast(msg: String) {
    if (BuildConfig.DEBUG) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }
}


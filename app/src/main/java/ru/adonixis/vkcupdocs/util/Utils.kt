package ru.adonixis.vkcupdocs.util

import android.content.Context
import android.content.res.Resources
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import com.google.android.material.snackbar.Snackbar
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.math.log10
import kotlin.math.pow

object Utils {
    @JvmStatic
    fun convertDpToPx(dp: Float): Float {
        return dp * Resources.getSystem().displayMetrics.density
    }

    fun convertPxToDp(px: Float): Float {
        return px / Resources.getSystem().displayMetrics.density
    }

    fun showSnackbar(view: View,
                     callback: Snackbar.Callback?,
                     @ColorInt backgroundColor: Int,
                     @ColorInt textColor: Int,
                     text: String,
                     @ColorInt actionTextColor: Int,
                     actionText: String,
                     onViewClickListener: View.OnClickListener?) {
        var onClickListener = onViewClickListener
        if (onClickListener == null) {
            onClickListener = View.OnClickListener { }
        }
        val snackbar = Snackbar
            .make(view, text, Snackbar.LENGTH_LONG)
            .addCallback(callback!!)
            .setActionTextColor(actionTextColor)
            .setAction(actionText, onClickListener)
        val sbView = snackbar.view
        sbView.setBackgroundColor(backgroundColor)
        val textView = sbView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.setTextColor(textColor)
        snackbar.show()
    }

    fun getPath(context: Context, uri: Uri): String {
        if (uri.scheme == "file") {
            if (uri.path != null) return uri.path!!
            return ""
        }
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, proj, null, null, null)
        val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return "file://" + cursor.getString(columnIndex)
    }

    fun formatFileSize(size: Long): String? {
        if (size <= 0) return "0 Б"
        val units = arrayOf("Б", "КБ", "МБ", "ГБ", "ТБ")
        val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()
        val symbols = DecimalFormatSymbols(Locale.US)
        return DecimalFormat("#,##0.#", symbols)
            .format(size / 1024.0.pow(digitGroups.toDouble()))
            .toString() + " " + units[digitGroups]
    }
}
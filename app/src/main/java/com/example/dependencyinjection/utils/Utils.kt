package com.example.dependencyinjection.utils

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.view.View
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow


object Utils {
    fun <T> LiveData<T>.observeOnce(observer: (T) -> Unit) {
        observeForever(object : Observer<T> {
            override fun onChanged(value: T) {
                removeObserver(this)
                observer(value)
            }
        })
    }

    fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, observer: (T) -> Unit) {
        observe(owner, object : Observer<T> {
            override fun onChanged(value: T) {
                removeObserver(this)
                observer(value)
            }
        })
    }

    fun <T> LiveData<T>.observeUntilNonNull(owner: LifecycleOwner, observer: (T) -> Unit) {
        observe(owner, object : Observer<T> {
            override fun onChanged(value: T) {
                value?.let {
                    removeObserver(this)
                    observer(it)
                }
            }
        })
    }

    inline fun <reified T> Gson.fromJson(json: String): T = fromJson(json, object : TypeToken<T>() {}.type)

    inline fun <reified T : Enum<T>> String.toEnumOrDefault(defaultValue: T? = null): T? =
        enumValues<T>().firstOrNull { it.name.equals(this, ignoreCase = true) } ?: defaultValue

    fun isInternet(context: Context): Boolean {
        val mConMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return (mConMgr.activeNetworkInfo != null && mConMgr.activeNetworkInfo!!.isAvailable
                && mConMgr.activeNetworkInfo!!.isConnected)
    }

    fun getTime(hr: Int, min: Int): String? {
        val cal: Calendar = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, hr)
        cal.set(Calendar.MINUTE, min)
        val formatter = SimpleDateFormat("h:mm a", Locale.ROOT)
        return formatter.format(cal.time)
    }

    private fun String.removeTone() : String {
        var str = this
        str = str.replace("??|??|???|???|??|??|???|???|???|???|???|??|???|???|???|???|???".toRegex(), "a");
        str = str.replace("??|??|???|???|???|??|???|???|???|???|???".toRegex(), "e");
        str = str.replace("??|??|???|???|??".toRegex(), "i");
        str = str.replace("??|??|???|???|??|??|???|???|???|???|???|??|???|???|???|???|???".toRegex(), "o");
        str = str.replace("??|??|???|???|??|??|???|???|???|???|???".toRegex(), "u");
        str = str.replace("???|??|???|???|???".toRegex(), "y");
        str = str.replace("??", "d");

        str = str.replace("??|??|???|???|??|??|???|???|???|???|???|??|???|???|???|???|???".toRegex(), "A");
        str = str.replace("??|??|???|???|???|??|???|???|???|???|???".toRegex(), "E");
        str = str.replace("??|??|???|???|??".toRegex(), "I");
        str = str.replace("??|??|???|???|??|??|???|???|???|???|???|??|???|???|???|???|???".toRegex(), "O");
        str = str.replace("??|??|???|???|??|??|???|???|???|???|???".toRegex(), "U");
        str = str.replace("???|??|???|???|???".toRegex(), "Y");
        str = str.replace("??", "D");
        return str
    }

    fun currentTimestamp(): Long {
        return System.currentTimeMillis()/1000
    }

    fun currentTime(timeFormat: String = "yyyy/MM/dd HH:mm"): String {
        return SimpleDateFormat(timeFormat, Locale.getDefault()).format(Calendar.getInstance().time)
    }

    fun String.createUniqueName(): String {
        return this.removeTone().replace(" ","_") +"_"+ currentTimestamp()
    }

    fun Int.toExcelFormat(): String {
        var columnString = ""
        var columnNumber: Int = this
        while (columnNumber > 0) {
            val currentLetterNumber: Int = (columnNumber - 1) % 26
            val currentLetter = (currentLetterNumber + 65).toChar()
            columnString = currentLetter + columnString
            columnNumber = (columnNumber - (currentLetterNumber + 1)) / 26
        }
        return columnString
    }

    fun String.excelFormatToNumber(): Int {
        var retVal = 0
        val col: String = this.uppercase()
        for (iChar in (col.length - 1) downTo 0) {
            val colPiece= col[iChar]
            val colNum = colPiece.code - 64
            retVal += colNum * 26.0.pow((col.length - (iChar + 1)).toDouble()).toInt()
        }
        return retVal
    }

    fun View.showKeyboard() = (this.context as? Activity)?.showKeyboard()
    fun View.hideKeyboard() = (this.context as? Activity)?.hideKeyboard()

    fun Fragment.showKeyboard() = activity?.showKeyboard()
    fun Fragment.hideKeyboard() = activity?.hideKeyboard()

    fun Context.showKeyboard() = (this as? Activity)?.showKeyboard()
    fun Context.hideKeyboard() = (this as? Activity)?.hideKeyboard()

    fun Activity.showKeyboard() = WindowInsetsControllerCompat(window, window.decorView).show(WindowInsetsCompat.Type.ime())
    fun Activity.hideKeyboard() = WindowInsetsControllerCompat(window, window.decorView).hide(WindowInsetsCompat.Type.ime())
}

enum class Status {
    SUCCESS, ERROR, LOADING, NOTHING
}

data class StatusControl<out T>(val status: Status, val data: T? = null, val message: String? = null) {
    companion object {
        fun <T> success(data: T): StatusControl<T> = StatusControl(Status.SUCCESS, data)
        fun <T> error(data: T? = null, message: String) = StatusControl(Status.ERROR, data, message)
        fun <T> loading(data: T? = null) = StatusControl(Status.LOADING, data)
        fun <T> nothing(data: T? = null) = StatusControl(Status.NOTHING, data)
    }
}
package com.example.blog_e.utils

import android.content.res.Resources
import android.graphics.Color
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.example.blog_e.Config
import com.google.android.material.textfield.TextInputEditText
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class Utils {
    companion object {
        fun formatBackstack(navController: NavController): String {
            return "current Backstack:\n " + navController.backQueue.joinToString("\n") {
                " ${it.destination.navigatorName}: ${it.destination.displayName}(${it.destination.label})"
            }
        }

        fun currentDate(): LocalDateTime {
            return LocalDateTime.now(ZoneId.of(Config.timeZone))
        }

        fun currentDateFormatted(): String {
            val current = currentDate()
            val formatter = DateTimeFormatter.ofPattern(Config.dateFormat)

            return current.format(formatter)

        }

        fun getDrawableID(
            resourceName: String,
            activity: FragmentActivity?,
            resources: Resources
        ): Int {
            val uri = "@drawable/${resourceName}"
            return resources.getIdentifier(uri, null, activity?.packageName)
        }
    }

}

fun calculatePastTime(date: String): String {
    val current = Utils.currentDate()
    var formatter = DateTimeFormatter.ofPattern(Config.dateFormat)
    val creationDate: LocalDateTime = LocalDateTime.parse(date, formatter)
    val duration = Duration.between(creationDate, current)

    if (duration.toDays() > 9) {
        formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        return creationDate.format(formatter)
    }
    if (duration.toDays() > 0) {
        return "${duration.toDays()}d"
    }
    if (duration.toHours() > 0) {
        return "${duration.toHours()}h"
    }
    if (duration.toMinutes() > 0) {
        return "${duration.toMinutes()}m"
    }

    if (duration.seconds > 0) {
        return "${duration.seconds}s"
    }

    return "just posted"
}

/**
 * Checks whether the given username is valid or not. If not, a descriptive error message is returned.
 */
fun validateUsername(name: String): String {
    if (name.length < 3) {
        return "username too short"
    }

    if (name.length > 15) {
        return "username too long"
    }

    val usernameRegex = """^[A-Za-z0-9]*$""".toRegex()
    if (!usernameRegex.matches(name)) {
        return "only letters and numbers allowed in username"
    }

    return ""
}

/**
 * Checks whether the given password is valid or not. If not, a descriptive error message is returned.
 */
fun validatePassword(password: String): String {
    if (password.length < 6) {
        return "password too short"
    }

    return ""
}


/**
 * Populates a textedit views with a affix preserving its prefix, with hickups
 */

fun displayGeneratedContentSpanedBkg(view: TextInputEditText, content: String, range: LongRange) {
    val prefix = view.text.toString()
    val completed = prefix + " " + content
    val span = SpannableString(completed)

    span.setSpan(
        ForegroundColorSpan(Color.LTGRAY),
        prefix.length, completed.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    view.setText(span)
    var i = prefix.length
    var handler = Handler()
    var runnable = object : Runnable {
        override fun run() {
            span.setSpan(
                ForegroundColorSpan(Color.BLACK),
                0, i,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            view.setText(span)
            if (i++ < completed.length) {
                handler.postDelayed(this, range.random())
            }
        }
    }
    handler.postDelayed(runnable, 0)
    view.setSelection(view.length())
}


class Garbler(view: TextInputEditText, range: LongRange) {

    val handler: Handler
    var runnable: Runnable
    var canceled: Boolean = false
    var originalContent: String
    var view: TextInputEditText
    val preserve = " "
    val range: LongRange

    init {
        this.view = view
        this.range = range

        originalContent = view.text.toString()
        if (originalContent.isBlank()) originalContent = getRandomString(10, "", emptySet())

        val indices = Regex(preserve).findAll(originalContent).map { it.range.first }.toSet()
        val completeLen = originalContent.length
        handler = Handler()
        runnable = object : Runnable {
            override fun run() {
                val str = getRandomString(originalContent.length, preserve, indices)
                val span = SpannableString(str)

                span.setSpan(
                    ForegroundColorSpan(Color.BLACK),
                    0, completeLen,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                view.setText(span)
                if (!canceled) handler.postDelayed(this, range.random())
                else revert()
            }
        }
    }

    fun garble() {
        this.handler.postDelayed(runnable, 0)
    }

    fun revert(){
        view.setText(originalContent)
    }

    fun rebuildString(content: String) {
        cancel()
        val indices = Regex(preserve).findAll(content).map { it.range.first }.toSet()
        val completeLen = content.length

        var i = 0
        runnable = object : Runnable {
            override fun run() {
                val str = "${content.subSequence(0, i)} ${
                    getRandomString(
                        content.length - i,
                        preserve,
                        indices
                    )
                }"
                val span = SpannableString(str)

                span.setSpan(
                    ForegroundColorSpan(Color.BLACK),
                    0, completeLen,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                view.setText(span)
                if (i++ < completeLen) {
                    handler.postDelayed(this, range.random())
                }
            }
        }
        handler.postDelayed(runnable, 0)
    }

    fun rebuildStringWithPrefix(newContent: String) {
        rebuildString("$originalContent $newContent")
    }

    fun cancel() {
        if (!canceled) {
            canceled = true
        }
    }
}


private fun getRandomString(sizeOfRandomString: Int, c: String, indices: Set<Int>): String {
    val ALLOWED_CHARACTERS = "qwertyuiopasdfghjklzxcvbnm"
    val random = Random()
    val sb = StringBuilder(sizeOfRandomString)
    for (i in 0 until sizeOfRandomString)
        if (indices.contains(i)) sb.append(c)
        else sb.append(ALLOWED_CHARACTERS[random.nextInt(ALLOWED_CHARACTERS.length)])
    return sb.toString()
}

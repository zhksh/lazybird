package com.example.blog_e.utils

import android.content.res.Resources
import android.graphics.Color
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.example.blog_e.Config
import com.google.android.material.textfield.TextInputEditText
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.logging.Logger

class Utils {
    companion object {
        fun formatBackstack(navController: NavController): String{
            return "current Backstack:\n " + navController.backQueue.joinToString("\n") {
                " ${it.destination.navigatorName}: ${it.destination.displayName}(${it.destination.label})"
            }
        }

        fun currentDate(): LocalDateTime{
            return LocalDateTime.now(ZoneId.of(Config.timeZone))
        }

        fun currentDateFormatted(): String{
            val current = currentDate()
            val formatter = DateTimeFormatter.ofPattern(Config.dateFormat)

            return current.format(formatter)

        }

        fun getDrawableID(resourceName: String, activity: FragmentActivity?, resources:Resources): Int {
            val uri = "@drawable/${resourceName}"
            return  resources.getIdentifier(uri, null, activity?.packageName)
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
fun displayGeneratedContent(view: TextInputEditText, content: String, range: LongRange){
    if (content.length < 1) return
    val prefix = view.text.toString()
    val preserve = " "
    val indices = Regex(preserve).findAll(content).map { it.range.first }.toSet()
    val completed = prefix + " " + getRandomString(content.length-1, preserve, indices)

    var i = 0
    var handler = Handler()
    var runnable = object : Runnable {
        override fun run() {
            val str = prefix + " " + content.subSequence(0, i) + getRandomString(content.length-i, preserve, indices)
            val span = SpannableString(str)

            span.setSpan(
                ForegroundColorSpan(Color.BLACK),
                0, completed.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            view.setText(span)
            if (prefix.length + i++ < completed.length){
                handler.postDelayed(this, range.random())
            }
        }
    }
    handler.postDelayed(runnable, 0)
    view.setSelection(view.length())
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



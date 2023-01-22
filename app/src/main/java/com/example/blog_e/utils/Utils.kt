package com.example.blog_e.utils

import android.content.res.Resources
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.example.blog_e.Config
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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
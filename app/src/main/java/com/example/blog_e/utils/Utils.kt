package com.example.blog_e.utils

import androidx.navigation.NavController
import com.example.blog_e.Config
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

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
    }

}

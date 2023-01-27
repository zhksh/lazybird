package com.example.blog_e

class Config {
    companion object {
        // Use these settings for production with the live api
//        private const val httpPrefix = "https://"
//        const val socketPrefix = "wss://"
//        private const val domain = "mvsp-api.ncmg.eu"

         // Use these settings for testing with a local api
          private const val httpPrefix = "http://"
          const val socketPrefix = "ws://"
          private const val domain = "10.0.2.2:6969"

        const val apiAddress = httpPrefix + domain
        const val socketAddress = socketPrefix + domain

        const val clientTimout = 10 //s
        const val tagPref = "mvsp"

        // the rest of the world is using ISO-8601 and so are we
        const val dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

        const val timeZone = "UTC"
        // const val timeZone = "Europe/Berlin"

        val  generatePostDelay = LongRange(10,100) //ms

        const val defaultMood = "ironic"
        const val defaultTemperature: Float = 0.8f
        const val defaultHistoryLength = 5


        const val defaultPageSize = 10
        const val enablePlaceHolders = false
        const val prefetchDistance = 1

        fun tag(str: String): String {return tagPref + "_" + str }
    }

}

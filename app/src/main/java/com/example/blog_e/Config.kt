package com.example.blog_e

class Config {
    companion object {
        // Use the commented settings for production
        // private const val httpPrefix = "https://"
        // const val socketPrefix = "wss://"
        // private const val domain = "mvsp-api.ncmg.eu"

        private const val httpPrefix = "http://"
        const val socketPrefix = "ws://"
        private const val domain = "10.0.2.2:6969"

        const val apiAddress = httpPrefix + domain
        const val socketAddress = socketPrefix + domain

//        const val apiAddress = "https://mvsp-api.ncmg.eu"
//        const val apiAddress = "http://10.0.2.2:6969"

        const val clientTimout = 60 //s
        const val tagPref = "mvsp"

//        const val dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSX"
        //the re4st of the world is using ISO-8601 and so are we
        const val dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

        const val timeZone = "UTC"
//        const val timeZone = "Europe/Berlin"


        const val defaultMood = "neutral"



        fun tag(str: String): String {return tagPref + "_" + str }
    }

}
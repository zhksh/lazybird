package com.example.blog_e

class Config {
    companion object {
//        const val apiAddress = "https://mvsp-api.ncmg.eu"
        const val apiAddress = "http://10.0.2.2:6969"
        const val clientTimout = 60 //s
        const val tagPref = "mvsp"

        const val dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSX"
        const val timeZone = "Europe/Berlin"
        const val dateFormat_better = "yyyy-MM-dd'T'HH:mm:ss'Z'+1"


        const val defaultMood = "neutral"



        fun tag(str: String): String {return tagPref + "_" + str }
    }

}
package com.example.blog_e

class Config {
    companion object {
        const val apiAddress = "https://mvsp-api.ncmg.eu"
//        const val apiAddress = "http://10.0.2.2:6969"
        const val clientTimout = 60 //s
        const val tagPref = "mvsp"

        fun tag(str: String): String {return tagPref + "_" + str }
    }

}
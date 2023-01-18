package com.example.blog_e.data.model

import com.example.blog_e.R
import java.util.*


data class User(
    val id: UUID = UUID.randomUUID(),
    val username: String,
    // TODO anders mit umgehen
    val password: String,
    // Resource
    val profilePicture: ProfilePicture
)

enum class ProfilePicture(val res: Int) {
    PICTURE_00(R.drawable.baby_yoda_0),
    PICTURE_01(R.drawable.baby_yoda_1),
    PICTURE_02(R.drawable.baby_yoda_2),
    PICTURE_03(R.drawable.baby_yoda_3),
    PICTURE_04(R.drawable.among_us_0),
    PICTURE_05(R.drawable.astronaut_horse_0),
}

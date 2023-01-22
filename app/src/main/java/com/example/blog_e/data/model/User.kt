package com.example.blog_e.data.model

import com.example.blog_e.R


data class User(
    val username: String,
    val displayName: String,
    val profilePicture: ProfilePicture,
    val followers: Int,
)

enum class ProfilePicture(val res: Int) {
    PICTURE_00(R.drawable.baby_yoda_0),
    PICTURE_01(R.drawable.baby_yoda_1),
    PICTURE_02(R.drawable.baby_yoda_2),
    PICTURE_03(R.drawable.baby_yoda_3),
    PICTURE_04(R.drawable.among_us_0),
    PICTURE_05(R.drawable.astronaut_horse_0),
}

fun mapApiUser(apiUser: GetUserAPIModel): User {
    var displayName = apiUser.displayName
    if (displayName == null) {
        displayName = apiUser.username
    }

    return User(
        username = apiUser.username,
        displayName = displayName,
        profilePicture = iconIdToProfilePicture(apiUser.iconId),
        followers = apiUser.followers.count(),
    )
}

fun iconIdToProfilePicture(iconId: String): ProfilePicture {
    return try {
        ProfilePicture.valueOf(iconId)
    }
    catch (e: IllegalArgumentException){
        ProfilePicture.PICTURE_05
    }
}
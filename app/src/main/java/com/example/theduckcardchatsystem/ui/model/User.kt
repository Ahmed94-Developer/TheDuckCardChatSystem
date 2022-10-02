package com.example.theduckcardchatsystem.ui.model

class User {
    var fullname = "Full Name"
    var phone: String? = null
    var uid: String? = null
    var username = "UserName"
    var bio = "bio"
    var photourl: String? = null
    var state: String? = null

    constructor() {}
    constructor(
        fullname: String,
        phone: String?,
        uid: String?,
        username: String,
        bio: String,
        photourl: String?,
        state: String?
    ) {
        this.fullname = fullname
        this.phone = phone
        this.uid = uid
        this.username = username
        this.bio = bio
        this.photourl = photourl
        this.state = state
    }
}
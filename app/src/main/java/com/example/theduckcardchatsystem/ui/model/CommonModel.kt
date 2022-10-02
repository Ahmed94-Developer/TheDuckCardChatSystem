package com.example.theduckcardchatsystem.ui.model

class CommonModel {
    var fullname: String? = null
    var phone: String? = null
    var id: String? = null
    var uid: String? = null
    var username: String? = null
    var bio: String? = null
    var photourl: String? = null
    var state: String? = null
    var text: String? = null
    var type: String? = null
    var from: String? = null
    var timeStamp: Any? = null
    var imageUrl: String? = null
    var lastMessage: String? = null
    var choice = false
    var senderImg: String? = null
        private set

    constructor() {}
    constructor(
        fullname: String?,
        phone: String?,
        id: String?,
        username: String?,
        bio: String?,
        photourl: String?,
        state: String?,
        uid: String?,
        choice: Boolean,
        senderImg: String?
    ) {
        this.fullname = fullname
        this.phone = phone
        this.id = id
        this.username = username
        this.bio = bio
        this.photourl = photourl
        this.state = state
        this.uid = uid
        this.choice = choice
        this.senderImg = senderImg
    }
}
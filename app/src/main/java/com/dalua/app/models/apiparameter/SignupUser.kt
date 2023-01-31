package com.dalua.app.models.apiparameter

class SignupUser(
    var first_name: String,
    var middle_name: String? = null,
    var last_name: String? = null,
    var username: String? = null,
    var email: String,
    var phone_no: String? = null,
    var country_code: String? = null,
    var password: String? = null,
    var social_token: String? = null,
    var social_user_id: String? = null,
    var login_type: String,
    var tank_size: String? = null,
    var country: String? = null
) {

}
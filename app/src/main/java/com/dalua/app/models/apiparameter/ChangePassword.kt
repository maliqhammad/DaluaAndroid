package com.dalua.app.models.apiparameter

data class ChangePassword(val email:String,val verification_code: String,val password:String,val password_confirmation:String) {
}
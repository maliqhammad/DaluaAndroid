package com.dalua.app.api

sealed class Resource<T>(val data: T? = null, val error: String? = null, val api_Type: String? = null) {

    class Success<T>(data: T?,api_Type: String?=null) : Resource<T>(data,api_Type=api_Type)
    class Error<T>(t: String?, data: T? = null,api_Type: String?=null) : Resource<T>(data, t,api_Type)
    class Loading<T> : Resource<T>()

}
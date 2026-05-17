package com.example.news.domain

sealed interface Resource<out T> {

    data class Success<T>(
        val data: T
    ) : Resource<T>

    data class Error<T>(
        val message: String,
        val throwable: Throwable? = null
    ) : Resource<T>
}
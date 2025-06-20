package com.defri.bookreflect.core

// state result class for repos returns
sealed class Result<out R> {
    object Idle : Result<Nothing>()
    object Loading : Result<Nothing>()
    data class Success<out R>(val data: R? = null) : Result<R>()
    data class Error(val exception: Throwable) : Result<Nothing>()
} 
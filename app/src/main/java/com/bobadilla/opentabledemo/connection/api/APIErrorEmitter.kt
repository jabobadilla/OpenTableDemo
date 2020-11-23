package com.bobadilla.opentabledemo.connection.api

interface APIErrorEmitter {
    fun onError(msg: String)
    fun onError(errorType: ErrorType)
}
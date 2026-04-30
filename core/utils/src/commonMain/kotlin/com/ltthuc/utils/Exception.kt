package com.ltthuc.utils

sealed class AppException : Exception() {
    object WifiException : AppException()
    object CellularException : AppException()
    object GeneralException : AppException()
    object NotModifiedException : AppException()
    object BadRequest : AppException()
    object IncorrectData : AppException()
}

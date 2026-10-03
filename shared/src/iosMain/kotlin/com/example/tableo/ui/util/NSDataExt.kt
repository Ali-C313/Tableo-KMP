package com.example.tableo.ui.util

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.create
import platform.posix.memcpy

/** NSData (iOS) in ein Kotlin-ByteArray kopieren. */
@OptIn(ExperimentalForeignApi::class)
fun NSData.toByteArray(): ByteArray {
    val result = ByteArray(length.toInt())
    if (result.isNotEmpty()) {
        result.usePinned { pinned -> memcpy(pinned.addressOf(0), bytes, length) }
    }
    return result
}

/** Kotlin-ByteArray in NSData für iOS-APIs. */
@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
fun ByteArray.toNSData(): NSData =
    if (isEmpty()) NSData()
    else usePinned { pinned -> NSData.create(bytes = pinned.addressOf(0), length = size.toULong()) }

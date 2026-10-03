package com.example.tableo.data.repository.impl

import com.example.tableo.ui.util.toNSData
import dev.gitlive.firebase.storage.Data

actual fun ByteArray.toStorageData(): Data = Data(toNSData())

package com.example.tableo.data.repository.impl

import dev.gitlive.firebase.storage.Data

/** Bytes in das Format, das Firebase Storage auf der jeweiligen Plattform braucht. */
expect fun ByteArray.toStorageData(): Data

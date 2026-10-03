package com.example.tableo.data.repository.mock

import com.example.tableo.data.repository.NotificationRepository

/** Für Previews, zeigt nichts an. */
class NotificationRepositoryMock : NotificationRepository {
    override fun show(id: Int, title: String, text: String) = Unit
}

package com.example.tableo.data.repository.impl

import com.example.tableo.data.repository.NotificationRepository
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotification
import platform.UserNotifications.UNNotificationPresentationOptionBanner
import platform.UserNotifications.UNNotificationPresentationOptionList
import platform.UserNotifications.UNNotificationPresentationOptionSound
import platform.UserNotifications.UNNotificationPresentationOptions
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.UNUserNotificationCenterDelegateProtocol
import platform.darwin.NSObject

/** Lokale Benachrichtigungen über UNUserNotificationCenter. */
class NotificationRepositoryImpl : NotificationRepository {

    // ohne Delegate zeigt iOS Benachrichtigungen nicht an, solange die App offen ist
    private val foregroundDelegate = ForegroundNotificationDelegate()

    init {
        UNUserNotificationCenter.currentNotificationCenter().delegate = foregroundDelegate
    }

    override fun show(id: Int, title: String, text: String) {
        val content = UNMutableNotificationContent()
        content.setTitle(title)
        content.setBody(text)

        // gleiche ID ersetzt die alte Benachrichtigung
        val request = UNNotificationRequest.requestWithIdentifier(id.toString(), content, null)
        UNUserNotificationCenter.currentNotificationCenter().addNotificationRequest(request, null)
    }
}

private class ForegroundNotificationDelegate : NSObject(), UNUserNotificationCenterDelegateProtocol {
    override fun userNotificationCenter(
        center: UNUserNotificationCenter,
        willPresentNotification: UNNotification,
        withCompletionHandler: (UNNotificationPresentationOptions) -> Unit
    ) {
        withCompletionHandler(
            UNNotificationPresentationOptionBanner or UNNotificationPresentationOptionList or UNNotificationPresentationOptionSound
        )
    }
}

package com.example.tableo.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalInspectionMode
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIViewController
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

@Composable
actual fun rememberImagePicker(onImagePicked: (ByteArray?) -> Unit): () -> Unit {
    if (LocalInspectionMode.current) return {}

    val currentOnPicked by rememberUpdatedState(onImagePicked)
    val delegate = remember { ImagePickerDelegate { bytes -> currentOnPicked(bytes) } }

    return remember(delegate) {
        {
            val configuration = PHPickerConfiguration()
            configuration.filter = PHPickerFilter.imagesFilter
            configuration.selectionLimit = 1

            val picker = PHPickerViewController(configuration = configuration)
            picker.delegate = delegate
            topViewController()?.presentViewController(picker, animated = true, completion = null)
        }
    }
}

/** Nimmt das gewählte Foto entgegen und wandelt es in JPEG um (iPhone-Fotos sind oft HEIC). */
private class ImagePickerDelegate(
    private val onPicked: (ByteArray?) -> Unit
) : NSObject(), PHPickerViewControllerDelegateProtocol {

    override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
        picker.dismissViewControllerAnimated(true, completion = null)

        val result = didFinishPicking.firstOrNull() as? PHPickerResult
        if (result == null) {
            onPicked(null)
            return
        }

        result.itemProvider.loadDataRepresentationForTypeIdentifier("public.image") { data, _ ->
            val jpeg = data?.let { UIImage(data = it) }?.let { UIImageJPEGRepresentation(it, 0.8) }
            val bytes = jpeg?.toByteArray()
            // zurück auf den Main-Thread, weil danach Compose-State geändert wird
            dispatch_async(dispatch_get_main_queue()) { onPicked(bytes) }
        }
    }
}

/** Oberster sichtbarer ViewController, über dem die Fotoauswahl aufgeht. */
private fun topViewController(): UIViewController? {
    @Suppress("DEPRECATION")
    var controller = UIApplication.sharedApplication.keyWindow?.rootViewController
    while (controller?.presentedViewController != null) {
        controller = controller.presentedViewController
    }
    return controller
}

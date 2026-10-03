package com.example.tableo.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import com.example.tableo.data.model.Coordinates
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.MapKit.MKAnnotationProtocol
import platform.MapKit.MKAnnotationView
import platform.MapKit.MKMapView
import platform.MapKit.MKMapViewDelegateProtocol
import platform.MapKit.MKMarkerAnnotationView
import platform.MapKit.MKPointAnnotation
import platform.UIKit.UIColor
import platform.UIKit.systemBlueColor
import platform.darwin.NSObject

/** Apple-Karte (MapKit) mit Fahrer (orange) und Lieferadresse (blau). */
@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun TrackingMap(
    driver: Coordinates,
    destination: Coordinates?,
    modifier: Modifier
) {
    val driverPin = remember { MKPointAnnotation() }
    val destinationPin = remember { MKPointAnnotation() }
    // MapKit hält den Delegate nur schwach, deshalb hier merken
    val mapDelegate = remember { PinColorDelegate(driverPin) }

    UIKitView(
        factory = {
            MKMapView().apply { delegate = mapDelegate }
        },
        modifier = modifier,
        update = { mapView ->
            driverPin.setCoordinate(CLLocationCoordinate2DMake(driver.latitude, driver.longitude))
            if (!mapView.annotations.contains(driverPin)) mapView.addAnnotation(driverPin)

            if (destination != null) {
                destinationPin.setCoordinate(CLLocationCoordinate2DMake(destination.latitude, destination.longitude))
                if (!mapView.annotations.contains(destinationPin)) mapView.addAnnotation(destinationPin)
            }

            // Ausschnitt so wählen, dass Fahrer und Adresse zu sehen sind
            mapView.showAnnotations(mapView.annotations, animated = true)
        }
    )
}

/** Färbt die Pins: Fahrer orange, Lieferadresse blau. */
private class PinColorDelegate(
    private val driverPin: MKPointAnnotation
) : NSObject(), MKMapViewDelegateProtocol {

    override fun mapView(mapView: MKMapView, viewForAnnotation: MKAnnotationProtocol): MKAnnotationView? {
        val view = MKMarkerAnnotationView(annotation = viewForAnnotation, reuseIdentifier = null)
        view.markerTintColor = if (viewForAnnotation === driverPin) UIColor.orangeColor else UIColor.systemBlueColor
        return view
    }
}

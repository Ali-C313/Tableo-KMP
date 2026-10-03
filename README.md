# Tableo (Kotlin Multiplatform)

Tableo für Android **und** iPhone. Gleicher Funktionsumfang wie das Android-Abschlussprojekt
(Bestellen am Tisch, Abholung, Lieferung mit Live-Tracking, Personal- und Fahrer-Bereich),
die Oberfläche ist mit Compose Multiplatform für beide Plattformen geschrieben.

## Aufbau

```
Tableo-KMP/
├── androidApp/   Android-App: MainActivity, TableoApp (startet Koin), Manifest, Icons
├── iosApp/       Xcode-Projekt: iOSApp.swift (startet Firebase), ContentView.swift
└── shared/
    └── src/
        ├── commonMain/   fast der ganze Code: Models, Repositories, ViewModels, Screens, Navigation
        │   └── composeResources/   Texte, Schriften, Logo
        ├── androidMain/  GPS, Google Maps, Benachrichtigungen, Berechtigungen, Fotoauswahl
        └── iosMain/      CoreLocation, MapKit, UserNotifications, PHPicker, Einstieg MainViewController
```

Was nur auf einer Plattform geht, steht als `expect` in commonMain und als `actual` in androidMain/iosMain
(z.B. `TrackingMap`, `rememberImagePicker`, `rememberLocationPermissionRequest`, `platformModule`).

## Mitarbeiter
- Mitarbeiter und Fahrer registrieren sich wie Kunden, schalten dabei "Ich arbeite in einem Restaurant" ein,
  wählen "Im Team" oder "Als Fahrer" und tragen die Restaurant-ID ein (Firestore-Dokument-ID, z.B. `layali-lounge`)
- Die ID steht im Dashboard unter Team, dort sieht man auch alle registrierten Mitarbeiter
- Beim Login erkennt die App über `staffProfiles`, ob das Konto zu einem Restaurant gehört,
  und öffnet direkt das Dashboard. Unter Konto gibt es dafür den Eintrag "Mein Restaurant"
- Fahrer haben die Rolle Lieferant und landen nach dem Login direkt im Fahrer-Bereich ihres Restaurants,
  einen Fahrer-PIN gibt es nicht mehr. Der Chef kann die Rolle auch im Team auf Lieferant setzen
- In der Team-Liste stehen Profilbild, Name und Rolle (Leitung, Kellner, Koch, Küchenhilfe, Bar, Lieferant).
  Antippen zeigt E-Mail und "Dabei seit", dort lässt sich die Rolle ändern oder der Mitarbeiter entfernen
- Das Profilbild setzt jeder selbst unter Konto → Kontoinformationen
- Im Dashboard meldet sich jeder Mitarbeiter mit dem Schalter "Im Dienst" an und ab. Nur wer im Dienst ist,
  bekommt eine Nachricht bei neuen Bestellungen, im Team sieht man das am grünen Punkt.
  Das klappt, solange die App offen oder im Hintergrund ist. Für Nachrichten bei ganz geschlossener App
  bräuchte man Firebase Cloud Messaging mit Cloud Functions, und das gibt es nur im Blaze-Tarif

## Konto löschen
- Unter Konto → "Konto löschen" mit dem Passwort bestätigen
- Gelöscht werden das Firebase-Konto, alle Team-Einträge (`staffProfiles`) und das Profilbild
- Bestellungen bleiben beim Restaurant für Archiv und Statistik erhalten

## Technik

| Bereich | Android-Projekt | Multiplatform |
|---|---|---|
| Oberfläche | Jetpack Compose | Compose Multiplatform |
| Firebase | Firebase Android SDK | GitLive Firebase SDK (Firestore, Auth, Storage) |
| API | Retrofit + Moshi | Ktor + kotlinx.serialization |
| DI | Koin | Koin (koin-compose-viewmodel) |
| Navigation | Navigation Compose | Navigation Compose Multiplatform, gleiche typisierten Routen |
| Bilder | Coil 2 | Coil 3 |
| Lokal speichern | DataStore | DataStore (Datei pro Plattform) |
| Karte | Google Maps Compose | Android: Google Maps, iOS: Apple Maps (MapKit) |
| Standort | FusedLocationProvider | Android: FusedLocationProvider, iOS: CoreLocation |

## Einrichtung

### Android
1. Projekt in Android Studio öffnen, Gradle Sync
2. `androidApp/google-services.json` ist schon drin (gleiche App-ID wie das alte Projekt)
3. Für die Karte in `local.properties`: `MAPS_API_KEY=dein-key`
4. Run-Konfiguration `androidApp` starten

### iOS
1. In der Firebase Console im selben Projekt eine **iOS-App** anlegen, Bundle-ID `com.alichehab.tableo`
2. `GoogleService-Info.plist` herunterladen und nach `iosApp/iosApp/` legen
3. `iosApp/iosApp.xcodeproj` in Xcode öffnen
4. **File → Add Package Dependencies…** → `https://github.com/firebase/firebase-ios-sdk`
   → Produkte **FirebaseAuth**, **FirebaseFirestore**, **FirebaseStorage** zum Target `iosApp` hinzufügen
5. Unter *Signing & Capabilities* das eigene Team wählen (für ein echtes iPhone)
6. In Xcode starten. Der erste Build dauert, weil Gradle das `shared`-Framework baut

## Firebase-Regeln

Firestore:

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /restaurants/{restaurantId} {
      allow read: if true;
    }
    match /menuItems/{menuItemId} {
      allow read, write: if true;
    }
    match /orders/{orderId} {
      allow read, write: if true;
    }
    match /staffProfiles/{profileId} {
      allow read, write: if true;
    }
  }
}
```

Storage (Speisekarten-Fotos und Profilbilder, braucht den Blaze-Tarif):

```
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /menuItems/{restaurantId}/{fileName} {
      allow read, write: if true;
    }
    match /profilePhotos/{fileName} {
      allow read, write: if true;
    }
  }
}
```

## Bekannte Grenzen
- Benachrichtigungen kommen nur, solange die App läuft (keine Push-Nachrichten über einen Server)
- Der Fahrer sendet seinen Standort nur, solange die App im Vordergrund ist
- Ankunftszeit über Luftlinie und Durchschnittsgeschwindigkeit, keine echte Route
- Ohne Blaze-Tarif gibt es kein Firebase Storage, dann klappt das Hochladen von Fotos und Profilbildern nicht.
  Die App läuft trotzdem, statt Bildern kommen Platzhalter bzw. Initialen

## Autor
Ali · Abschlussprojekt B29 · Syntax Institut

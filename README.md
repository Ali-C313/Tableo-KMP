# Tableo (Kotlin Multiplatform)

Tableo ist eine Bestell-App für Restaurants und Shisha-Bars, für Android **und** iPhone aus einem gemeinsamen Code
(Compose Multiplatform). Gäste bestellen am Tisch, zum Abholen oder per Lieferung, das Personal bearbeitet die Bestellungen
im Dashboard, und Fahrer liefern mit Live-Standort aus.

Abschlussprojekt B29 am Syntax Institut.

## Funktionen

**Gäste und Kunden**
- Startseite mit Lieferadresse und Schalter Lieferung/Abholung, beides bleibt nach dem Neustart gespeichert
- Bei Lieferung nur Restaurants, die liefern, Suche, Kategorien mit Bildern und Filter nach Art
- Speisekarte mit Kategorien und Preisen in Fremdwährung (Wechselkurs-API)
- Bestellen am Tisch, zur Abholung mit Uhrzeit oder als Lieferung, Bezahlart wählen
- Meine Bestellungen mit Live-Status, Bestelldetails, Erneut bestellen und Stornieren, solange noch offen
- Live-Tracking der Lieferung auf der Karte mit Ankunftszeit und Platz in der Tour des Fahrers
- Benachrichtigungen bei jedem neuen Schritt der Bestellung
- Konto mit Profilbild, Name, E-Mail und Passwort, Konto löschen mit 30 Tagen Frist
- 10 Sprachen, Hell/Dunkel, Impressum, Datenschutz und AGB

**Personal**
- Registrierung mit Restaurant-ID, danach direkt ins Dashboard
- Bestellungen weiterschalten (Neu → In Zubereitung → Fertig → Erledigt) oder stornieren
- Dienst-Schalter: im Dienst gibt es eine Nachricht bei jeder neuen Bestellung
- Speisekarte verwalten mit Fotos, Archiv und Statistik mit Diagramm
- Team mit Profilbild, Rolle und Dienst-Status, die Leitung vergibt Rollen und entfernt Mitarbeiter

**Fahrer**
- Registrierung als Fahrer, danach direkt in den Fahrer-Bereich des eigenen Restaurants
- Lieferungen annehmen, Tour sortieren, Standort wird live an die Kunden geschickt

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

## Projektstruktur (MVVM + Repository)

```
shared/src/commonMain/kotlin/com/example/tableo/
├── data/
│   ├── enums/        alle Enums, eine Datei pro Enum
│   ├── model/        Data Classes, eine Datei pro Model
│   ├── remote/       Wechselkurs-API (Ktor) und Firestore-Collection-Namen
│   └── repository/   Interfaces, dazu impl/ (Firebase, DataStore, Ktor) und mock/ (für Previews)
├── di/               Koin-Module
├── navigation/       typisierte Routen
├── ui/
│   ├── components/   Bausteine, die mehrere Screens nutzen
│   ├── <bereich>/    Screen, ViewModel, UiState und Bausteine nur für diesen Screen, je eine Datei
│   └── theme/        Farben, Schriften, Abstände
└── util/             Hilfsfunktionen (Formatieren, Berechtigungen, Sprache, Plattform)
```

- Screens bekommen ihr ViewModel per `koinViewModel()`, Previews bauen es mit den Mock-Repositories
- ViewModels sprechen nur mit Repositories, nie direkt mit Firebase oder Ktor
- Screens, Bausteine, ViewModels, UiStates, Models und Enums stehen jeweils in einer eigenen Datei, jedes Composable mit Preview

## Mitarbeiter
- Mitarbeiter und Fahrer registrieren sich wie Kunden, schalten dabei "Ich arbeite in einem Restaurant" ein,
  wählen "Im Team" oder "Als Fahrer" und tragen die Restaurant-ID ein (Firestore-Dokument-ID, z.B. `layali-lounge`)
- Die ID steht im Dashboard unter Team, dort sieht man auch alle registrierten Mitarbeiter.
  Rollen vergeben und Mitarbeiter entfernen kann nur die Leitung, die erste Leitung wird in der Firebase Console gesetzt
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

## Lieferung und Abholung
- Restaurants haben in Firestore das Feld `offersDelivery` (Boolean, Standard `true`)
- Steht oben "Lieferung", zeigt die Startseite nur Restaurants mit `offersDelivery = true`
- Bei "Abholung" erscheinen alle, Restaurants ohne Lieferung tragen den Hinweis "Nur Abholung"

## Sprachen
- Deutsch (Standard), Englisch, Türkisch, Arabisch (von rechts nach links), Spanisch, Französisch,
  Italienisch, Polnisch, Russisch und Ukrainisch
- Auswahl unter Konto → Einstellungen → Sprache, "Wie am Handy" nimmt die Sprache des Geräts
- Texte liegen in `shared/src/commonMain/composeResources/values*/strings*.xml`, eine Datei pro Bereich
  (common, settings, guest, orders, staff, driver). Neue Texte immer zuerst in `values/` (deutsch) anlegen
- Die Wahl wird per DataStore gespeichert, `AppLocaleEnvironment` setzt sie für Android und iOS

## Rechtliches
- Impressum, Datenschutz und AGB sind unter Konto erreichbar (`ui/legal/LegalScreen.kt`)
- Platzhalter in eckigen Klammern, z.B. `[Vor- und Nachname]`, vor einer Veröffentlichung ersetzen

## Konto löschen
- Unter Konto → "Konto löschen" mit dem Passwort bestätigen
- Das Konto wird nur vorgemerkt (`users/{userId}.deletionRequestedAt`) und abgemeldet
- Wer sich innerhalb von 30 Tagen wieder anmeldet, behält sein Konto
- Nach 30 Tagen wird es beim nächsten Anmeldeversuch endgültig gelöscht: Firebase-Konto,
  Team-Eintrag (`staffProfiles`) und Profilbild. Bestellungen bleiben beim Restaurant
- Ganz automatisch nach 30 Tagen, auch ohne erneuten Login, geht nur mit einer Cloud Function (Blaze-Tarif).
  Code dafür, z.B. in `functions/index.js`:

```js
const { onSchedule } = require("firebase-functions/v2/scheduler");
const admin = require("firebase-admin");
admin.initializeApp();

// läuft jede Nacht und löscht Konten, deren Vormerkung älter als 30 Tage ist
exports.deleteExpiredAccounts = onSchedule("every day 03:00", async () => {
  const limit = Date.now() - 30 * 24 * 60 * 60 * 1000;
  const db = admin.firestore();
  const expired = await db.collection("users").where("deletionRequestedAt", "<", limit).get();

  for (const doc of expired.docs) {
    const uid = doc.id;
    if (doc.data().deletionRequestedAt <= 0) continue;
    await db.collection("staffProfiles").doc(uid).delete().catch(() => {});
    await admin.storage().bucket().file(`profilePhotos/${uid}.jpg`).delete().catch(() => {});
    await admin.auth().deleteUser(uid).catch(() => {});
    await doc.ref.delete();
  }
});
```

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

In der Firebase Console unter **Authentication → Sign-in method** muss **Anonym** aktiviert sein.
Gäste ohne Konto werden beim Start anonym angemeldet, für die App zählen sie weiter als nicht eingeloggt.
Team-Einträge in `staffProfiles` haben die User-ID als Dokument-ID, so prüfen die Regeln direkt,
wo jemand arbeitet und ob er die Leitung hat.

Firestore:

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {

    function signedIn() {
      return request.auth != null;
    }

    function hasProfile() {
      return signedIn()
        && exists(/databases/$(database)/documents/staffProfiles/$(request.auth.uid));
    }

    function myProfile() {
      return get(/databases/$(database)/documents/staffProfiles/$(request.auth.uid)).data;
    }

    function worksAt(restaurantId) {
      return hasProfile() && myProfile().restaurantId == restaurantId;
    }

    function leads(restaurantId) {
      return worksAt(restaurantId) && myProfile().role == 'LEITUNG';
    }

    match /restaurants/{restaurantId} {
      allow read: if true;
      allow write: if false;
    }

    match /menuItems/{menuItemId} {
      allow read: if true;
      allow create: if worksAt(request.resource.data.restaurantId);
      allow update, delete: if worksAt(resource.data.restaurantId);
    }

    match /orders/{orderId} {
      allow read: if signedIn();
      allow create: if signedIn() && request.resource.data.status == 'OFFEN';
      // Personal und Fahrer des Restaurants, oder der Kunde selbst (Stornieren)
      allow update: if worksAt(resource.data.restaurantId)
        || (signedIn() && resource.data.userId == request.auth.uid);
      allow delete: if false;
    }

    // Zusatzdaten zum Konto, z.B. Lösch-Vormerkung
    match /users/{userId} {
      allow read, write: if signedIn() && request.auth.uid == userId;
    }

    match /staffProfiles/{profileId} {
      allow read: if signedIn();
      // eigenes Profil anlegen, aber nicht selbst zur Leitung machen
      allow create: if (signedIn()
          && profileId == request.auth.uid
          && request.resource.data.userId == request.auth.uid
          && request.resource.data.role in ['OHNE', 'LIEFERANT'])
        || leads(request.resource.data.restaurantId);
      // eigenes Profil: Dienst, Bild und Name ja, Rolle und Restaurant nur durch die Leitung
      allow update: if (signedIn()
          && profileId == request.auth.uid
          && request.resource.data.role == resource.data.role
          && request.resource.data.restaurantId == resource.data.restaurantId)
        || leads(resource.data.restaurantId);
      allow delete: if (signedIn() && profileId == request.auth.uid)
        || leads(resource.data.restaurantId);
    }
  }
}
```

Umstellung von den offenen Regeln:
1. Neue App-Version installieren und mit dem Leitungs-Konto einmal Konto und Team öffnen.
   Dabei ziehen alle alten Team-Einträge auf die User-ID als Dokument-ID um
2. Prüfen, dass das eigene Profil in `staffProfiles` die Rolle `LEITUNG` hat
3. Erst dann die Regeln oben veröffentlichen

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

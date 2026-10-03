import SwiftUI
import FirebaseCore

@main
struct iOSApp: App {

    init() {
        // liest GoogleService-Info.plist, muss vor jedem Firebase-Aufruf passieren
        FirebaseApp.configure()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

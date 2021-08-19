import SwiftUI
import FirebaseAuth

@main
struct ComposeApp: App {
    @UIApplicationDelegateAdaptor(ComposeAppDelegate.self) var appDelegate
    var body: some Scene {
        WindowGroup {
            if (Auth.auth().currentUser == nil) {
                WelcomeView()
            } else {
                ProductivityView()
            }
        }
    }
}

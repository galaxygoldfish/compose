import SwiftUI
import FirebaseAuth

@main
struct ComposeApp: App {
   
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

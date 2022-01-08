import SwiftUI
import FirebaseAuth
import Firebase

@main
struct ComposeApp: App {
    
    init() {
        FirebaseApp.configure()
    }
    
    @StateObject var productivityViewModel = ProductivityViewModel()
   
    var body: some Scene {
        WindowGroup {
            if (Auth.auth().currentUser == nil) {
                WelcomeView()
            } else {
                ProductivityView().environmentObject(productivityViewModel)
            }
        }
    }
}

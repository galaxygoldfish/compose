import SwiftUI
import FirebaseAuth

struct ProductivityView: View {
    var body: some View {
        VStack {
            Text("Authenticated!")
            Button(
                action: {
                    FirebaseAccount().signOutCurrentUser()
                }
            ) {
                Text("Sign out")
            }
        }
    }
}

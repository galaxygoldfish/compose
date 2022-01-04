import SwiftUI

struct NoteListView: View {
    var body: some View {
        ZStack {
            FullscreenPlaceholder()
            VStack {
                Text("Notes")
            }
        }
        .background(Color("ColorBackground"))
    }
}

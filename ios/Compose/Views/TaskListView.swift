import SwiftUI

struct TaskListView: View {
    var body: some View {
        ZStack {
            FullscreenPlaceholder()
            VStack {
                Text("Tasks")
            }
        }
        .background(Color("ColorBackground"))
    }
}


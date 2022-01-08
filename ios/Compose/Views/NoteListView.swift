import SwiftUI
import SwiftUIRefresh

struct NoteListView: View {
    
    @EnvironmentObject private var viewModel: ProductivityViewModel
    
    @State var refreshing = true
    
    var body: some View {
        ScrollView {
            HStack(alignment: .top, spacing: 1) {
                Spacer()
                LazyVStack {
                    ForEach(viewModel.noteList1) { item in
                        NoteListCard(item: item)
                    }
                }
                .frame(width: UIScreen.main.bounds.width * 0.45)
                Spacer()
                LazyVStack {
                    ForEach(viewModel.noteList2) { item in
                        NoteListCard(item: item)
                    }
                }
                .frame(width: UIScreen.main.bounds.width * 0.45)
                Spacer()
            }
            .frame(width: UIScreen.main.bounds.width * 0.94)
            .padding(.top, 10)
        }
    }
}

struct NoteListCard: View {
    var item: NoteDocument
    var body: some View {
        VStack {
            Text(item.title)
            Text(item.content)
                .lineLimit(8)
            HStack {
                Text(item.date)
                Text(item.time)
            }
        }
        .frame(width: UIScreen.main.bounds.width * 0.435)
        .background(Color("NeutralGrayDark"))
        .cornerRadius(10)
        .padding(.bottom, 5)
    }
}

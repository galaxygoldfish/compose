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
            .padding(.top, 5)
        }
        .animation(.easeInOut)
    }
}

struct NoteListCard: View {
    
    @Environment(\.colorScheme) private var colorScheme
    var item: NoteDocument

    var body: some View {
        VStack(alignment: .leading, spacing: 1) {
            Text(item.title)
                .font(typographyH6)
                .padding(.horizontal, 10)
                .padding(.top, 10)
                .foregroundColor(.black)
            Text(item.content)
                .lineLimit(8)
                .font(typographyBody1)
                .padding(.horizontal, 10)
                .padding(.top, 2)
                .foregroundColor(.black)
            HStack {
                Text(item.date)
                    .font(typographyBody1)
                Spacer()
                Text(item.time)
                    .font(typographyBody1)
            }
            .padding(.horizontal, 10)
            .padding(.vertical, 5)
            .padding(.bottom, 5)
            .foregroundColor(colorScheme == .dark ? Color("NeutralGray") : .black.opacity(0.8))
        }
        .frame(width: UIScreen.main.bounds.width * 0.445)
        .background(Color("CardColor\(item.color)"))
        .cornerRadius(10)
        .padding(.bottom, 2)
    }
}

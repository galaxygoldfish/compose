import SwiftUI
import SwiftUIPager
import UIKit
import FirebaseAuth

struct ProductivityView: View {
    
    @EnvironmentObject private var viewModel: ProductivityViewModel
    
    var body: some View {
        ZStack(alignment: .topLeading) {
            FullscreenPlaceholder()
            VStack {
                TopAppBar()
                Pager(
                    page: viewModel.currentPage,
                    data: Array(0...1),
                    id: \.self,
                    content: { index in
                        switch index {
                        case 0:
                            NoteListView()
                        default:
                            TaskListView()
                        }
                    }
                ).animation(.linear)
                NoteTaskPager()
            }
        }
        .navigationBarTitle("")
        .navigationBarHidden(true)
        .navigationBarBackButtonHidden(true)
    }
}

struct TopAppBar : View {

    @EnvironmentObject private var viewModel: ProductivityViewModel
    
    @State private var searchQueryText: String = ""
    private let userFirstName = getNsUserDefaults().string(forKey: "IDENTITY_USER_NAME_FIRST") ?? "Error"
    
    var body: some View {
        HStack(alignment: .center) {
            VStack(alignment: .leading) {
                Text("Welcome, \(userFirstName)")
                    .font(.custom(InterBold, size: 26.0))
                    .padding(.leading, 18)
                    .padding(.top, 15)
                Text("productivity_temp_message")
                    .font(.custom(InterRegular, size: 16.5))
                    .padding(.leading, 19)
            }
            Spacer()
            Image(uiImage: getAvatarImage())
                .resizable()
                .clipShape(Circle())
                .frame(width: 48, height: 48, alignment: .trailing)
                .padding(.trailing, 17)
                .padding(.top, 15)
        }
        ZStack(alignment: Alignment.leading) {
            Color("NeutralGray")
                .cornerRadius(10)
            HStack {
                Image("SearchMagnifier")
                    .padding(.leading, 15)
                Spacer()
                TextField(
                    LocalizedStringKey("productivity_search_hint"),
                    text: $searchQueryText
                )
                .font(typographyBody2)
                .padding(.leading, 5)
                Spacer()
                Image("KeyboardVoice")
                    .padding(.trailing, 15)
            }
        }
        .frame(width: .infinity, height: 55)
        .padding(.leading, 18)
        .padding(.trailing, 17)
        .padding(.top, 10)
    }
}


struct NoteTaskPager: View {
    
    @EnvironmentObject private var viewModel: ProductivityViewModel
    
    @State private var noteTabColor: Color = Color.white
    @State private var taskTabColor: Color = Color("NeutralGrayDisabled")
    
    var body: some View {
        ZStack {
            Color("NeutralGray")
                .cornerRadius(10)
            HStack(alignment: .center) {
                Spacer()
                Button(
                    action: {
                        viewModel.currentPage = .withIndex(0)
                    }
                ) {
                    Image("EditPen")
                        .resizable()
                        .colorMultiply(
                            viewModel.currentPage.index == 0 ? Color("ColorOnBackground") : Color("NeutralGrayDisabled")
                        )
                        .frame(width: 30, height: 30)
                }
                .padding(.trailing, 25)
                .frame(width: 42, height: 42, alignment: .center)
                Spacer()
                Button(
                    action: {
                        try! Auth.auth().signOut()
                    }
                ) {
                    ZStack(alignment: .center) {
                        Color("DeepSea")
                            .cornerRadius(8)
                        Image("NewItem")
                            .colorMultiply(.black)
                    }
                }
                .frame(width: 45, height: 45, alignment: .center)
                Spacer()
                Button(
                    action: {
                        viewModel.currentPage = .withIndex(1)
                    }
                ) {
                    Image("TaskCheck")
                        .resizable()
                        .colorMultiply(
                            viewModel.currentPage.index == 1 ? Color("ColorOnBackground") : Color("NeutralGrayDisabled")
                        )
                        .frame(width: 30, height: 30)
                }
                .padding(.leading, 25)
                Spacer()
            }
        }
        .frame(width: .infinity, height: 65)
        .padding(.leading, 18)
        .padding(.trailing, 17)
        .padding(.bottom, 10)
    }
}

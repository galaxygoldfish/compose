import SwiftUI
import SwiftUIPager
import UIKit
import FirebaseAuth
import SwiftUIGenericDialog

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
            }
            VStack {
                Spacer()
                NoteTaskPager()
            }
        }
        .navigationBarTitle("")
        .navigationBarHidden(true)
        .navigationBarBackButtonHidden(true)
        .genericDialog(
            isShowing: $viewModel.showingProfileDialog,
            dialogContent: {
                ProfileContextMenu()
            }
        )
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
                    .font(typographyBody1)
                    .padding(.leading, 19)
            }
            Spacer()
            Button(
                action: {
                    viewModel.showingProfileDialog = true
                }
            ) {
            Image(uiImage: getAvatarImage())
                .resizable()
                .clipShape(Circle())
                .frame(width: 48, height: 48, alignment: .trailing)
                .padding(.trailing, 17)
                .padding(.top, 15)
            }
        }
        ZStack(alignment: Alignment.leading) {
            colorPrimaryVariant
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
            colorPrimaryVariant
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
                            viewModel.currentPage.index == 0 ? colorOnBackground : colorOnBackground.opacity(0.5)
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
                        colorPrimary
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
                            viewModel.currentPage.index == 1 ? colorOnBackground : colorOnBackground.opacity(0.5)
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

struct ProfileContextMenu: View {
    
    private let preferences = getNsUserDefaults()
    
    var body: some View {
        VStack {
            HStack {
                Spacer()
                VStack(alignment: .leading, spacing: 1) {
                    Text(preferences.string(forKey: "IDENTITY_USER_NAME_FIRST") ?? "Error")
                        .font(typographyH5)
                    Text(preferences.string(forKey: "IDENTITY_USER_NAME_LAST") ?? "Error")
                        .font(typographyH5)
                        .padding(.top, 2)
                }
                Spacer()
                Spacer()
                Image(uiImage: getAvatarImage())
                    .resizable()
                    .clipShape(Circle())
                    .frame(width: 90, height: 90, alignment: .trailing)
                    .padding(.top, 20)
                Spacer()
            }
            VStack(alignment: .leading) {
                OptionListItem(
                    text: LocalizedStringKey("profile_context_menu_preferences"),
                    icon: "Preferences",
                    onClick: {
                        // navigate settings
                    }
                )
                OptionListItem(
                    text: LocalizedStringKey("profile_context_menu_account"),
                    icon: "UserGroup",
                    onClick: {
                        // navigate account page
                    }
                )
                OptionListItem(
                    text: LocalizedStringKey("profile_context_menu_theme"),
                    icon: "ThemeShirt",
                    onClick: {
                        // switch dark/light mode and save
                    }
                )
                OptionListItem(
                    text: LocalizedStringKey("profile_context_menu_logout"),
                    icon: "LogInArrow",
                    onClick: {
                        // log out and navigate welcome
                    }
                )
            }
            .padding(.bottom, 20)
            .padding(.leading, 20)
        }
        .frame(width: UIScreen.main.bounds.width * 0.8)
        .background(colorPrimaryVariant.cornerRadius(7.9))
    }
}

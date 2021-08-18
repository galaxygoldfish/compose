import SwiftUI

struct WelcomeView : View {
    
    @State private var navigationChoice: String? = nil
    
    var body: some View {
        NavigationView {
            ZStack(alignment: .topLeading) {
                FullscreenPlaceholder()
                VStack(alignment: .leading) {
                    Text("welcome_header_message")
                        .font(.custom(InterBold, size: 35.0))
                        .padding(.top)
                        .padding(.leading, 20)
                    Text("welcome_subtitle_text")
                        .font(.custom(InterRegular, size: 16))
                        .padding(.leading, 20)
                        .padding(.top, 2)
                        .padding(.trailing, 30)
                    HStack {
                        Spacer()
                        Image("WelcomeGraphic")
                            .resizable()
                            .aspectRatio(contentMode: .fit)
                            .padding(.horizontal, 35)
                            .padding(.vertical, 30)
                        Spacer()
                    }
                    Spacer()
                    NavigationLink(
                        destination: LogInView(),
                        tag: "logIn",
                        selection: $navigationChoice
                    ) {
                        FullWidthButton (
                            text: "welcome_button_log_in",
                            icon: "LogInArrow",
                            color: Color("DeepSea"),
                            onAction: {
                                navigationChoice = "logIn"
                            }
                        )
                    }
                    .navigationBarBackButtonHidden(true)
                    NavigationLink(
                        destination: CreateAccountView(),
                        tag: "createAccount",
                        selection: $navigationChoice
                    ) {
                        FullWidthButton (
                            text: "welcome_button_create_account",
                            icon: "AddUser",
                            color: Color("NeutralGray"),
                            onAction: {
                                navigationChoice = "createAccount"
                            }
                        )
                        .padding(.bottom, 15)
                    }
                    .navigationBarBackButtonHidden(true)
                }
            }
            .navigationBarTitle("")
            .navigationBarHidden(true)
            .navigationBarBackButtonHidden(true)
        }
    }
}

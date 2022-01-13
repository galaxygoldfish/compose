import SwiftUI

struct WelcomeView : View {
    
    @State private var navigationChoice: String? = nil
    
    var body: some View {
        NavigationView {
            ZStack(alignment: .topLeading) {
                FullscreenPlaceholder()
                VStack(alignment: .leading, spacing: 0) {
                    Text("welcome_header_message")
                        .font(typographyH1)
                        .padding(.top)
                        .padding(.leading, 20)
                    Text("welcome_subtitle_text")
                        .font(typographyBody1)
                        .padding(.leading, 20)
                        .padding(.trailing, 30)
                        .padding(.top, 10)
                    Spacer()
                    HStack {
                        Spacer()
                        Image("WelcomeGraphic")
                            .resizable()
                            .aspectRatio(contentMode: .fit)
                            .padding(.horizontal, 35)
                            .padding(.bottom, 20)
                            .padding(.top, 10)
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
                            color: colorPrimary,
                            onAction: {
                                navigationChoice = "logIn"
                            }
                        )
                            .padding(.bottom, 10)
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
                            color: colorSecondaryVariant,
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

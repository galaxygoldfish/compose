import SwiftUI

struct LogInView: View {
    
    @State private var emailText: String = ""
    @State private var passwordText: String = ""
    
    @State private var navigateProductivity: Bool = false
    
    @State private var snackbarOpen: Bool = false
    @State private var snackbarIcon: String = "WarningAlert"
    @State private var snackbarMessage: LocalizedStringKey = LocalizedStringKey("log_in_progress")
    
    @Environment(\.presentationMode) var presentationMode: Binding<PresentationMode>
    
    var body: some View {
        ZStack(alignment: .topLeading) {
            FullscreenPlaceholder()
            ScrollView {
            VStack(alignment: .leading) {
                Button(action: {
                    presentationMode.wrappedValue.dismiss()
                }) {
                    Image("BackArrow")
                        .padding(.top)
                        .padding(.leading, 16)
                }
                Text("log_in_header_message")
                    .font(.custom(InterBold, size: 35.0))
                    .padding(.top, 2)
                    .padding(.leading, 20)
                Text("log_in_subtitle_text")
                    .font(.custom(InterRegular, size: 16))
                    .padding(.leading, 20)
                    .padding(.top, 2)
                    .padding(.trailing, 30)
                Text("log_in_form_header")
                    .font(.custom(InterBold, size: 14))
                    .padding(.leading, 20)
                    .padding(.top, 15)
                    .padding(.trailing, 30)
                TextInputFieldLarge(
                    icon: "MailLetter",
                    hint: "log_in_email_field_hint",
                    inputText: $emailText
                )
                TextInputFieldLarge(
                    icon: "PasswordLock",
                    hint: "log_in_password_field_hint",
                    inputText: $passwordText,
                    secureField: true
                )
                if (snackbarOpen) {
                    withAnimation {
                        Snackbar(
                            message: $snackbarMessage,
                            icon: $snackbarIcon,
                            showingSnackbar: $snackbarOpen
                        )
                        .padding(.top, 15)
                    }
                }
                HStack(alignment: .center) {
                    TextOnlyButton(
                        text: "log_in_button_cancel_text",
                        color: Color("NeutralGray"),
                        onAction: {
                            presentationMode.wrappedValue.dismiss()
                        }
                    )
                    .padding(.leading, 20)
                    Spacer()
                    NavigationLink(
                        destination: ProductivityView(),
                        isActive: $navigateProductivity
                    ) {
                        TextOnlyButton(
                            text: "log_in_button_continue_text",
                            color: Color("DeepSea"),
                            onAction: {
                                FirebaseAccount().authenticateWithEmail(
                                    email: emailText,
                                    password: passwordText,
                                    onCompletion: { stringKey in
                                        if (stringKey == LocalizedStringKey("success_internal")) {
                                            navigateProductivity = true
                                        } else {
                                            snackbarOpen = true
                                            snackbarMessage = stringKey
                                            snackbarIcon = "WarningAlert"
                                        }
                                    }
                                )
                            }
                        )
                        .padding(.trailing, 20)
                    }
                }
                .padding(.top, 10)
            }
        }
        .navigationBarTitle("")
        .navigationBarHidden(true)
    }
}
}

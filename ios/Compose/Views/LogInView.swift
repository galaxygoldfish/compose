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
            VStack(alignment: .leading, spacing: 0) {
                Button(action: {
                    presentationMode.wrappedValue.dismiss()
                }) {
                    Image("BackArrow")
                        .padding(.top)
                        .padding(.leading, 16)
                }
                Text("log_in_header_message")
                    .font(typographyH1)
                    .padding(.top, 2)
                    .padding(.leading, 20)
                Text("log_in_subtitle_text")
                    .font(typographyBody1)
                    .padding(.leading, 20)
                    .padding(.top, 10)
                    .padding(.trailing, 30)
                Text("log_in_form_header")
                    .font(typographyOverline)
                    .padding(.leading, 20)
                    .padding(.top, 25)
                    .padding(.trailing, 30)
                TextInputFieldLarge(
                    icon: "MailLetter",
                    hint: "log_in_email_field_hint",
                    inputText: $emailText
                ).padding(.top, 10)
                TextInputFieldLarge(
                    icon: "PasswordLock",
                    hint: "log_in_password_field_hint",
                    inputText: $passwordText,
                    secureField: true
                ).padding(.top, 10)
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
                        color: colorSecondaryVariant,
                        onAction: {
                            presentationMode.wrappedValue.dismiss()
                        }
                    )
                    .padding(.leading, 20)
                    Spacer()
                    NavigationLink(
                        destination: ProductivityView()
                            .environmentObject(ProductivityViewModel()),
                        isActive: $navigateProductivity
                    ) {
                        TextOnlyButton(
                            text: "log_in_button_continue_text",
                            color: colorPrimary,
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
                .padding(.top, 15)
            }
        }
        .navigationBarTitle("")
        .navigationBarHidden(true)
    }
}
}

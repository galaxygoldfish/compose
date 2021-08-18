import SwiftUI

struct CreateAccountView: View {
    
    @State private var emailText: String = ""
    @State private var passwordText: String = ""
    
    @State private var nameText: String = ""
    @State private var surnameText: String = ""
    
    @Environment(\.presentationMode) var presentationMode: Binding<PresentationMode>
    
    var body: some View {
        ZStack(alignment: .topLeading) {
            FullscreenPlaceholder()
            VStack(alignment: .leading) {
                Button(action: {
                    presentationMode.wrappedValue.dismiss()
                }) {
                    Image("BackArrow")
                        .padding(.top)
                        .padding(.leading, 16)
                }
                Text("create_account_header_message")
                    .font(.custom(InterBold, size: 35.0))
                    .padding(.top, 2)
                    .padding(.leading, 20)
                Text("create_account_subtitle_text")
                    .font(.custom(InterRegular, size: 16))
                    .padding(.leading, 20)
                    .padding(.top, 2)
                    .padding(.trailing, 30)
                Text("create_account_account_form_header")
                    .font(.custom(InterBold, size: 14))
                    .padding(.leading, 20)
                    .padding(.top, 15)
                    .padding(.trailing, 30)
                VStack {
                    TextInputFieldLarge(
                        icon: "MailLetter",
                        hint: "create_account_email_field_hint",
                        inputText: $emailText
                    )
                    TextInputFieldLarge(
                        icon: "PasswordLock",
                        hint: "create_account_password_field_hint",
                        inputText: $passwordText,
                        secureField: true
                    )
                }
                Text("create_account_profile_form_header")
                    .font(.custom(InterBold, size: 14))
                    .padding(.leading, 20)
                    .padding(.top, 15)
                    .padding(.trailing, 30)
                VStack {
                    TextInputFieldLarge(
                        icon: "UserSingle",
                        hint: "create_account_name_field_hint",
                        inputText: $nameText
                    )
                    TextInputFieldLarge(
                        icon: "UserGroup",
                        hint: "create_account_surname_field_hint",
                        inputText: $surnameText
                    )
                }
                HStack {
                    VStack {
                        HStack {
                            Text("create_account_choose_avatar_header")
                                .font(.custom(InterRegular, size: 16))
                                .padding(.leading, 20)
                                .padding(.top, 20)
                            Spacer()
                        }
                        HStack {
                            IconOnlyButton(
                                icon: "PhotoCamera",
                                onAction: {
                                    
                                }
                            )
                            IconOnlyButton(
                                icon: "PhotoGallery",
                                onAction: {
                                    
                                }
                            ).padding(.leading, 5)
                            Spacer()
                        }
                        .padding(.leading, 20)
                    }
                    Spacer()
                    Image("DefaultAvatar")
                        .resizable()
                        .frame(width: 100.0, height: 100.0)
                        .padding(.trailing, 20)
                        .padding(.top, 20)
                }
                HStack {
                    TextOnlyButton(
                        text: "create_account_button_cancel",
                        color: Color("NeutralGray"),
                        onAction: {
                            presentationMode.wrappedValue.dismiss()
                        }
                    )
                    .padding(.leading, 20)
                    Spacer()
                    TextOnlyButton(
                        text: "create_account_button_continue",
                        color: Color("DeepSea"),
                        onAction: {
                            
                        }
                    )
                    .padding(.trailing, 20)
                }
                .padding(.top, 20)
            }
        }
        .navigationBarTitle("")
        .navigationBarHidden(true)
    }
}

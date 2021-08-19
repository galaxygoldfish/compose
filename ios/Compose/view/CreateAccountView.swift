import SwiftUI

struct CreateAccountView: View {
    
    @State private var emailText: String = ""
    @State private var passwordText: String = ""
    @State private var nameText: String = ""
    @State private var surnameText: String = ""
    
    @State private var selectedAvatar: UIImage = UIImage(named: "DefaultAvatar") ?? UIImage()
    @State private var photoLibraryType: UIImagePickerController.SourceType = UIImagePickerController.SourceType.photoLibrary
    
    @Environment(\.presentationMode) var presentationMode: Binding<PresentationMode>
    
    @State private var photoLibraryOpen: Bool = false
    @State private var snackbarOpen: Bool = false
    @State private var navigateAuthenticated: Bool = false
    
    @State private var snackbarMessage: LocalizedStringKey = LocalizedStringKey("create_account_failure_generic")
    @State private var snackbarIcon: String = "AddUser"
    
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
                ScrollView {
                    VStack(alignment: .leading) {
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
                                        .padding(.bottom, 10)
                                    Spacer()
                                }
                                HStack {
                                    IconOnlyButton(
                                        icon: "PhotoCamera",
                                        onAction: {
                                            photoLibraryType = UIImagePickerController.SourceType.photoLibrary
                                            photoLibraryOpen = true
                                        }
                                    )
                                    IconOnlyButton(
                                        icon: "PhotoGallery",
                                        onAction: {
                                            photoLibraryType = UIImagePickerController.SourceType.camera
                                            photoLibraryOpen = true
                                        }
                                    )
                                    .padding(.leading, 5)
                                    Spacer()
                                }
                                .padding(.leading, 20)
                            }
                            Spacer()
                            Image(uiImage: selectedAvatar)
                                .resizable()
                                .clipShape(Circle())
                                .frame(width: 100.0, height: 100.0)
                                .padding(.trailing, 20)
                                .padding(.top, 20)
                        }
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
                            NavigationLink(
                                destination: ProductivityView(),
                                isActive: $navigateAuthenticated
                            ) {
                                TextOnlyButton(
                                    text: "create_account_button_continue",
                                    color: Color("DeepSea"),
                                    onAction: {
                                        snackbarMessage = LocalizedStringKey("create_account_progress_message")
                                        snackbarOpen = true
                                        FirebaseAccount().createUserWithEmail(
                                            email: emailText,
                                            password: passwordText,
                                            name: nameText,
                                            surname: surnameText,
                                            avatar: selectedAvatar,
                                            onCompletion: { stringKey in
                                                if (stringKey == LocalizedStringKey("success_internal")) {
                                                    navigateAuthenticated = true
                                                } else {
                                                    snackbarMessage = stringKey
                                                    snackbarIcon = "WarningAlert"
                                                    snackbarOpen = true
                                                }
                                            }
                                        )
                                    }
                                )
                                .padding(.trailing, 20)
                            }
                        }
                        .padding(.top, 20)
                        .padding(.bottom, 20)
                    }
                }
                .navigationBarTitle("")
                .navigationBarHidden(true)
                .sheet(isPresented: $photoLibraryOpen, content: {
                    ImagePicker(sourceType: photoLibraryType, selectedImage: $selectedAvatar)
                })
            }
        }
    }
}



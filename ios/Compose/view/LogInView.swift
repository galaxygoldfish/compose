import SwiftUI

struct LogInView: View {
    
    @State public var emailText: String = ""
    @State public var passwordText: String = ""
    
    var body: some View {
        ZStack(alignment: .topLeading) {
            FullscreenPlaceholder()
            VStack {
                VStack(alignment: .leading) {
                    Text("Log in to your account")
                        .font(.custom(InterBold, size: 35.0))
                        .padding(.top)
                        .padding(.leading, 20)
                    Text("Welcome back to Compose. To continue, enter your account details below:")
                        .font(.custom(InterRegular, size: 16))
                        .padding(.leading, 20)
                        .padding(.top, 2)
                        .padding(.trailing, 30)
                    Text("SIGN-IN")
                        .font(.custom(InterBold, size: 14))
                        .padding(.leading, 20)
                        .padding(.top, 15)
                        .padding(.trailing, 30)
                    TextInputFieldLarge(
                        icon: "envelope.fill",
                        hint: "Email address",
                        inputText: $emailText
                    )
                    TextInputFieldLarge(
                        icon: "lock.rectangle.fill",
                        hint: "Password",
                        inputText: $passwordText
                    )
                    HStack(alignment: .center) {
                        TextOnlyButton(
                            text: "CANCEL",
                            color: Color("NeutralGray"),
                            onAction: {
                            
                            }
                        )
                        .padding(.leading, 20)
                        Spacer()
                        TextOnlyButton(
                            text: "CONTINUE",
                            color: Color("DeepSea"),
                            onAction: {
                            
                            }
                        )
                        .padding(.trailing, 20)
                    }
                    .padding(.top, 10)
                }
            }
        }
    }
}

struct LogInView_Previews : PreviewProvider {
    static var previews: some View {
        LogInView()
    }
}

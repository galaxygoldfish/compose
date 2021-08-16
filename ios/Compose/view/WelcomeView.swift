
import SwiftUI

struct WelcomeView: View {
    var body: some View {
        ZStack(alignment: .topLeading) {
            FullscreenPlaceholder()
            VStack(alignment: .leading) {
                Text("Welcome to\nCompose")
                    .font(.custom(InterBold, size: 35.0))
                    .padding(.top)
                    .padding(.leading, 20)
                Text("Organize your life by saving all your notes & tasks with Compose, backed by cloud save.")
                    .font(.custom(InterRegular, size: 16))
                    .padding(.leading, 20)
                    .padding(.top, 2)
                    .padding(.trailing, 30)
                Image("WelcomeGraphic")
                    .resizable()
                    .aspectRatio(contentMode: .fit)
                    .padding(.horizontal, 35)
                    .padding(.vertical, 30)
                Spacer()
                FullWidthButton (
                    text: "LOG IN",
                    systemIcon: "arrow.right.square",
                    color: Color("DeepSea"),
                    onAction: {
                        
                    }
                )
                FullWidthButton (
                    text: "CREATE ACCOUNT",
                    systemIcon: "person.crop.circle.badge.plus",
                    color: Color("NeutralGray"),
                    onAction: {
                    }
                )
                .padding(.bottom, 15)
            }
        }
    }
}

struct WelcomeView_Previews: PreviewProvider {
    static var previews: some View {
        WelcomeView()
    }
}

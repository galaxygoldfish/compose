import SwiftUI

/**
 * Creates an input field that accepts single line text as input, including
 * an icon with a neutral gray background.
 */
func TextInputFieldLarge(icon: String, hint: String, inputText: Binding<String>) -> some View {
    return ZStack(alignment: .leading) {
        Rectangle()
            .foregroundColor(Color("NeutralGray"))
            .cornerRadius(10)
            .frame(height: 55)
            .padding(.leading, 20)
            .padding(.trailing, 20)
        Image(systemName: icon)
            .padding(.leading, 35)
        TextField(hint, text: inputText)
            .padding(.leading, 70)
            .padding(.trailing, 80)
            .font(.custom(InterSemiBold, size: 15.0))
    }
}

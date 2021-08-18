import SwiftUI

/**
 * Creates an input field that accepts single line text as input, including
 * an icon with a neutral gray background.
 */
func TextInputFieldLarge(
    icon: String,
    hint: String,
    inputText: Binding<String>,
    secureField: Bool = false
) -> some View {
    return ZStack(alignment: .leading) {
        Rectangle()
            .foregroundColor(Color("NeutralGray"))
            .cornerRadius(10)
            .frame(height: 55)
            .padding(.leading, 20)
            .padding(.trailing, 20)
        HStack {
            Image(icon)
                .padding(.leading, 35)
           if (secureField) {
                SecureField(
                    LocalizedStringKey(hint),
                    text: inputText
                )
                    .padding(.leading, 6)
                    .padding(.trailing, 80)
                    .font(.custom(InterRegular, size: 15.0))
            } else {
                TextField(
                    LocalizedStringKey(hint),
                    text: inputText
                )
                    .padding(.leading, 6)
                    .padding(.trailing, 80)
                    .font(.custom(InterRegular, size: 16.0))
            }
            
        }
    }
}

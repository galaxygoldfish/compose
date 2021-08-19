import SwiftUI
import UIKit

/**
 * Creates a placeholder view to be used in layouts, to fill the full device
 * width.
 */
func FullscreenPlaceholder() -> some View {
    return Color.clear.frame(maxWidth: .infinity, maxHeight: .infinity)
}

func Snackbar(
    message: Binding<LocalizedStringKey>,
    icon: Binding<String>,
    showingSnackbar: Binding<Bool>
) -> some View {
    return ZStack {
        Color("NeutralGrayDark")
            .cornerRadius(10)
        HStack {
           Spacer()
            Image(icon.wrappedValue)
                .padding(10)
            Spacer()
            Text(message.wrappedValue)
                .font(.custom(InterRegular, size: 15))
                .padding(.top, 15)
                .padding(.bottom, 15)
                .padding(.trailing, 5)
            Spacer()
        }
    }
    .padding(.trailing, 20)
    .padding(.leading, 20)
}

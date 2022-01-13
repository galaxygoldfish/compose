import SwiftUI
import UIKit

/**
 * Creates a placeholder view to be used in layouts, to fill the full device
 * width.
 */
func FullscreenPlaceholder() -> some View {
    return Color.clear.frame(maxWidth: .infinity, maxHeight: .infinity)
}

struct Snackbar: View {
    
    var message: Binding<LocalizedStringKey>
    var icon: Binding<String>
    var showingSnackbar: Binding<Bool>
    
    var body: some View {
        return ZStack(alignment: .leading) {
            colorPrimaryVariant
                .opacity(0.8)
                .cornerRadius(10)
            HStack() {
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
}

struct OptionListItem: View {
    
    var text: LocalizedStringKey
    var icon: String
    var onClick: () -> Void
    
    var body: some View {
        Button(action: onClick) {
            HStack(alignment: .center) {
                Image(icon)
                    .frame(width: 24, height: 24, alignment: .leading)
                Text(text)
                    .foregroundColor(colorOnBackground)
                    .font(typographyBody1)
                    .padding(.leading, 10)
                    .frame(alignment: .leading)
            }
            .frame(width: UIScreen.main.bounds.width * 0.8, height: 40, alignment: .leading)
            .padding(.leading, 25)
        }
    }
    
}

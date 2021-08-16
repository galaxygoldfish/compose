import SwiftUI

/**
 * Action button that includes an icon, color and text,
 * which extends to the full screen width, with padding
 * on the sides
 */
public func FullWidthButton(text: String, systemIcon: String,
                            color: Color, onAction: @escaping () -> Void) -> some View {
    return Button(action: onAction.self) {
        HStack() {
            Image(systemName: systemIcon)
            Spacer()
            Text(text)
                .font(.custom(InterBold, size: 16.0))
            Spacer()
        }
        .frame(minWidth: 0, maxWidth: .infinity, alignment: .center)
        .padding()
        .contentShape(Rectangle())
        .background(color)
        .foregroundColor(.black)
        .cornerRadius(10.0)
        .padding(.horizontal, 20)
    }
}

/**
 * Button thart includes color and text, ideal when there
 * are multiple primary actions on the screen.
 */
public func TextOnlyButton(text: String, color: Color,
                           onAction: @escaping () -> Void) -> some View {
    return Button(action: onAction.self) {
        HStack(alignment: .center) {
            Text(text)
                .font(.custom(InterBold, size: 14.0))
                .padding(.leading, 10)
                .padding(.trailing, 10)
        }
        .padding()
        .contentShape(Rectangle())
        .background(color)
        .foregroundColor(.black)
        .cornerRadius(8.0)
    }
}

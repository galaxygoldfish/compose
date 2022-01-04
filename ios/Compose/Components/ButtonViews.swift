import SwiftUI

/**
 * Action button that includes an icon, color and text,
 * which extends to the full screen width, with padding
 * on the sides
 */
public func FullWidthButton(
    text: String,
    icon: String,
    color: Color,
    onAction: @escaping () -> Void
) -> some View {
    return Button(action: onAction.self) {
        HStack() {
            Image(icon).colorMultiply(.black)
            Spacer()
            Text(LocalizedStringKey(text))
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
public func TextOnlyButton(
    text: String,
    color: Color,
    onAction: @escaping () -> Void
) -> some View {
    return Button(action: onAction.self) {
        HStack(alignment: .center) {
            Text(LocalizedStringKey(text))
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

public func IconOnlyButton(
    icon: String,
    onAction: @escaping () -> Void
) -> some View {
    return Button(action: onAction.self) {
        Image(icon)
            .padding(.leading, 20)
            .padding(.trailing, 20)
            .padding(.top, 10)
            .padding(.bottom, 10)
    }
    .contentShape(Rectangle())
    .background(Color("NeutralGray"))
    .cornerRadius(8.0)
}

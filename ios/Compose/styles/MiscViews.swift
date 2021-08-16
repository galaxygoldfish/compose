import SwiftUI

/**
 * Creates a placeholder view to be used in layouts, to fill the full device
 * width.
 */
func FullscreenPlaceholder() -> some View {
    return Color.clear.frame(maxWidth: .infinity, maxHeight: .infinity)
}

import SwiftUI

struct FocusLoopMark: View {
    var size: CGFloat = 48

    var body: some View {
        Image("FocusLoop")
            .renderingMode(.template)
            .resizable()
            .scaledToFit()
            .foregroundStyle(FocusLabTheme.actionPrimary)
            .frame(width: size, height: size)
            .accessibilityHidden(true)
    }
}

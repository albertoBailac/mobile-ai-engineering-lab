import SwiftUI

struct FocusLoopMark: View {
    var size: CGFloat = 48

    var body: some View {
        Image("FocusLoop")
            .renderingMode(.template)
            .resizable()
            .scaledToFit()
            .foregroundStyle(Color("ActionPrimary"))
            .frame(width: size, height: size)
            .accessibilityHidden(true)
    }
}

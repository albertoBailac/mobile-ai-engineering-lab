import SwiftUI

#Preview("Focus Loop Light") {
    FocusLoopMarkScalePreview()
        .preferredColorScheme(.light)
}

#Preview("Focus Loop Dark") {
    FocusLoopMarkScalePreview()
        .preferredColorScheme(.dark)
}

private struct FocusLoopMarkScalePreview: View {
    var body: some View {
        HStack(alignment: .center, spacing: 24) {
            FocusLoopMark(size: 16)
            FocusLoopMark(size: 24)
            FocusLoopMark(size: 48)
        }
        .padding(24)
        .background(Color(.systemBackground))
    }
}

import SwiftUI

#Preview("Idle Light") {
    FocusScreen(state: FocusState(), onAction: { _ in })
        .preferredColorScheme(.light)
}

#Preview("Idle Dark") {
    FocusScreen(state: FocusState(), onAction: { _ in })
        .preferredColorScheme(.dark)
}

#Preview("Running Light") {
    FocusScreen(
        state: FocusState(remainingMilliseconds: 17 * 60 * 1_000 + 42 * 1_000, status: .running),
        onAction: { _ in }
    )
    .preferredColorScheme(.light)
}

#Preview("Running Dark") {
    FocusScreen(
        state: FocusState(remainingMilliseconds: 17 * 60 * 1_000 + 42 * 1_000, status: .running),
        onAction: { _ in }
    )
    .preferredColorScheme(.dark)
}

#Preview("Paused Light") {
    FocusScreen(
        state: FocusState(duration: .long, remainingMilliseconds: 31 * 60 * 1_000 + 15 * 1_000, status: .paused),
        onAction: { _ in }
    )
    .preferredColorScheme(.light)
}

#Preview("Paused Dark") {
    FocusScreen(
        state: FocusState(duration: .long, remainingMilliseconds: 31 * 60 * 1_000 + 15 * 1_000, status: .paused),
        onAction: { _ in }
    )
    .preferredColorScheme(.dark)
}

#Preview("Completed Light") {
    FocusScreen(
        state: FocusState(remainingMilliseconds: 0, status: .completed),
        onAction: { _ in }
    )
    .preferredColorScheme(.light)
}

#Preview("Completed Dark") {
    FocusScreen(
        state: FocusState(remainingMilliseconds: 0, status: .completed),
        onAction: { _ in }
    )
    .preferredColorScheme(.dark)
}

#Preview(
    "Compact Portrait 320x568",
    traits: .fixedLayout(width: 320, height: 568)
) {
    FocusScreen(state: FocusState(), onAction: { _ in })
        .preferredColorScheme(.light)
}

#Preview(
    "Compact Landscape 667x375",
    traits: .fixedLayout(width: 667, height: 375)
) {
    FocusScreen(
        state: FocusState(remainingMilliseconds: 12 * 60 * 1_000, status: .running),
        onAction: { _ in }
    )
    .preferredColorScheme(.dark)
}

#Preview(
    "Regular iPad Portrait 834x1194",
    traits: .fixedLayout(width: 834, height: 1_194)
) {
    FocusScreen(
        state: FocusState(duration: .long, remainingMilliseconds: 35 * 60 * 1_000, status: .paused),
        onAction: { _ in }
    )
    .preferredColorScheme(.light)
}

#Preview(
    "Regular iPad Landscape 1194x834",
    traits: .fixedLayout(width: 1_194, height: 834)
) {
    FocusScreen(
        state: FocusState(remainingMilliseconds: 0, status: .completed),
        onAction: { _ in }
    )
    .preferredColorScheme(.dark)
}

#Preview(
    "Accessibility Compact 390x844",
    traits: .fixedLayout(width: 390, height: 844)
) {
    FocusScreen(
        state: FocusState(duration: .long, remainingMilliseconds: 28 * 60 * 1_000, status: .paused),
        onAction: { _ in }
    )
    .dynamicTypeSize(.accessibility3)
    .preferredColorScheme(.light)
}

#Preview(
    "Folded-like Approximation 390x844",
    traits: .fixedLayout(width: 390, height: 844)
) {
    FocusScreen(
        state: FocusState(remainingMilliseconds: 20 * 60 * 1_000, status: .running),
        onAction: { _ in }
    )
    .preferredColorScheme(.dark)
}

#Preview(
    "Expanded-like Approximation 780x844",
    traits: .fixedLayout(width: 780, height: 844)
) {
    FocusScreen(
        state: FocusState(remainingMilliseconds: 20 * 60 * 1_000, status: .running),
        onAction: { _ in }
    )
    .preferredColorScheme(.dark)
}

#Preview("Dynamic Type Accessibility 3") {
    FocusScreen(
        state: FocusState(remainingMilliseconds: 20 * 60 * 1_000, status: .running),
        onAction: { _ in }
    )
    .environment(\.dynamicTypeSize, .accessibility3)
}

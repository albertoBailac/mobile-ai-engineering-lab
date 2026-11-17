import SwiftUI

enum FocusLabTheme {
    static let canvas = Color("Canvas")
    static let surfacePrimary = Color("SurfacePrimary")
    static let surfaceSecondary = Color("SurfaceSecondary")
    static let contentPrimary = Color("ContentPrimary")
    static let contentSecondary = Color("ContentSecondary")
    static let actionPrimary = Color("ActionPrimary")
    static let actionOnPrimary = Color("ActionOnPrimary")
    static let actionContainer = Color("ActionContainer")
    static let actionOnContainer = Color("ActionOnContainer")
    static let statusIdle = Color("StatusIdle")
    static let statusRunning = Color("StatusRunning")
    static let statusPaused = Color("StatusPaused")
    static let statusCompleted = Color("StatusCompleted")
    static let borderSubtle = Color("BorderSubtle")
    static let focusIndicator = Color("FocusIndicator")
    static let error = Color("Error")

    enum Metrics {
        static let compactGutter: CGFloat = 24
        static let regularGap: CGFloat = 56
        static let compactMaxWidth: CGFloat = 520
        static let expandedBreakpoint: CGFloat = 680
        static let expandedMaxWidth: CGFloat = 920
        static let timerCornerRadius: CGFloat = 28
    }
}

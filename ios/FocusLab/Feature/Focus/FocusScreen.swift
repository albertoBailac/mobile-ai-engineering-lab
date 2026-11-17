import Accessibility
import SwiftUI

@MainActor
struct FocusScreen: View {
    let state: FocusState
    let onAction: (FocusAction) -> Void

    @Environment(\.dynamicTypeSize) private var dynamicTypeSize
    @ScaledMetric(relativeTo: .largeTitle) private var timerTextSize: CGFloat = 56

    var body: some View {
        ZStack {
            FocusLabTheme.canvas
                .ignoresSafeArea()

            GeometryReader { geometry in
                let availableWidth = max(
                    geometry.size.width - (FocusLabTheme.Metrics.compactGutter * 2),
                    0
                )

                ScrollView {
                    adaptiveContent(availableWidth: availableWidth)
                        .padding(.horizontal, FocusLabTheme.Metrics.compactGutter)
                        .padding(.vertical, 40)
                        .frame(maxWidth: .infinity)
                }
            }
        }
        .foregroundStyle(FocusLabTheme.contentPrimary)
        .onChange(of: state.status) { _, status in
            if status == .completed {
                AccessibilityNotification.Announcement(
                    String(localized: "Session complete")
                ).post()
            }
        }
    }

    private func adaptiveContent(availableWidth: CGFloat) -> some View {
        let usesRegularLayout = availableWidth >= FocusLabTheme.Metrics.expandedBreakpoint
        let layout = usesRegularLayout
            ? AnyLayout(HStackLayout(alignment: .center, spacing: FocusLabTheme.Metrics.regularGap))
            : AnyLayout(VStackLayout(spacing: 0))
        let sectionWidth = usesRegularLayout
            ? min((availableWidth - FocusLabTheme.Metrics.regularGap) / 2, 432)
            : min(availableWidth, FocusLabTheme.Metrics.compactMaxWidth)

        return layout {
            sessionConfiguration
                .frame(maxWidth: .infinity)

            timerAndActions(
                availableWidth: sectionWidth,
                ringSize: usesRegularLayout ? 240 : 220
            )
            .padding(.top, usesRegularLayout ? 0 : 48)
            .frame(maxWidth: .infinity)
        }
        .frame(
            maxWidth: usesRegularLayout
                ? FocusLabTheme.Metrics.expandedMaxWidth
                : FocusLabTheme.Metrics.compactMaxWidth
        )
    }

    private var sessionConfiguration: some View {
        VStack(spacing: 0) {
            FocusLoopMark()

            Text("FOCUSLAB")
                .font(.caption.weight(.semibold))
                .tracking(1.6)
                .foregroundStyle(FocusLabTheme.actionPrimary)
                .padding(.top, 8)

            Text("Focus timer")
                .font(.largeTitle.bold())
                .multilineTextAlignment(.center)
                .padding(.top, 16)

            Text("Choose a session and protect your attention.")
                .font(.body)
                .foregroundStyle(FocusLabTheme.contentSecondary)
                .multilineTextAlignment(.center)
                .padding(.top, 8)

            Picker("Session duration", selection: durationBinding) {
                ForEach(FocusDuration.allCases, id: \.self) { duration in
                    Text(duration.label).tag(duration)
                }
            }
            .pickerStyle(.segmented)
            .tint(FocusLabTheme.actionPrimary)
            .disabled(state.status == .running)
            .accessibilityIdentifier("durationPicker")
            .padding(.top, 32)
        }
    }

    private func timerAndActions(
        availableWidth: CGFloat,
        ringSize: CGFloat
    ) -> some View {
        VStack(spacing: 0) {
            timerDisplay(ringSize: ringSize)

            statusIndicator
                .padding(.top, 24)

            actionButtons(availableWidth: max(availableWidth - 48, 0))
                .padding(.top, 28)
        }
        .padding(24)
        .background(
            FocusLabTheme.surfacePrimary,
            in: RoundedRectangle(
                cornerRadius: FocusLabTheme.Metrics.timerCornerRadius,
                style: .continuous
            )
        )
        .overlay {
            RoundedRectangle(
                cornerRadius: FocusLabTheme.Metrics.timerCornerRadius,
                style: .continuous
            )
            .stroke(FocusLabTheme.borderSubtle.opacity(0.45), lineWidth: 1)
        }
    }

    @ViewBuilder
    private func timerDisplay(ringSize: CGFloat) -> some View {
        let usesAccessibleLayout = dynamicTypeSize.isAccessibilitySize

        Group {
            if usesAccessibleLayout {
                VStack(spacing: 16) {
                    progressGauge(ringSize: ringSize)
                    timerText
                }
            } else {
                ZStack {
                    progressGauge(ringSize: ringSize)
                    timerText
                        .minimumScaleFactor(0.6)
                        .frame(width: ringSize * 0.56)
                }
            }
        }
        .accessibilityElement(children: .ignore)
        .accessibilityLabel("Focus timer")
        .accessibilityValue(state.spokenRemainingTime)
        .accessibilityIdentifier("timerGauge")
    }

    private func progressGauge(ringSize: CGFloat) -> some View {
        Gauge(value: state.progress) {
            EmptyView()
        }
        .gaugeStyle(.accessoryCircularCapacity)
        .tint(FocusLabTheme.actionPrimary)
        .controlSize(.extraLarge)
        .scaleEffect(ringSize / 76)
        .frame(width: ringSize, height: ringSize)
        .accessibilityHidden(true)
    }

    private var timerText: some View {
        Text(state.formattedTime)
            .font(.system(size: timerTextSize, weight: .semibold, design: .rounded))
            .monospacedDigit()
            .lineLimit(1)
            .accessibilityHidden(true)
    }

    private var statusIndicator: some View {
        HStack(spacing: 8) {
            Image(systemName: statusSymbol)
                .accessibilityHidden(true)

            Text(statusLabel)
                .font(.headline)
                .accessibilityIdentifier("statusLabel")
        }
        .foregroundStyle(statusColor)
    }

    private var durationBinding: Binding<FocusDuration> {
        Binding(
            get: { state.duration },
            set: { onAction(.selectDuration($0)) }
        )
    }

    private var statusLabel: LocalizedStringKey {
        switch state.status {
        case .idle: "Ready to focus"
        case .running: "Focus in progress"
        case .paused: "Session paused"
        case .completed: "Session complete"
        }
    }

    private var statusSymbol: String {
        switch state.status {
        case .idle: "timer"
        case .running: "play.fill"
        case .paused: "pause.fill"
        case .completed: "checkmark.circle.fill"
        }
    }

    private var statusColor: Color {
        switch state.status {
        case .idle: FocusLabTheme.statusIdle
        case .running: FocusLabTheme.statusRunning
        case .paused: FocusLabTheme.statusPaused
        case .completed: FocusLabTheme.statusCompleted
        }
    }

    private func actionButtons(availableWidth: CGFloat) -> some View {
        let usesVerticalActions = dynamicTypeSize.isAccessibilitySize || availableWidth < 300
        let layout = usesVerticalActions
            ? AnyLayout(VStackLayout(spacing: 12))
            : AnyLayout(HStackLayout(spacing: 12))

        return layout {
            switch state.status {
            case .idle:
                primaryButton("Start", identifier: "startButton", action: .start)
            case .running:
                primaryButton("Pause", identifier: "pauseButton", action: .pause)
            case .paused:
                primaryButton("Resume", identifier: "resumeButton", action: .resume)
            case .completed:
                EmptyView()
            }

            if state.status != .idle {
                Button("Reset") {
                    onAction(.reset)
                }
                .buttonStyle(.bordered)
                .tint(FocusLabTheme.actionPrimary)
                .accessibilityIdentifier("resetButton")
            }
        }
        .controlSize(.large)
    }

    private func primaryButton(
        _ title: LocalizedStringKey,
        identifier: String,
        action: FocusAction
    ) -> some View {
        Button(title) {
            onAction(action)
        }
        .buttonStyle(.borderedProminent)
        .tint(FocusLabTheme.actionPrimary)
        .foregroundStyle(FocusLabTheme.actionOnPrimary)
        .accessibilityIdentifier(identifier)
    }
}

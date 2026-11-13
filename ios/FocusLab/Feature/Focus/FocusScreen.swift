import Accessibility
import SwiftUI

@MainActor
struct FocusScreen: View {
    let state: FocusState
    let onAction: (FocusAction) -> Void

    @Environment(\.dynamicTypeSize) private var dynamicTypeSize

    var body: some View {
        GeometryReader { geometry in
            let availableWidth = max(geometry.size.width - 48, 0)

            ScrollView {
                adaptiveContent(availableWidth: availableWidth)
                    .padding(.horizontal, 24)
                    .padding(.vertical, 40)
                    .frame(maxWidth: .infinity)
            }
            .background(Color(.systemBackground))
        }
        .onChange(of: state.status) { _, status in
            if status == .completed {
                AccessibilityNotification.Announcement(
                    String(localized: "Session complete")
                ).post()
            }
        }
    }

    private func adaptiveContent(availableWidth: CGFloat) -> some View {
        let usesRegularLayout = availableWidth >= 680
        let layout = usesRegularLayout
            ? AnyLayout(HStackLayout(alignment: .center, spacing: 56))
            : AnyLayout(VStackLayout(spacing: 0))
        let sectionWidth = usesRegularLayout
            ? min((availableWidth - 56) / 2, 402)
            : min(availableWidth, 520)

        return layout {
            sessionConfiguration
                .frame(maxWidth: .infinity)

            timerAndActions(
                availableWidth: sectionWidth,
                topPadding: usesRegularLayout ? 0 : 48
            )
            .frame(maxWidth: .infinity)
        }
        .frame(maxWidth: usesRegularLayout ? 860 : 520)
    }

    private var sessionConfiguration: some View {
        VStack(spacing: 0) {
            Text("Focus timer")
                .font(.largeTitle.bold())
                .multilineTextAlignment(.center)

            Text("Choose a session and protect your attention.")
                .font(.body)
                .foregroundStyle(.secondary)
                .multilineTextAlignment(.center)
                .padding(.top, 8)

            Picker("Session duration", selection: durationBinding) {
                ForEach(FocusDuration.allCases, id: \.self) { duration in
                    Text(duration.label).tag(duration)
                }
            }
            .pickerStyle(.segmented)
            .disabled(state.status == .running)
            .accessibilityIdentifier("durationPicker")
            .padding(.top, 32)
        }
    }

    private func timerAndActions(
        availableWidth: CGFloat,
        topPadding: CGFloat
    ) -> some View {
        VStack(spacing: 0) {
            Gauge(value: state.progress) {
                Text("Focus progress")
            }
            .gaugeStyle(.accessoryCircularCapacity)
            .tint(.accentColor)
            .controlSize(.extraLarge)
            .scaleEffect(2.4)
            .frame(width: 180, height: 180)
            .accessibilityLabel("Focus timer")
            .accessibilityValue(state.spokenRemainingTime)
            .accessibilityIdentifier("timerGauge")
            .padding(.top, topPadding)

            Text(state.formattedTime)
                .font(.system(.largeTitle, design: .rounded, weight: .semibold))
                .monospacedDigit()
                .accessibilityHidden(true)
                .padding(.top, 24)

            Text(statusLabel)
                .font(.headline)
                .foregroundStyle(.secondary)
                .accessibilityIdentifier("statusLabel")
                .padding(.top, 28)

            actionButtons(availableWidth: availableWidth)
                .padding(.top, 28)
        }
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
        .accessibilityIdentifier(identifier)
    }
}

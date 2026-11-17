# FocusLab design system

## Purpose and scope

This document defines the approved **Calm precision** visual direction for FocusLab. It is the source of truth for the reusable visual language that Android Compose and iOS SwiftUI will implement in later deliveries.

This delivery does not integrate resources into either application. It also keeps two concerns deliberately separate:

- **Design tokens** describe reusable interface intent: canvas, content, actions, spacing, typography, shape, layout, and accessibility.
- **Brand assets** identify FocusLab: the Focus Loop master symbol and, in a later delivery, separately designed application icons.

The Focus Loop symbol is not an AppIcon. Application icons need their own composition, optical padding, background, platform variants, and validation.

## Approved visual direction

Calm precision uses cool neutrals, a restrained indigo accent, generous space, and a circular session motif. The timer is the visual focus, while controls remain recognizably native to each platform.

The hierarchy is intentionally simple:

1. FocusLab identity and screen purpose.
2. Session duration.
3. Remaining time and progress.
4. Session status.
5. Primary and secondary actions.

Android retains Material 3 interaction conventions. iOS retains SwiftUI controls, Dynamic Type, SF Symbols, and platform feedback. Cross-platform consistency means shared intent and hierarchy, not pixel parity.

## Semantic color tokens

Token names describe meaning rather than a particular hue. Values are specified in sRGB.

| Token | Light | Dark | Responsibility |
| --- | --- | --- | --- |
| `color.canvas` | `#F7F8FC` | `#101118` | Screen background |
| `color.surface.primary` | `#FFFFFF` | `#191B24` | Main cards and grouped regions |
| `color.surface.secondary` | `#EEF0F7` | `#242734` | Progress tracks and quiet selections |
| `color.content.primary` | `#181B25` | `#F2F3FA` | Primary text and essential icons |
| `color.content.secondary` | `#565E70` | `#C5C8D4` | Supporting text and inactive content |
| `color.action.primary` | `#4F46E5` | `#C5C4FF` | Primary action and running state |
| `color.action.onPrimary` | `#FFFFFF` | `#211A69` | Content on the primary action |
| `color.action.container` | `#E5E7FF` | `#373281` | Selected and softly emphasized surfaces |
| `color.action.onContainer` | `#272160` | `#E4E3FF` | Content on the action container |
| `color.status.idle` | `#565E70` | `#C5C8D4` | Ready state |
| `color.status.running` | `#4F46E5` | `#C5C4FF` | Active session |
| `color.status.paused` | `#9A4D00` | `#FFB775` | Paused session |
| `color.status.completed` | `#176B4D` | `#73D5A9` | Completed session |
| `color.border.subtle` | `#747C8E` | `#8E92A1` | Essential boundaries and dividers |
| `color.focus.indicator` | `#4F46E5` | `#C5C4FF` | Keyboard and accessibility focus |
| `color.error` | `#B3261E` | `#FFB4AB` | Future validation or error feedback |

Status is always communicated through text and an icon in addition to color:

- Idle: timer icon and “Ready to focus”.
- Running: progress/play icon and “Focus in progress”.
- Paused: pause icon and “Session paused”.
- Completed: check icon and “Session complete”.

### Android color policy

The fixed FocusLab palette is the default and the source of truth for previews, screenshots, and cross-platform review.

Material You may later remain available only as an internal experimental theme parameter, defaulting to off. In that experiment, Material dynamic colors may resolve canvas, surface, content, and primary-action roles. FocusLab status colors remain explicit and must be contrast-checked against the resolved surfaces. The Focus Loop symbol is not recolored by the wallpaper, and no user-facing preference or persistence is added as part of the visual redesign.

## Typography

FocusLab uses system fonts. This preserves native rendering, language coverage, Android font scaling, and iOS Dynamic Type without adding font assets or licenses.

| Semantic role | Android intent | iOS intent |
| --- | --- | --- |
| `type.screenTitle` | 32sp / 40sp, bold | `.largeTitle.bold()` |
| `type.body` | 16sp / 24sp, regular | `.body` |
| `type.timer` | 56sp / 64sp, semibold, tabular figures | Rounded system style with `.monospacedDigit()` |
| `type.status` | 16sp / 24sp, semibold | `.headline` |
| `type.control` | Material label large | Native control label |
| `type.supporting` | 14sp / 20sp, regular | `.subheadline` |

The timer must not change width as digits update. It remains text, not an image, and scales with the user's text settings. Platform implementations may cap only decorative scale; essential time and actions must remain available through reflow and scrolling.

## Spacing, shape, and size

The base spacing scale is `4, 8, 12, 16, 24, 32, 48, 64`.

| Token | Value | Use |
| --- | ---: | --- |
| `space.1` | 4 | Fine optical adjustment |
| `space.2` | 8 | Closely related text |
| `space.3` | 12 | Icon-label and control gaps |
| `space.4` | 16 | Standard component padding |
| `space.6` | 24 | Compact screen gutters |
| `space.8` | 32 | Section separation |
| `space.12` | 48 | Major compact separation |
| `space.16` | 64 | Expanded breathing room |

Shape roles are 12 for controls, 20 for grouped surfaces, and 28 for a visually dominant timer surface. Platform-native controls keep their native shape when replacing it would reduce familiarity.

Minimum interaction targets are 48dp on Android and 44pt on iOS. Functional icons are normally 20–24 units. The Focus Loop header mark is normally 40–48 units. The progress ring is approximately 220 units in compact layouts and may grow to 240 in expanded layouts.

## Layouts

### Compact: one column

- Always scrollable and safe-area aware.
- 24-unit horizontal gutter and a content width no greater than 520.
- Identity and introduction precede duration selection.
- Remaining time is centered inside the progress ring.
- Status follows the ring as one text-and-icon group.
- Actions may share a row at normal text sizes, but stack vertically for accessibility text or less than 300 units of available width.
- Duration selection remains disabled while the timer is running.

```text
             Focus Loop
              FOCUSLAB

            Focus timer
  Choose a session and protect your attention.

          [ 25 min ] [ 50 min ]

        ┌──────────────────────┐
        │       ╭──────╮       │
        │    ╭──╯      ╰──╮    │
        │   │     17:42     │   │
        │    ╰──╮      ╭──╯    │
        │       ╰──────╯       │
        │  ▶ Focus in progress │
        │      Pause  Reset    │
        └──────────────────────┘
```

### Expanded: two regions

- Activated from 680 units of useful content width.
- Overall content width no greater than approximately 920, with a 56-unit inter-region gap.
- The first region contains identity, purpose, and duration.
- The second region contains progress, time, status, and actions.
- Both regions render the same state and dispatch the same actions as the compact composition.

```text
┌──────────────────────┐    ┌────────────────────────┐
│     Focus Loop       │    │       ╭────────╮       │
│      FOCUSLAB        │    │    ╭──╯        ╰──╮    │
│                      │    │   │      17:42      │   │
│     Focus timer      │    │    ╰──╮        ╭──╯    │
│ Choose a session and │    │       ╰────────╯       │
│ protect your         │    │                        │
│ attention.           │    │  ▶ Focus in progress  │
│                      │    │                        │
│ [ 25 min | 50 min ]  │    │    Pause    Reset     │
└──────────────────────┘    └────────────────────────┘
```

## Accessibility

- Normal text meets at least 4.5:1 contrast; large text and essential non-text graphics meet at least 3:1.
- Color is never the only status signal.
- Reading order is identity, title and description, duration, remaining time, status, then actions.
- The timer is exposed as one accessible element with a localized label, remaining-time value, and progress. Duplicate visual time text is hidden from the accessibility tree.
- Completion is announced politely once. Individual timer ticks are not announced.
- Decorative brand marks and icons inside already labeled controls are hidden from assistive technologies.
- Layouts must survive Android font scaling to 200% and iOS accessibility Dynamic Type through reflow and scrolling.
- Verify RTL, increased contrast, grayscale, differentiate-without-color, TalkBack, VoiceOver, Switch Access, and Voice Control.
- No continuous decorative animation is required to understand progress. Any future transition must respect reduced-motion settings.

## Focus Loop master symbol

Focus Loop is a master brand symbol, not a functional icon and not the final application icon.

Its 24×24 coordinate system contains:

- A 325-degree circular arc of radius 8.5 centered at `(12, 12)`.
- A 35-degree opening centered near the two-o'clock direction.
- A 2-unit stroke with round caps.
- A central dot of radius 1.25, representing sustained attention.
- An orbital accent of radius 1.15 on a radius-10 orbit, centered in the opening.
- A minimum 2-unit optical margin inside the viewBox.

The geometry is single-color, symmetric apart from the intentional opening and accent, and contains no text, gradients, filters, masks, shadows, linked resources, or embedded raster data. Consumers apply color through `currentColor` or a platform tint.

## Approved conceptual references and limitation

The approved reference is the written brief developed for this repository: cool indigo and neutral surfaces, generous space, centered timer, compact one-column and expanded two-region compositions, and the Focus Loop geometry described above.

No original mood-board images, AI-generated concept sheets, or licensed external visual references are present in the repository or otherwise accessible for this delivery. Consequently, these files do not claim to reproduce an unavailable image. They implement only the approved conceptual brief and record that limitation explicitly.

## Asset provenance and future conversion

For future AI-assisted explorations, record the tool and model, date, full prompt, parameters, input references, reference authors and licenses, and the human reconstruction performed. Use only owned material or material with a compatible, documented license. Check proposed marks for accidental similarity to existing brands before product use. Generated raster output is a reference, never the vector source of truth.

### Android VectorDrawable integration

1. Convert the master geometry to a 24×24 `VectorDrawable`, preserving the arc, round caps, center point, and accent.
2. Confirm that the arc, round caps, center point, and accent survive conversion at small sizes.
3. Apply `color.action.primary` through Compose tint rather than baking a brand color into the geometry.
4. Design adaptive, round, monochrome, and legacy application icons as a separate exercise. Do not promote the master symbol directly to an AppIcon.

Android does not render arbitrary SVG files as drawable resources. Unsupported SVG features must be flattened or redrawn, which is why the master intentionally uses only an arc and circles.

### iOS vector export

Focus Loop is exported with CairoSVG 2.9.1 in a temporary Python virtual environment. This delivery used Cairo 1.18.4. Neither Python packages nor native libraries are added to the project.

Reproducing the export requires:

- Python 3.10 or later with `venv` and `pip`.
- CairoSVG 2.9.1 installed only inside the temporary virtual environment.
- A compatible native Cairo shared library already installed on the host and discoverable by its dynamic loader. The Python virtual environment does not provide this native dependency.
- Optional Poppler tools (`pdfinfo`, `pdfimages`, and `pdffonts`) for the PDF validation commands below.

Run the commands from the repository root. `cairosvg --version` is a preflight check: if it cannot load Cairo, stop and provision the native dependency according to the host operating system rather than adding it to the project.

The reproducible export commands are:

```bash
CAIROSVG_VENV="${TMPDIR:-/tmp}/focuslab-cairosvg-2.9.1"
python3 -m venv "$CAIROSVG_VENV"
"$CAIROSVG_VENV/bin/python" -m pip install "CairoSVG==2.9.1"
"$CAIROSVG_VENV/bin/cairosvg" --version
"$CAIROSVG_VENV/bin/cairosvg" \
  --format pdf \
  --dpi 72 \
  --output ios/FocusLab/Resources/Assets.xcassets/FocusLoop.imageset/FocusLoop.pdf \
  docs/design/assets/focus-loop-master.svg

pdfinfo ios/FocusLab/Resources/Assets.xcassets/FocusLoop.imageset/FocusLoop.pdf
pdfimages -list ios/FocusLab/Resources/Assets.xcassets/FocusLoop.imageset/FocusLoop.pdf
pdffonts ios/FocusLab/Resources/Assets.xcassets/FocusLoop.imageset/FocusLoop.pdf
```

If the host has Cairo in a non-standard location, configure that host's dynamic loader for the command without writing the machine-specific library path into the repository. For example, macOS may require a temporary `DYLD_LIBRARY_PATH`, while Linux commonly uses `LD_LIBRARY_PATH`. A DPI of 72 maps each SVG user unit to one PDF point, producing the required 24×24-point media box.

The resulting asset is configured as a single-scale template image with vector preservation enabled. SF Symbols remain the source for standard actions, and Focus Loop is reserved for identity. Any, Dark, and Tinted application icon compositions will be designed and validated separately.

A vector PDF asset is compiled by Xcode for image use; it is not a runtime PDF document. App icons have different padding, background, transparency, appearance, and small-size requirements from an in-app brand symbol.

## Visual validation

`focus-loop-validation.svg` renders the geometry at actual 16, 24, 48, 128, and 1024 user-unit dimensions in three treatments:

- Light: indigo `#4F46E5` on canvas `#F7F8FC`.
- Dark: pale indigo `#C5C4FF` on canvas `#101118`.
- Monochrome: black on white.

The 16-unit rendering is the critical minimum. At ordinary 1× rasterization its 2-unit master stroke becomes approximately 1.33 pixels, the center dot diameter becomes approximately 1.67 pixels, and the orbital accent diameter becomes approximately 1.53 pixels. Rasterizers may antialias these features differently or soften them on a non-integer pixel grid. The symbol must therefore be inspected on target devices after platform conversion; if either dot becomes ambiguous, the small optical-size export may increase those two radii without changing the master geometry.

The 1024-unit rendering validates smooth geometry but does not constitute an application icon design or App Store/Play Store preview.

### Initial validation result

The validation sheet was rasterized at a one-user-unit-to-one-output-pixel scale with macOS Quick Look. In the light, dark, and monochrome treatments, the open ring, center point, and orbital accent remained separately recognizable at 16 pixels. The 24-, 48-, and 128-pixel samples retained clear spacing, and the 1024-pixel samples showed smooth curves without clipping.

At 16 pixels, both dots are nevertheless close to a single effective raster pixel and look visibly more delicate than the ring. This is an accepted master-symbol limitation, not proof that every platform export will be equally legible. Android VectorDrawable and Xcode PDF rasterization must be checked independently in the later integration delivery, with optical small-size variants considered only if a target renderer loses either dot.

### Native integration validation

The Android `VectorDrawable` and iOS vector PDF preserve the 24×24 master geometry and are previewed at 16, 24, and 48 dp/pt in light and dark appearances. The iOS PDF has one 24×24-point page and contains neither embedded raster images nor fonts. At a 16-pixel rasterization, both dots remain separately recognizable, but antialiasing makes them visibly lighter than at 24 or 48 pixels; this remains the accepted minimum-size limitation.

`color.action.primary` measures 5.92:1 against the approved light canvas and 11.40:1 against the approved dark canvas. On the Android device used for validation, the dark dynamic surface was sampled as `#130D09`, giving the dark token 11.67:1 contrast. Because Material You surfaces vary with the user's wallpaper, each experimental dynamic-color surface must still be checked independently; the fixed FocusLab palette remains the default design direction.

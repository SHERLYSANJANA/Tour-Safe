# TourSafe

**Your travel safety companion.**

TourSafe is a native Android app that helps travelers see nearby disaster alerts, find emergency services and safe places, and prepare an emergency message to share with people they trust.

> **TourSafe supports access to safety information. It does not replace official emergency services.** Always follow local official instructions and contact the local emergency number directly when you need help.

---

## Highlights

- **Emergency access everywhere:** a dedicated SOS tab in the bottom bar and a prominent SOS button on Home.
- **Review before you share:** SOS never sends, shares, or dials silently. You review the message and location first, and the app hands off to your SMS or phone app.
- **Honest data:** live information comes from public sources and is attributed. Sample data is clearly labelled as demonstration content.
- **Eight languages**, with native names in the language selector.
- **Light and dark themes**, following the system or set manually in Settings.

## Features

| Feature | What it does |
|---|---|
| **Home** | Current location and status, SOS button, quick actions, active alerts, and a short safety tip. |
| **Emergency SOS** | Typed or Voice SOS with an editable message, optional GPS coordinates, and a preview before hand-off to SMS or the dialer. |
| **Voice SOS** | Converts speech to text for review. Falls back to typing when speech recognition is unavailable. |
| **Disaster alerts** | Alerts with severity levels, affected area, and safety guidance, with source attribution. |
| **Map** | Shows your position and nearby emergency services and shelters. Directions open in an installed maps app. |
| **Safe places** | Browse shelters and accommodation, with distance and verification status shown only where known. |
| **Check-in** | Prepare a safety status message for your emergency contacts. |
| **Profile and settings** | Name, emergency contacts, language, theme, notifications, location permission shortcut, and demo mode. |

## Data Sources

| Data | Source |
|---|---|
| Earthquakes | [USGS](https://earthquake.usgs.gov/) |
| Global disaster alerts | [GDACS](https://www.gdacs.org/) |
| Hospitals, police stations, shelters | [OpenStreetMap](https://www.openstreetmap.org/copyright) via the Overpass API (© OpenStreetMap contributors, ODbL) |

**Demo mode** shows simulated shelters and alerts for testing. It is **on by default in debug builds** and can be switched in Settings. Demo content is always labelled and must not be treated as real.

## Safety Principles

TourSafe is built around honesty in emergencies:

- It never claims an SMS was delivered when it only opened the messaging app.
- It never claims a call was completed when it only opened the dialer.
- It does not show a safety score or call an area "safe" without a trustworthy source.
- A calculated distance does not mean a place is real, open, safe, or available.
- If GPS is unavailable, it says so and still lets the user prepare a message without coordinates.
- Permissions are requested explicitly, and a text fallback is always available.

## Tech Stack

| | |
|---|---|
| Language | Kotlin 2.0.0 |
| UI | Jetpack Compose (BOM 2024.06.00), Material 3 |
| Architecture | MVVM with `StateFlow` ViewModels and a repository layer |
| Navigation | Navigation Compose 2.7.7 |
| Location | Google Play Services Location 21.3.0 |
| Build | Gradle 8.7 (Kotlin DSL), Android Gradle Plugin 8.5.1 |
| Android | `minSdk` 26, `targetSdk` / `compileSdk` 34 |

## Getting Started

### Prerequisites

- Android Studio recent enough for AGP 8.5.1
- JDK 17 (the version bundled with Android Studio works)
- Android SDK Platform 34
- An emulator or a physical Android device (Android 8.0+)

### Run the app

```bash
git clone https://github.com/SHERLYSANJANA/Tour-Safe.git
cd Tour-Safe
```

1. Open the folder in Android Studio and let Gradle sync finish.
2. Select a device and press **Run** on the `app` configuration.

Or from the command line:

```bash
./gradlew installDebug
```

### Run the tests

```bash
./gradlew test
```

The JVM test suite covers the SOS message builder, location and geo math, the live-data parsers (USGS, GDACS, Overpass), map markers, demo mode, and string-key parity across all eight locales.

## Permissions

| Permission | Why |
|---|---|
| `ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION` | Show your position, calculate distances, and optionally include coordinates in an emergency message. |
| `RECORD_AUDIO` | Voice SOS speech-to-text. Speech processing depends on the device's recognition service. |
| `INTERNET` | Fetch live alerts and nearby places from the public sources above. |

Location is never shared automatically. You review it before anything is handed off.

## Languages

| Language | Native name |
|---|---|
| English | English |
| Spanish | Español |
| French | Français |
| German | Deutsch |
| Simplified Chinese | 简体中文 |
| Japanese | 日本語 |
| Thai | ไทย |
| Hindi | हिन्दी |

All UI text lives in localized string resources, and a test checks that every locale has every key.

## Project Structure

```
app/src/main/java/com/toursafe/
├── data/
│   ├── models/        Alert, MapMarker, Accommodation, EmergencyContact, ...
│   ├── remote/        HTTP client, USGS / GDACS / Overpass parsers, TTL cache
│   ├── repository/    SafetyRepository, LiveSafetyRepository, LoadStatus
│   └── mock/          Clearly labelled demo data
├── navigation/        Screen routes and NavGraph
├── ui/
│   ├── theme/         Color, Theme, Type, Dimens (design tokens)
│   ├── components/    Shared composables (SOS button, alert card, status panels, ...)
│   └── screens/       Home, Map, Alerts, Safe places, SOS, Check-in, Settings, ...
├── util/              Location, geo math, locale, speech recognition helpers
└── viewmodels/        StateFlow ViewModels
```

## Design System

- **Theme:** light-first forest-green Material 3 scheme with a matching dark scheme (`ui/theme`).
- **Colors:** read them through `MaterialTheme.colorScheme` or `MaterialTheme.tourSafe` (safe, warning, info, service, user location). Do not hardcode colors in screens.
- **Red** is reserved for emergency actions and critical warnings. It is not a decorative brand color.
- **Tokens:** spacing and touch-target sizes live in `ui/theme/Dimens.kt`.
- **Shared components:** `EmergencySosButton`, `SectionHeader`, `QuickActionButton`, `StatusBanner`, `AlertCard`, `DataStatusPanel`.
- **Strings:** every UI string must exist in all eight locales.

## Known Limitations

- The map is a simplified custom canvas, not full map tiles. Directions open in an external maps app.
- Alert and place filters, a Privacy section, and an About section are not yet implemented.
- Live data depends on the public services above. When they are offline or have nothing nearby, the app shows an unavailable or empty state instead of inventing content.
- Opening an SMS or phone app is a hand-off. TourSafe cannot confirm delivery or call completion.
- Layouts in every language have not yet been fully tested for text overflow on small screens and large font sizes.

## Development

1. Create a feature branch for each piece of work.
2. Keep changes small and focused.
3. Build and run `./gradlew test` before opening a pull request.
4. Manually test permissions (granted, denied, GPS off), language switching, light and dark themes, large font sizes, and offline behavior.
5. Review changes before merging into `main`.
6. Never commit secrets, tokens, signing keys, or machine-specific configuration.

```bash
git checkout -b feature/your-feature
```

## Contributing

Contributions, suggestions, and bug reports are welcome. When reporting a bug, include:

- A clear description of the issue
- Steps to reproduce it
- Expected and actual behavior
- Device, Android version, app language, and any error messages

Please do not include API secrets, access tokens, or private information such as real locations in issues or commits.

## License

No license has been specified yet. Add one before presenting the project as open source or redistributing it.

## Acknowledgements

Alert and place data come from USGS, GDACS, and OpenStreetMap contributors. Thank you to these projects for making public safety data available.

---

**Built to make travel safety information easier to reach when it matters.**

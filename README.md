# TourSafe 🛡️

**Your travel safety companion.**

TourSafe is an Android application designed to help travelers access safety information, locate nearby emergency assistance, and prepare emergency messages when they need help.

## ✨ Features

- 🌍 **Multilingual Support** — English, Spanish, French, German, Simplified Chinese, Japanese, Thai, and Hindi.
- 📍 **GPS Location** — Access the user's location with permission and calculate distances to nearby places.
- 🗺️ **Interactive Maps** — Help users locate emergency services and safe places.
- 🚨 **Disaster Alerts** — Display safety alerts with severity levels and relevant instructions.
- 🎙️ **Voice SOS** — Convert spoken emergency messages into text for review and sharing.
- 🏥 **Safe Places** — Browse hospitals, police stations, shelters, and other supported emergency locations.
- 🆘 **Emergency Check-ins** — Support safety check-in workflows.
- 🔐 **Privacy-Aware Design** — Handle location and microphone permissions explicitly.

## 🛠️ Tech Stack

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose
- **Design System:** Material 3
- **IDE:** Android Studio
- **Build System:** Gradle with Kotlin DSL
- **Platform:** Android

## 🚀 Getting Started

### Prerequisites

- Android Studio compatible with the project's Gradle configuration
- Android SDK components required by the project
- A compatible JDK
- An Android device or emulator

### Installation

1. Clone this repository:

   ```bash
   git clone <YOUR_GITHUB_REPOSITORY_URL>
   ```

2. Open the project in Android Studio.
3. Allow Gradle synchronization to finish.
4. Connect an Android device or start an emulator.
5. Run the `app` configuration.

Replace `<YOUR_GITHUB_REPOSITORY_URL>` with your repository's actual clone URL.

## 🌐 Supported Languages

TourSafe is designed to support the following languages:

| Language | Native Name |
|---|---|
| English | English |
| Spanish | Español |
| French | Français |
| German | Deutsch |
| Simplified Chinese | 简体中文 |
| Japanese | 日本語 |
| Thai | ไทย |
| Hindi | हिन्दी |

## 📍 Location and Permissions

TourSafe may request location permission to display the user's position and calculate distances to emergency locations.

Voice SOS may use microphone and speech-recognition capabilities to convert spoken messages into text.

- Location access should be requested transparently.
- Users should be able to review location details before sharing them.
- Speech recognition availability and processing behavior depend on the device and configured recognition service.
- The app should provide a text-entry fallback when speech recognition is unavailable.

## 🚨 Emergency Data and Safety

TourSafe is intended to support access to safety information, not replace official emergency services.

- Demo locations and alerts must be clearly identified as sample data.
- A calculated distance does not establish that a location is real, open, safe, or available.
- Emergency locations should be verified using trustworthy sources.
- The app must not claim that an emergency message was delivered unless delivery is confirmed.
- Opening an SMS app or phone dialer does not itself mean that a message was sent or a call was completed.

Always follow official local emergency instructions and contact the appropriate emergency service directly when needed.

## 🔧 Development

To keep development organized:

1. Create a feature branch for new work.
2. Make small, focused changes.
3. Build and test the application regularly.
4. Test location permissions, language switching, speech recognition, and failure scenarios.
5. Review changes before merging them into `main`.
6. Keep secrets, tokens, signing keys, and machine-specific configuration out of source control.

Example:

```bash
git checkout -b feature/your-feature
git status
```

## 🤝 Contributing

Contributions, suggestions, and bug reports are welcome.

When reporting a bug, include:
- A clear description of the issue
- Steps to reproduce it
- Expected behavior
- Actual behavior
- Relevant error messages, if available

Do not include API secrets, access tokens, or private user information in issues or commits.

## 📌 Project Status

TourSafe is under active development. Features, integrations, and emergency-data sources may be incomplete or subject to change.

## 📄 License

No license has been specified yet. A license should be added before the project is presented as open source or redistributed under specific terms.

---

**Built with the goal of making travel safety information easier to access.**

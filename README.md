# Bridge

Open Apple Maps links on Android without getting stuck in the browser.

Bridge catches `maps.apple.com` links and shared Apple Maps text, converts the location into a Google Maps-compatible link, then lets you open it with Google Maps, Waze, or any other maps app of your choosing that handles `geo:` links.

## What It Does

- Opens Apple Maps links from chats, email, browsers, and shared text.
- Converts coordinates, addresses, place searches, and directions destinations.
- Shows Android's app chooser so you can pick Google Maps, Waze, Uber, or another compatible app.
- Includes a manual converter screen for pasting Apple Maps links directly.
- Works without accounts, tracking, analytics, or background services.

## Install

The easiest path is to install the APK from the project's GitHub Releases page:

1. Open the latest release: <https://github.com/stanley-projects/Bridge/releases/latest>
2. Download the APK attached to that release.
3. On your Android device, allow installation from your browser or file manager when prompted.

Android may warn that the APK is from an unknown source. That is expected for apps installed outside the Play Store.

## Set Up Auto-Bridging

For Bridge to open Apple Maps links automatically — without a chooser appearing every time — Android needs your one-time permission. The first time you launch Bridge, you'll see a welcome screen with an **Enable auto-bridging** button. Tap it; Android opens a settings page; switch on `maps.apple.com` under "Supported web addresses". Done — from then on, every Apple Maps link you tap routes silently through Bridge to your preferred maps app.

If you skip the onboarding, the main screen shows a small "Auto-bridging is off — tap to set up" prompt that links to the same settings page. You can run the setup whenever you like.

### Why does Android require this?

Bridge handles links to `maps.apple.com`, a domain it doesn't own. Android requires users to opt in per domain for any app handling third-party links — otherwise a malicious app could silently hijack links to `paypal.com`, `youtube.com`, etc. This is a platform-level security boundary, not a Bridge limitation; every link-handling app of this type works the same way.

## Use

After setup, just tap any Apple Maps link. It opens in your preferred maps app silently.

You can also open Bridge from your launcher and paste a link manually:

```text
https://maps.apple.com/?ll=37.7749,-122.4194&q=San%20Francisco
```

This is useful for the rare case where a link arrives inside a chat app's in-app webview (some messaging apps render links in their own browser and never hand them to Android's intent system).

## Supported Links

Bridge currently understands:

- `ll=latitude,longitude`
- `daddr=latitude,longitude` or `daddr=address`
- `address=...`
- `q=...`
- `saddr=...`
- `/place/<name>` style paths
- Short links or redirect links when the device has network access

## Build From Source

Requirements:

- Android Studio or Android SDK
- JDK 17 or newer

Build a debug APK:

```powershell
.\gradlew.bat assembleDebug
```

The APK will be written to:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Privacy

Bridge does not collect, store, or transmit analytics. It may use the network only to resolve Apple Maps redirect links so the final destination can be parsed.

## Project Page

Visit the project front page at <https://stanley-projects.github.io/Bridge/>.

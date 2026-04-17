# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Purpose

Example Android app (Java, single-module) demonstrating integration of the **Bridgee SDK** (`ai.bridgee:bridgee-android-sdk`) for user attribution / UTM tracking, paired with Firebase Analytics as the `AnalyticsProvider`. The project is intentionally minimal — it exists as a reference implementation, not a full product.

## Common Commands

Gradle wrapper is committed; use it directly. All commands run from the repo root.

- Build debug APK: `./gradlew assembleDebug`
- Build release APK: `./gradlew assembleRelease`
- Install on connected device/emulator: `./gradlew installDebug`
- Clean: `./gradlew clean`
- Lint: `./gradlew lint` (note: `abortOnError false` in `app/build.gradle` — lint never fails the build)
- Run unit tests: `./gradlew test`
- Run a single unit test: `./gradlew :app:testDebugUnitTest --tests "ai.bridgee.androidexampleapp.SomeTest.someMethod"`

There is currently no test source set under `app/src/test` or `app/src/androidTest` — test tasks will be no-ops until tests are added.

## Architecture

Everything non-boilerplate lives in `app/src/main/java/ai/bridgee/androidexampleapp/MainActivity.java`. The flow across that single file is the architecture:

1. `onCreate` obtains a `FirebaseAnalytics` instance, then calls `initializeAnalyticsProvider()` and `initializeBridgeeSDK()` — **in that order**, because the SDK singleton requires the provider.
2. `initializeAnalyticsProvider()` builds an anonymous `AnalyticsProvider` that forwards `logEvent` / `setUserProperty` to Firebase. This is the bridge between the Bridgee SDK and whatever analytics backend the host app uses — swap this implementation to change backends.
3. `initializeBridgeeSDK()` calls `BridgeeSDK.getInstance(context, analyticsProvider, tenantId, tenantKey, dryRun)`. `tenantId` / `tenantKey` are hardcoded placeholders (`"tenant-id"` / `"tenant-key"`) and **must** be replaced for the SDK to work against real infrastructure. `dryRun=true` is the testing mode.
4. On button tap, `callBridgeeFirstOpen()` builds a `MatchBundle` from the form fields (`withName`/`withEmail`/`withPhone`) and invokes `bridgeeSDK.firstOpen(bundle, ResponseCallback<MatchResponse>)`. The callback fires on a background thread — both `ok` and `error` paths wrap UI work in `runOnUiThread`.

Key integration points when modifying:
- To add user fields: extend the `MatchBundle` chain in `callBridgeeFirstOpen` (SDK also exposes `withGclid`, `withCustomParam`).
- To read more attribution data: `MatchResponse` exposes `getUtmSource` / `getUtmMedium` / `getUtmCampaign`; extend `showResponseDialog` accordingly.
- To change analytics backend: replace the body of `initializeAnalyticsProvider` — nothing else needs to change.

## Config & Secrets

- `app/google-services.json` in the repo is a **placeholder**. Replace with a real Firebase config for package `ai.bridgee.androidexampleapp` before building against Firebase. Do not commit the real file.
- Tenant credentials in `MainActivity.initializeBridgeeSDK()` are inline strings. Production apps should move these out of source.

## Build Environment

- AGP 8.5.2, `com.google.gms.google-services` 4.4.0 (see root `build.gradle`).
- `compileSdk` / `targetSdk` 34, `minSdk` 21, Java 8 source/target (`app/build.gradle`).
- Firebase BoM 32.7.0 governs Firebase dep versions; Bridgee SDK pinned to `2.3.0`.

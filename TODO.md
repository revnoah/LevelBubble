# TODO

Project tasks based on the current implementation. Unchecked verification items
describe behavior to investigate, not confirmed defects. Mark tasks complete when
their acceptance criteria have been checked.

## Automated testing

- [ ] Extract sensor filtering, normalization, mode selection, and angle calculations into Kotlin code that can be tested without Android hardware.
  Keep the activity responsible for sensor access and the view responsible for drawing; preserve existing behavior during extraction.
- [ ] Replace the sample arithmetic test in `ExampleUnitTest.kt` with measurement tests.
  Cover level and upright reference vectors, known tilted vectors, signed axis readings, inverted positions, and centering values.
- [ ] Test accelerometer filtering and gravity-vector normalization.
  Cover first-sample initialization, subsequent filtered samples, gravity-sensor input, and zero or near-zero magnitudes; verify finite results.
- [ ] Test surface/plumb mode selection on both sides of the `abs(z) >= 0.82` threshold, including equality and negative Z values.
- [ ] Expand `ExampleInstrumentedTest.kt` beyond the package-name check.
  Add a way to supply controlled sensor readings and verify mode labels, sensor-unavailable behavior, and listener registration/unregistration across activity lifecycle changes.

## Device and IDE verification

- [ ] Run the unit tests and Android Lint in a configured development environment; record and resolve failures.
  Use `./gradlew :app:testDebugUnitTest :app:lintDebug`.
- [ ] Run instrumented tests on a connected device or emulator with `./gradlew :app:connectedDebugAndroidTest`.
- [ ] Compare surface and plumb readings against a physical level or known reference positions.
  Check bubble direction, signed angles, and centering with the phone stationary; record device and case/contact conditions. Displaying two decimal places does not establish accuracy.
- [ ] Verify behavior using both the gravity sensor and accelerometer fallback, plus the no-sensor path.
  Use controlled input where hardware cannot expose a particular configuration.
- [ ] Verify portrait, landscape, and inverted orientations.
  The manifest permits all sensor orientations while calculations use device sensor axes. Check that bubble movement and axis labels match the displayed orientation; remap coordinates if needed.
- [ ] Check small screens, different densities, and system-bar insets for clipped vials or unreadable measurement panels.
- [ ] Verify pause/resume behavior, including moving the phone while the app is paused.
  Confirm readings settle after resuming and the screen stays awake while the app is active.
- [ ] Check for repeated mode switching near the surface/plumb threshold.
  If observed, introduce separate entry and exit thresholds and add regression tests.

## Usability

- [ ] Review accessibility of the custom Canvas interface with TalkBack and larger text settings.
  Add accessible measurement descriptions or equivalent controls where needed, and avoid announcing every sensor update.
- [ ] Evaluate whether manual calibration is needed after collecting reference measurements on devices.
  Document observed offsets and limitations before deciding whether to add a calibration control.

## Documentation

- [ ] Update the README's testing section when meaningful tests and device verification results are available.
  Distinguish automated coverage from physical measurement checks and record remaining limitations.

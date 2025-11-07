# audio-input-route

An Expo module for detecting audio input routes on iOS and Android devices. This module allows you to identify which audio input device is currently being used (built-in microphone, Bluetooth headset, wired headphones, etc.) and listen for audio route changes.

## Installation

```bash
npm install audio-input-route
```

or

```bash
yarn add audio-input-route
```

After installation, rebuild your app:

```bash
npx expo prebuild
npx expo run:ios
# or
npx expo run:android
```

## Features

- 🎤 Detect current audio input route (microphone type)
- 🔄 Listen for audio route change events
- 📱 iOS support with AVAudioSession integration
- 🤖 Android support with AudioManager integration
- 🎧 Support for Bluetooth, wired, and built-in microphones

## Usage

### Get Current Audio Input Route

```javascript
import { getAudioInputRoute } from 'audio-input-route';

const route = await getAudioInputRoute();
console.log(route);
// { portType: "MicrophoneBuiltIn", portName: "iPhone Microphone" }
```

### Listen for Audio Route Changes

```javascript
import { addAudioRouteChangeListener } from 'audio-input-route';

const subscription = addAudioRouteChangeListener(() => {
  console.log('Audio route changed!');
  // Get the new route
  getAudioInputRoute().then(route => {
    console.log('New route:', route);
  });
});

// Don't forget to remove the listener when done
subscription.remove();
```

## API

### `getAudioInputRoute()`

Returns a Promise that resolves to an object containing:
- `portType`: The type of audio input port (e.g., "MicrophoneBuiltIn", "BluetoothHFP", "MicrophoneWired")
- `portName`: The human-readable name of the input device

### `addAudioRouteChangeListener(listener)`

Adds a listener that will be called whenever the audio route changes (e.g., when plugging in headphones or connecting Bluetooth).

Returns a subscription object with a `remove()` method to unsubscribe.

## Common Port Types

### iOS
- `MicrophoneBuiltIn` - Built-in device microphone
- `BluetoothHFP` - Bluetooth hands-free profile
- `BluetoothA2DP` - Bluetooth A2DP
- `MicrophoneWired` - Wired headset microphone
- `HeadsetMic` - Headset microphone

### Android
- `MicrophoneBuiltIn` - Built-in device microphone
- `BluetoothSCO` - Bluetooth SCO (voice calls)
- `BluetoothA2DP` - Bluetooth A2DP (high quality audio)
- `MicrophoneWired` - Wired headset microphone
- `USBDevice` - USB audio device
- `USBHeadset` - USB headset

## Requirements

- iOS 13.0 or higher
- Android 6.0 (API 23) or higher
- Expo SDK 47 or higher
- React Native project with Expo modules

## License

MIT © moritouch

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

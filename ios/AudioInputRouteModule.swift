// MIT License
//
// Copyright (c) 2025 moritouch
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

import ExpoModulesCore
import AVFoundation

public class AudioInputRouteModule: Module {
  public func definition() -> ModuleDefinition {
    Name("AudioInputRoute")

    AsyncFunction("getAudioInputRoute") { () -> [String: String] in
      let session = AVAudioSession.sharedInstance()

      // Check current category
      let currentCategory = session.category
      print("📱 [AudioInputRoute] Current AVAudioSession category: \(currentCategory)")

      // Activate audio session with recording-enabled category
      do {
        // .playAndRecord: Allow both recording and playback
        // .allowBluetoothHFP: Allow Bluetooth HFP (Hands-Free Profile) microphone
        // .defaultToSpeaker: Default to speaker output
        try session.setCategory(
          .playAndRecord,
          mode: .default,
          options: [.allowBluetoothHFP, .defaultToSpeaker]
        )
        try session.setActive(true)
        print("[AudioInputRoute] AVAudioSession activated successfully")
      } catch {
        print("[AudioInputRoute] AVAudioSession activation failed: \(error.localizedDescription)")
        // Continue even on error to check current route
      }

      // Get current route
      let currentRoute = session.currentRoute
      print("[AudioInputRoute] Current route inputs count: \(currentRoute.inputs.count)")

      // Log all input devices (for debugging)
      for (index, input) in currentRoute.inputs.enumerated() {
        print("  Input \(index): \(input.portType.rawValue) - \(input.portName)")
      }

      guard let input = currentRoute.inputs.first else {
        print("[AudioInputRoute] No input devices found")
        return ["portType": "Unknown", "portName": "No Input"]
      }

      print("[AudioInputRoute] Selected input: \(input.portType.rawValue) - \(input.portName)")
      return [
        "portType": input.portType.rawValue,
        "portName": input.portName
      ]
    }

    Events("onAudioRouteChange")

    OnStartObserving {
      NotificationCenter.default.addObserver(
        self,
        selector: #selector(audioRouteChanged),
        name: AVAudioSession.routeChangeNotification,
        object: nil
      )
    }

    OnStopObserving {
      NotificationCenter.default.removeObserver(
        self,
        name: AVAudioSession.routeChangeNotification,
        object: nil
      )
    }
  }

  @objc func audioRouteChanged(notification: Notification) {
    sendEvent("onAudioRouteChange", [:])
  }
}

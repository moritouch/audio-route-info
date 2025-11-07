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

package expo.modules.audioinputroute

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import android.util.Log
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition

class AudioInputRouteModule : Module() {
  private val context: Context
    get() = appContext.reactContext ?: throw IllegalStateException("React context is null")

  private var audioManager: AudioManager? = null
  private var audioDeviceCallback: AudioManager.AudioDeviceCallback? = null

  override fun definition() = ModuleDefinition {
    Name("AudioInputRoute")

    OnCreate {
      audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    AsyncFunction("getAudioInputRoute") {
      getAudioInputRoute()
    }

    Events("onAudioRouteChange")

    OnStartObserving {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        audioDeviceCallback = object : AudioManager.AudioDeviceCallback() {
          override fun onAudioDevicesAdded(addedDevices: Array<out AudioDeviceInfo>?) {
            sendEvent("onAudioRouteChange", emptyMap<String, Any>())
          }

          override fun onAudioDevicesRemoved(removedDevices: Array<out AudioDeviceInfo>?) {
            sendEvent("onAudioRouteChange", emptyMap<String, Any>())
          }
        }
        audioManager?.registerAudioDeviceCallback(audioDeviceCallback, null)
      }
    }

    OnStopObserving {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        audioDeviceCallback?.let {
          audioManager?.unregisterAudioDeviceCallback(it)
        }
        audioDeviceCallback = null
      }
    }

    OnDestroy {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        audioDeviceCallback?.let {
          audioManager?.unregisterAudioDeviceCallback(it)
        }
      }
    }
  }

  private fun getAudioInputRoute(): Map<String, String> {
    val manager = audioManager ?: return mapOf(
      "portType" to "Unknown",
      "portName" to "AudioManager not available"
    )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      // Get all input devices
      val devices = manager.getDevices(AudioManager.GET_DEVICES_INPUTS)
      
      Log.d(TAG, "Found ${devices.size} input devices")
      
      // Log all input devices for debugging
      devices.forEachIndexed { index, device ->
        Log.d(TAG, "Input $index: ${getDeviceTypeName(device.type)} - ${device.productName}")
      }

      // Priority order: Bluetooth > Wired > Built-in
      val prioritizedDevice = devices
        .filter { it.isSource }
        .sortedByDescending { getPriority(it.type) }
        .firstOrNull()

      if (prioritizedDevice != null) {
        val portType = getDeviceTypeName(prioritizedDevice.type)
        val portName = prioritizedDevice.productName?.toString() ?: "Unknown Device"
        
        Log.d(TAG, "Selected input: $portType - $portName")
        
        return mapOf(
          "portType" to portType,
          "portName" to portName
        )
      }
    }

    Log.d(TAG, "No input devices found")
    return mapOf(
      "portType" to "Unknown",
      "portName" to "No Input"
    )
  }

  private fun getPriority(type: Int): Int {
    return when (type) {
      AudioDeviceInfo.TYPE_BLUETOOTH_SCO -> 3
      AudioDeviceInfo.TYPE_BLUETOOTH_A2DP -> 3
      AudioDeviceInfo.TYPE_WIRED_HEADSET -> 2
      AudioDeviceInfo.TYPE_WIRED_HEADPHONES -> 2
      AudioDeviceInfo.TYPE_USB_DEVICE -> 2
      AudioDeviceInfo.TYPE_USB_HEADSET -> 2
      AudioDeviceInfo.TYPE_BUILTIN_MIC -> 1
      else -> 0
    }
  }

  private fun getDeviceTypeName(type: Int): String {
    return when (type) {
      AudioDeviceInfo.TYPE_BUILTIN_MIC -> "MicrophoneBuiltIn"
      AudioDeviceInfo.TYPE_BLUETOOTH_SCO -> "BluetoothSCO"
      AudioDeviceInfo.TYPE_BLUETOOTH_A2DP -> "BluetoothA2DP"
      AudioDeviceInfo.TYPE_WIRED_HEADSET -> "MicrophoneWired"
      AudioDeviceInfo.TYPE_WIRED_HEADPHONES -> "MicrophoneWired"
      AudioDeviceInfo.TYPE_USB_DEVICE -> "USBDevice"
      AudioDeviceInfo.TYPE_USB_HEADSET -> "USBHeadset"
      AudioDeviceInfo.TYPE_USB_ACCESSORY -> "USBAccessory"
      AudioDeviceInfo.TYPE_TELEPHONY -> "Telephony"
      AudioDeviceInfo.TYPE_AUX_LINE -> "AuxLine"
      AudioDeviceInfo.TYPE_IP -> "IP"
      AudioDeviceInfo.TYPE_BUS -> "Bus"
      AudioDeviceInfo.TYPE_HDMI -> "HDMI"
      AudioDeviceInfo.TYPE_HDMI_ARC -> "HDMI_ARC"
      AudioDeviceInfo.TYPE_FM_TUNER -> "FMTuner"
      AudioDeviceInfo.TYPE_LINE_ANALOG -> "LineAnalog"
      AudioDeviceInfo.TYPE_LINE_DIGITAL -> "LineDigital"
      else -> "Unknown"
    }
  }

  companion object {
    private const val TAG = "AudioInputRoute"
  }
}

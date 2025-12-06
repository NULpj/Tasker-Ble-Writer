# Basic-Ble-Writer
Plugin for Tasker that allows the user to write arbitrary binary arrays to Bluetooth Low Energy Devices.

> This repository is a fork that adds the ability to send values as plain UTF-8 text (serial-style) in addition to the original hex payload mode.

Within tasker the action is called "Basic BLE Write"

Use the "Scan BLE devices" button in the configuration screen to pick a nearby device; the address field will be filled automatically.

Binary Arrays can be written to devices provided the user can supply:
* The device address in the format FF:FF:FF:FF:FF:FF (where "F" stands for any hex character)
* The service address/GUID: FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF
* The characteristic address/GUID: FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF.

Values can be sent either as hex pairs (the original behavior) or as plain UTF-8 text for serial-style payloads.
Use the "Send value as plain text (serial/UTF-8)" checkbox in the Tasker action to switch between modes.

I wrote this to aide me in the construction of my very own, very simple, BLE device, so the functionality fits my use case.  

I will likely add type selection for the value to be written in the near future.

This Module could really really really use tasker variable interpolation, but I don't have the time at this moment to sort that out.  

This code may be modified and used freely by other products and projects, excepting in the case where this project makes up 50% or more of said project or product: Lets work together on this to make it better for everyone!

## Build

* Java 21 and Android SDK Platform 34 / Build-Tools 34.0.0
* From the repo root: `./gradlew clean assembleDebug`

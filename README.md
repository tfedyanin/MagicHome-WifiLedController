[![Build Status](https://travis-ci.com/tfedyanin/MagicHome-WifiLedController.svg?branch=master)](https://travis-ci.com/tfedyanin/MagicHome-WifiLedController)

# JMagicHome
Library and OpenHAB2 binding for 
<img src="readme/controller.jpg" width="400" height="400" alt="Фото контроллера" title="Именно такой контроллер использую я"/>

# Subprojects
`led-binding-core` - java library for discovery and control MagicHome WiFi controller

# How to

## How to use library
`ru.ittim.openhab.ledbinding.library.DiscoveryFinder.main` - discovery example
`ru.ittim.openhab.ledbinding.library.LedController.main` - control example

## How to connect WiFi LED device to your Wi-Fi
1. Download application Magic Home WiFi for [iOS](https://itunes.apple.com/ru/app/magic-home-wifi/id944574066?mt=8) or Android.
2. Connect your iOS/Android device to Wi-Fi LED controller `LEDnet*`.
3. Open application, scan and connect to LED device.
4. Open `Settings`, connect to your Wi-Fi network and wait while device reboot about 1-3 minutes.
5. Connect your iOS/Android device to you Wi-Fi network and scan devices. Your must see new device in list of local devices.
6. Rename device to man readable name and save device name-ID mapping for incident investigation in future.

# TODO list
- [x] One device PoC 
- [x] Discovery and manipulate multiple controllers
- [ ] Release core
- [ ] OH2 binding

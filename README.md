# MCPs for Mobile

MCPs for Mobile is a Kotlin-based project designed to interact with Android devices or emulators using an AI agent
powered by Google's Gemini models. The agent can understand natural language commands to perform tasks on the connected
Android device, such as listing apps, opening apps, interacting with UI elements, and more.

## Features

* **AI-Powered Device Control:** Uses Google's Gemini AI to interpret commands and control Android devices.
* **ADB Integration:** Leverages ADB (Android Debug Bridge) for device interaction.
* **Tool-Based Agent:** The AI agent uses a set of defined tools to perform actions on the device.
* **Extensible:** New tools and capabilities can be added to expand the agent's functionality.
* **Kotlin-Based:** Written in modern Kotlin, suitable for desktop execution.

## ⚠️ Caution

> **This AI agent can execute ADB commands automatically in response to your prompts without confirmation.**  
> Please be cautious when using natural language commands, especially those that could modify app state, interact with UI, or perform sensitive operations.  
> It is recommended to use this tool in controlled environments, such as development devices or emulators.

## 🎥 Demo

The following demo shows how the AI agent performs a sequence of actions on an Android device using natural language.

> **Prompt:**
>
> ```text
> Find available android device,  
> list installed apps,  
> open "com.cacaosd.later.dev",  
> dump ui and find Search tab and click it  
> dump ui and find edittext for search and type "Google" and send done event,  
> dump ui search result list and tap first element in result list which has title, description and date  
> AS a result tell me what you see on screen and tell me which tool did you use
> ```

[android_demo.webm](https://github.com/user-attachments/assets/4e993976-7e87-495d-882f-29e086f7e3f0)

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing
purposes.

### Prerequisites

* **Java Development Kit (JDK):** Version 17 or higher recommended.
* **IntelliJ IDEA:** Recommended for development, but any IDE supporting Gradle and Kotlin will work.
* **Android SDK:** Specifically, you need `adb` (Android Debug Bridge) installed and set `ANDROID_HOME` environment
  variable in run configuration.
* You can install this as part of Android Studio or as standalone platform tools.
* **An Android Device or Emulator:** Ensure a device is connected via USB (with USB debugging enabled) or an emulator is
  running. You can check connectivity by running `adb devices` in your terminal.

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE.txt) file for details.

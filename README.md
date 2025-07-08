# MCPs for Mobile

**MCPs for Mobile** is a Kotlin-based project designed to interact with Android devices or emulators using an AI agent 
powered by Google's Gemini models. The agent understands natural language commands and executes them on connected 
Android devices—such as listing apps, opening apps, interacting with UI elements, and more.

---

## ✨ Features

* **AI-Powered Device Control:** Uses Google's Gemini AI to interpret commands and control Android devices.
* **ADB Integration:** Leverages ADB (Android Debug Bridge) for device interaction.
* **Tool-Based Agent:** The AI agent uses a set of defined tools to perform actions on the device.
* **Extensible:** New tools and capabilities can be added to expand the agent's functionality.
* **Kotlin-Based:** Written in modern Kotlin, suitable for desktop execution.

---

## ⚠️ Caution

> **This AI agent can execute ADB commands automatically in response to your prompts without confirmation.**  
> Please be cautious when using natural language commands, especially those that could modify app state, interact with UI, or perform sensitive operations.  
> It is recommended to use this tool in controlled environments, such as development devices or emulators.

---

## 🎥 Demo

This demo shows the AI agent performing a sequence of natural language-driven actions on an Android device:

> **Prompt Example:**
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

📹 [Watch the demo](https://github.com/user-attachments/assets/c18f5b3a-5c11-48e9-981f-68bbb99ab3f8)


## 🚀 Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing
purposes.

### Prerequisites

- **Java Development Kit (JDK)**: Version 17 or higher.
- **IntelliJ IDEA** *(Recommended)* or any Kotlin-compatible IDE with Gradle support.
- **Android SDK + ADB**: Install ADB (Android Debug Bridge) and ensure `ANDROID_HOME` is configured.
  - You can install ADB via Android Studio or [platform-tools](https://developer.android.com/studio/releases/platform-tools).
- **Android Emulator or Device**: Make sure a device is connected and recognized via `adb devices`.

---

## 🗺️ Roadmap

Here's a list of planned improvements and upcoming features:

- **Enhanced UI Representation**
  - Improve the quality and detail of the UI dump to better represent real-world app scenarios.
  - Support for rich component hierarchies to aid in accurate agent reasoning.

- **Expanded ADB Capabilities**
  - Introduce new ADB-based tools to support more device-level operations.
  - Improve error handling and fallback mechanisms for device communication.

- **Layout Optimizer Iteration**
  - Refine and simplify layout structures for better LLM understanding.
  - Remove redundant or low-signal elements from UI trees before interpretation.

- **Migration to `adblib`**
  - Investigate replacing `ddmlib` with `adblib` or `adblib-tools` for improved performance and maintainability.
  - Compare feature parity and assess integration effort.

---

## 📄 License

This project is licensed under the Apache License 2.0 – see the [LICENSE](LICENSE.txt) file for details.

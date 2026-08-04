# Photosynthesis Lab - Class 9

Welcome to the **Photosynthesis Lab** Android application! This interactive educational app is designed specifically for Class 9 students to explore and understand the fascinating process of photosynthesis. 

## 📖 About the Learning Material
The app provides a comprehensive and engaging learning experience, breaking down complex biological concepts into easy-to-digest interactive modules. It aims to foster a deeper understanding of plant biology through varied content delivery methods.

### 🌟 Content Types Available
* **Reading Material:** In-depth, easy-to-understand textual content explaining the fundamental concepts of photosynthesis.
* **Flip Cards:** Interactive flashcards for quick revision and memorization of key terms and definitions.
* **Media Interactions:** Engaging diagrams and visual aids (like chloroplast structure and the photosynthesis process) that users can interact with.
* **YouTube Videos:** Curated educational videos integrated directly into the app for visual and auditory learning.
* **Quizzes:** Interactive assessments to test knowledge and track learning progress.

## 📊 Clickstream Data Analytics
To better understand user engagement and improve the learning experience, this app implements comprehensive **Clickstream Data Capture**. 
The following specific clickstream events are captured:
* `LOGIN` & `LOGOUT`: Tracks user sessions.
* `NAVIGATION_CLICK`: Tracks general app navigation between screens.
* `READING_SECTION_VIEW` & `READING_DWELL_TIME`: Measures engagement and time spent with reading materials.
* `MEDIA_INTERACTION`: Tracks interaction with interactive diagrams.
* `VOCAB_CARD_FLIP`: Captures engagement with flip cards.
* **Video Analytics:** `VIDEO_LAUNCHED`, `VIDEO_PLAYED`, `VIDEO_PAUSED`, `VIDEO_SKIPPED`, `VIDEO_ENDED`, `VIDEO_ERROR`.
* **Quiz Analytics:** `QUIZ_START`, `QUESTION_VIEW`, `QUESTION_ANSWER_SELECT`, `QUESTION_RESPONSE_LATENCY`, `QUIZ_SUBMIT`, `QUIZ_FINISHED`.

This data can be exported in CSV or JSON format and analyzed by Admins to provide insights into how students interact with the educational material.

## 🔐 Authentication (Sample Demo)
For demonstration purposes, the app includes a simplified login flow for both **User** and **Admin** roles.
* **Note:** Currently, this is a sample implementation. **Any combination of username and password will work** to log in and explore the app's features.

---

## 🛠️ Tech Stack
This application is built using modern Android development practices and technologies:
* **Language:** [Kotlin](https://kotlinlang.org/) - The primary programming language for Android development.
* **UI Toolkit:** [Jetpack Compose](https://developer.android.com/jetpack/compose) - Android's modern toolkit for building native UI in a declarative manner.
* **Architecture:** Modern Android Architecture 
* **IDE:** Android Studio.

---

## 🚀 How to Run the Project Locally

If you want to view the source code or contribute to the project, follow these steps to run the app in your local environment:

### Prerequisites
1. Download and install the latest version of [Android Studio](https://developer.android.com/studio).
2. Ensure you have Git installed on your system.

### Steps to Run
1. **Download the Code:**
   * Download the source code as a ZIP file and extract it, or clone the repository if it's hosted on Git.
2. **Open in Android Studio:**
   * Launch Android Studio.
   * Click on **File > Open** (or "Open an existing Android Studio project" from the welcome screen).
   * Navigate to the folder where you extracted the code and select the root directory (`photosynthesis-lab---class-9`).
   * Wait for Android Studio to sync the project and download all necessary Gradle dependencies.
3. **Run the App:**
   * Once the Gradle sync is complete, select your target device (an Android Emulator or a physical device connected via USB with USB Debugging enabled) from the device drop-down menu in the toolbar.
   * Click the **Run** button (green play icon) or press `Shift + F10`.
   * The app will build and launch on your selected device.

---

## 📥 Download the APK
If you just want to install and try out the app on your Android device without building it from source, you can download the latest APK file from the Google Drive link below:

🔗 **[Download APK Here]** *https://drive.google.com/file/d/1b99fHgNYm2JnCYwukFfJln76FH4I-bbB/view?usp=sharing*

*Note: You may need to enable "Install unknown apps" from your device settings to install the APK directly.*

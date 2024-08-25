# JarvisV2

JarvisV2 is an AI assistant that uses OpenAI's ChatGPT API to create multiple robot sessions, each starting different conversations. Additionally, it features a "generate image" capability for creating images. This project is developed in Kotlin for Android.

## Features

- Create and manage multiple robot sessions
- Start and manage conversations
- Integration with OpenAI's ChatGPT API
- Image generation capability
- Splash screen and Lottie animations
- Multi-screen navigation using Navigation component
- SQLite database integration with Room

## Installation

  • Clone the repository.
  • Add your OpenAI API key in the `build.gradle` file:
   `gradle buildConfigField("String", "OPENAI_API_KEY", "\"your-openai-api-key\"")`
  • Build and run the project in Android Studio.

## Requirements

  •	Android SDK 24+
	•	OpenAI API Key
	•	Kotlin 1.8+

## Libraries Used

  •	Retrofit: For networking and API requests
	•	Room: For local database storage
	•	Glide: For image loading
	•	Lottie: For animations
	•	MultiWaveHeader: For wave animations in UI
	•	Navigation Component: For multi-screen navigation

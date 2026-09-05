# CropGuard

An offline-first Android application for crop disease diagnosis, built for farmers in connectivity-limited regions of Ghana. CropGuard uses an on-device quantized deep learning model to identify diseases in **cashew**, **cassava**, and **maize** from leaf images, and provides actionable treatment recommendations sourced from Ghanaian agricultural authorities.

---

## Features

- **Fully offline** — no internet required after installation. The TFLite model runs entirely on-device.
- **17 conditions detected** across 3 crops (cashew, cassava, maize), including healthy states.
- **Camera & gallery input** — capture a leaf photo or pick from gallery.
- **Treatment recommendations** — verified protocols from COCOBOD/CRIG (cashew), IITA/CSIR-CRI (cassava), and MOFA PPRSD (maize).
- **Confidence scoring** — shows prediction confidence with a visual ring indicator. Low-confidence results trigger an uncertainty screen advising the farmer to re-scan or consult an extension officer.

## Supported Conditions

| Crop | Conditions |
|------|-----------|
| **Cashew** | Anthracnose, Gummosis, Leaf Miner, Red Rust, Healthy |
| **Cassava** | Bacterial Blight, Brown Spot, Green Mite, Mosaic, Healthy |
| **Maize** | Fall Armyworm, Grasshopper, Leaf Beetle, Leaf Blight, Leaf Spot, Streak Virus, Healthy |

---

## Architecture

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│ MainActivity │────►│CaptureActivity│────►│ResultActivity │
│  (Crop Grid) │     │ (Camera/     │     │ (Diagnosis + │
│              │     │  Gallery)    │     │  Treatment)  │
└──────────────┘     └──────┬───────┘     └──────────────┘
                            │                     ▲
                            ▼                     │
                     ┌──────────────┐      ┌──────┴───────┐
                     │  Classifier  │─────►│TreatmentData │
                     │ (TFLite INT8)│      │ (Protocols)  │
                     └──────────────┘      └──────────────┘
```

- **Classifier.java** — Loads the quantized EfficientNet-Lite0 model (`.tflite`), preprocesses 224×224 RGB input, and runs inference using TensorFlow Lite with 4-thread CPU execution.
- **TreatmentData.java** — Maps model output labels to human-readable disease descriptions and treatment protocols verified by Ghanaian agricultural authorities.
- **UncertainActivity.java** — Handles low-confidence predictions, advising re-scan or expert consultation.

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| **Language** | Java |
| **ML Runtime** | TensorFlow Lite (Google AI Edge LiteRT 1.0.1) |
| **Model** | EfficientNet-Lite0, INT8 quantized (~4 MB) |
| **Camera** | Android Camera2 API |
| **Min SDK** | Android 8.0 (API 26) |
| **Target SDK** | Android 15 (API 36) |
| **Build** | Gradle (Kotlin DSL) |

---

## Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1) or later
- Android device or emulator running API 26+

### Build & Run

1. Clone the repository:
   ```bash
   git clone https://github.com/SamuelNadutey/CropGuard.git
   ```

2. Open the project in Android Studio.

3. Sync Gradle and build.

4. Run on a physical device (recommended for camera testing) or emulator.

### Install from APK

A pre-built APK is available for direct installation on Android devices — sideload via USB or file transfer.

---

## Model Details

- **Architecture:** EfficientNet-Lite0 (MobileNet-family, optimized for edge)
- **Training:** Transfer learning with TensorFlow Lite Model Maker on a custom dataset of Ghanaian crop images
- **Quantization:** Post-training INT8 quantization — reduces model size from ~16 MB (FP32) to ~4 MB while maintaining 94% classification accuracy
- **Input:** 224 × 224 × 3 (RGB)
- **Output:** 17-class softmax probability vector

---

## Project Structure

```
app/src/main/
├── java/com/cropguard/app/
│   ├── MainActivity.java        # Home screen with crop selection grid
│   ├── CaptureActivity.java     # Camera/gallery image acquisition
│   ├── Classifier.java          # TFLite model loading & inference
│   ├── ResultActivity.java      # Diagnosis display & treatment info
│   ├── TreatmentData.java       # Disease → treatment protocol mapping
│   └── UncertainActivity.java   # Low-confidence result handling
│
├── assets/
│   ├── cropguard_model_dynamic.tflite   # Quantized model
│   └── labels.txt                       # 17 class labels
│
└── res/
    ├── layout/                  # Activity XML layouts
    └── drawable/                # UI assets, crop images, custom shapes
```

---

## Treatment Data Sources

All treatment protocols are sourced from recognized Ghanaian and international agricultural authorities:

- **Cashew:** COCOBOD / Cocoa Research Institute of Ghana (CRIG)
- **Cassava:** IITA / CSIR-Crops Research Institute (CRI)
- **Maize:** MOFA Plant Protection & Regulatory Services Directorate (PPRSD)

---

## License

This project was developed as a coursework project at KNUST. Feel free to use and adapt for academic and educational purposes.

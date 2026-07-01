package com.cropguard.app;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class Classifier {

    private static final String MODEL_FILE = "cropguard_model_dynamic.tflite";
    private static final String LABEL_FILE = "labels.txt";
    private static final int IMG_SIZE = 224;   // model expects 224x224
    private static final int NUM_CHANNELS = 3; // RGB

    private Interpreter interpreter;
    private final List<String> labels = new ArrayList<>();

    public Classifier(Context context) throws IOException {
        Interpreter.Options options = new Interpreter.Options();
        options.setNumThreads(4);
        interpreter = new Interpreter(loadModelFile(context), options);
        loadLabels(context);
    }

    // Load the .tflite model from assets into memory
    private MappedByteBuffer loadModelFile(Context context) throws IOException {
        AssetFileDescriptor fd = context.getAssets().openFd(MODEL_FILE);
        FileInputStream is = new FileInputStream(fd.getFileDescriptor());
        FileChannel channel = is.getChannel();
        long startOffset = fd.getStartOffset();
        long declaredLength = fd.getDeclaredLength();
        return channel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    // Read labels.txt (one class per line, in model output order)
    private void loadLabels(Context context) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(context.getAssets().open(LABEL_FILE)));
        String line;
        while ((line = reader.readLine()) != null) {
            if (!line.trim().isEmpty()) labels.add(line.trim());
        }
        reader.close();
    }

    // Convert a bitmap into the exact tensor the model expects
    private ByteBuffer preprocess(Bitmap bitmap) {
        Bitmap resized = Bitmap.createScaledBitmap(bitmap, IMG_SIZE, IMG_SIZE, true);

        // 4 bytes per float * pixels * channels
        ByteBuffer buffer = ByteBuffer.allocateDirect(4 * IMG_SIZE * IMG_SIZE * NUM_CHANNELS);
        buffer.order(ByteOrder.nativeOrder());

        int[] pixels = new int[IMG_SIZE * IMG_SIZE];
        resized.getPixels(pixels, 0, IMG_SIZE, 0, 0, IMG_SIZE, IMG_SIZE);

        for (int pixel : pixels) {
            // Extract R, G, B (0-255) and feed as RAW values — NO division by 255.
            // EfficientNet normalizes internally; this MUST match training.
            float r = (pixel >> 16) & 0xFF;
            float g = (pixel >> 8) & 0xFF;
            float b = pixel & 0xFF;
            buffer.putFloat(r);
            buffer.putFloat(g);
            buffer.putFloat(b);
        }
        return buffer;
    }

    // Run inference and return the top result
    public Result classify(Bitmap bitmap) {
        ByteBuffer input = preprocess(bitmap);
        float[][] output = new float[1][labels.size()];
        interpreter.run(input, output);

        // Find the highest-scoring class
        int bestIdx = 0;
        float bestScore = output[0][0];
        for (int i = 1; i < labels.size(); i++) {
            if (output[0][i] > bestScore) {
                bestScore = output[0][i];
                bestIdx = i;
            }
        }
        return new Result(labels.get(bestIdx), bestScore, output[0]);
    }

    public List<String> getLabels() {
        return labels;
    }

    // Simple holder for a prediction
    public static class Result {
        public final String label;
        public final float confidence;
        public final float[] allScores;

        public Result(String label, float confidence, float[] allScores) {
            this.label = label;
            this.confidence = confidence;
            this.allScores = allScores;
        }
    }

    // Get the model's score for a SPECIFIC class index, given a bitmap
    public float scoreForClass(Bitmap bitmap, int classIndex) {
        ByteBuffer input = preprocess(bitmap);
        float[][] output = new float[1][labels.size()];
        interpreter.run(input, output);
        return output[0][classIndex];
    }

    // Occlusion heatmap: returns a gridSize x gridSize array of "importance"
    // (how much covering each cell drops the target class's confidence)
    public float[][] occlusionHeatmap(Bitmap bitmap, int classIndex, int gridSize) {
        Bitmap base = Bitmap.createScaledBitmap(bitmap, 224, 224, true);
        float baseScore = scoreForClass(base, classIndex);

        float[][] importance = new float[gridSize][gridSize];
        int cell = 224 / gridSize;

        for (int gy = 0; gy < gridSize; gy++) {
            for (int gx = 0; gx < gridSize; gx++) {
                // Copy the base image and grey out one cell
                Bitmap occluded = base.copy(Bitmap.Config.ARGB_8888, true);
                for (int y = gy * cell; y < (gy + 1) * cell && y < 224; y++) {
                    for (int x = gx * cell; x < (gx + 1) * cell && x < 224; x++) {
                        occluded.setPixel(x, y, 0xFF808080); // grey patch
                    }
                }
                float occScore = scoreForClass(occluded, classIndex);
                // Bigger drop = this region mattered more
                importance[gy][gx] = Math.max(0f, baseScore - occScore);
                occluded.recycle();
            }
        }
        return importance;
    }
}
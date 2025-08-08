package com.ps5controller.mod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class Config {
    public static boolean invertY = false;
    public static float deadzone = 0.15f;
    public static float moveCurve = 1.5f;
    public static float lookSensitivity = 2.0f;
    public static float lookCurve = 1.2f;
    public static String extraKeyOnTouchpad = "";
    public static String extraKeyOnTriangle = "";
    public static String extraKeyOnCircle = "";

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void load() {
        try {
            File cfg = new File("config/ps5controller.json");
            if (!cfg.exists()) {
                save(); // write defaults
                return;
            }
            ConfigFile f = GSON.fromJson(new FileReader(cfg), ConfigFile.class);
            if (f == null) return;
            invertY = f.invertY;
            deadzone = f.deadzone;
            moveCurve = f.moveCurve;
            lookSensitivity = f.lookSensitivity;
            lookCurve = f.lookCurve;
            extraKeyOnTouchpad = f.extraKeyOnTouchpad == null ? "" : f.extraKeyOnTouchpad;
            extraKeyOnTriangle = f.extraKeyOnTriangle == null ? "" : f.extraKeyOnTriangle;
            extraKeyOnCircle = f.extraKeyOnCircle == null ? "" : f.extraKeyOnCircle;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try {
            File cfg = new File("config/ps5controller.json");
            cfg.getParentFile().mkdirs();
            ConfigFile f = new ConfigFile();
            f.invertY = invertY;
            f.deadzone = deadzone;
            f.moveCurve = moveCurve;
            f.lookSensitivity = lookSensitivity;
            f.lookCurve = lookCurve;
            f.extraKeyOnTouchpad = extraKeyOnTouchpad;
            f.extraKeyOnTriangle = extraKeyOnTriangle;
            f.extraKeyOnCircle = extraKeyOnCircle;
            try (FileWriter w = new FileWriter(cfg)) {
                GSON.toJson(f, w);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class ConfigFile {
        public boolean invertY;
        public float deadzone;
        public float moveCurve;
        public float lookSensitivity;
        public float lookCurve;
        public String extraKeyOnTouchpad;
        public String extraKeyOnTriangle;
        public String extraKeyOnCircle;
    }
}

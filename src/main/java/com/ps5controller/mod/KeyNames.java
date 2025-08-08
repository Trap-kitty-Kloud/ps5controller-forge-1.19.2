package com.ps5controller.mod;

import org.lwjgl.glfw.GLFW;

public class KeyNames {
    public static int toGlfw(String name) {
        if (name == null) return -1;
        name = name.trim();
        if (name.length() == 1) {
            char c = name.toUpperCase().charAt(0);
            if (c >= 'A' && c <= 'Z') return GLFW.GLFW_KEY_A + (c - 'A');
            if (c >= '0' && c <= '9') return GLFW.GLFW_KEY_0 + (c - '0');
        }
        // Common names
        switch (name.toUpperCase()) {
            case "SPACE": return GLFW.GLFW_KEY_SPACE;
            case "SHIFT": return GLFW.GLFW_KEY_LEFT_SHIFT;
            case "CTRL":
            case "CONTROL": return GLFW.GLFW_KEY_LEFT_CONTROL;
            case "ALT": return GLFW.GLFW_KEY_LEFT_ALT;
            case "TAB": return GLFW.GLFW_KEY_TAB;
            case "CAPS": return GLFW.GLFW_KEY_CAPS_LOCK;
            case "ENTER": return GLFW.GLFW_KEY_ENTER;
            case "BACKSPACE": return GLFW.GLFW_KEY_BACKSPACE;
            case "ESC":
            case "ESCAPE": return GLFW.GLFW_KEY_ESCAPE;
            case "LEFT": return GLFW.GLFW_KEY_LEFT;
            case "RIGHT": return GLFW.GLFW_KEY_RIGHT;
            case "UP": return GLFW.GLFW_KEY_UP;
            case "DOWN": return GLFW.GLFW_KEY_DOWN;
            case "F1": return GLFW.GLFW_KEY_F1;
            case "F2": return GLFW.GLFW_KEY_F2;
            case "F3": return GLFW.GLFW_KEY_F3;
            case "F4": return GLFW.GLFW_KEY_F4;
            case "F5": return GLFW.GLFW_KEY_F5;
            case "F6": return GLFW.GLFW_KEY_F6;
            case "F7": return GLFW.GLFW_KEY_F7;
            case "F8": return GLFW.GLFW_KEY_F8;
            case "F9": return GLFW.GLFW_KEY_F9;
            case "F10": return GLFW.GLFW_KEY_F10;
            case "F11": return GLFW.GLFW_KEY_F11;
            case "F12": return GLFW.GLFW_KEY_F12;
        }
        return -1;
    }
}

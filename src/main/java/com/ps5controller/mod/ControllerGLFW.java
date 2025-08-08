package com.ps5controller.mod;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWGamepadState;

public class ControllerGLFW {
    private int jid = -1;
    private final GLFWGamepadState state = GLFWGamepadState.create();

    // Index constants
    public static final int AXIS_LX = 0;
    public static final int AXIS_LY = 1;
    public static final int AXIS_RX = 2;
    public static final int AXIS_RY = 3;
    public static final int AXIS_L2 = 4; // analog triggers
    public static final int AXIS_R2 = 5;
    public static final int AXIS_DPAD_X = 6; // these two aren't standard axes in GLFW; we synthesize from buttons
    public static final int AXIS_DPAD_Y = 7;

    public boolean ready() { return jid != -1; }

    public void tryInit() {
        for (int i = 0; i <= GLFW.GLFW_GAMEPAD_LAST; i++) {
            if (GLFW.glfwJoystickIsGamepad(i)) {
                String name = GLFW.glfwGetGamepadName(i);
                if (name != null && name.toLowerCase().contains("wireless") || jid == -1) {
                    jid = i;
                }
            }
        }
    }

    public void poll() {
        if (jid == -1) return;
        GLFW.glfwGetGamepadState(jid, state);
    }

    public float axis(int idx) {
        if (jid == -1) return 0f;
        if (idx == AXIS_DPAD_X) {
            float right = button(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_RIGHT);
            float left = button(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_LEFT);
            return right - left;
        }
        if (idx == AXIS_DPAD_Y) {
            float down = button(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN);
            float up = button(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP);
            return down - up;
        }
        if (idx < 0 || idx >= state.axes().limit()) return 0f;
        return state.axes(idx);
    }

    public float button(int glfwButton) {
        if (jid == -1) return 0f;
        if (glfwButton < 0 || glfwButton >= state.buttons().limit()) return 0f;
        return state.buttons(glfwButton) == GLFW.GLFW_PRESS ? 1f : 0f;
    }
}

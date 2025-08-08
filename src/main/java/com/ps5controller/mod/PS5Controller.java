package com.ps5controller.mod;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWGamepadState;

@Mod(PS5Controller.MODID)
public class PS5Controller {
    public static final String MODID = "ps5controller";

    private final ControllerGLFW controller = new ControllerGLFW();
    private final Mapper mapper = new Mapper();

    public PS5Controller() {
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(this);
        Config.load(); // create config if missing
    }

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        if (!controller.ready()) controller.tryInit();
        if (!controller.ready()) return;

        controller.poll();
        mapper.apply(controller);
    }

    static final class Mapper {
        private boolean lastOptions = false, lastTouch = false, lastTriangle = false, lastCircle = false;

        private float applyDead(float v) {
            float dz = Config.deadzone;
            if (Math.abs(v) < dz) return 0f;
            float sign = Math.signum(v);
            float norm = (Math.abs(v) - dz) / (1.0f - dz);
            return (float) (sign * Math.pow(norm, Config.moveCurve));
        }

        private float curveLook(float v) {
            float sign = Math.signum(v);
            return (float)(sign * Math.pow(Math.abs(v), Config.lookCurve));
        }

        public void apply(ControllerGLFW c) {
            Minecraft mc = Minecraft.getInstance();
            Player p = mc.player;
            if (mc.screen != null) return; // don't drive controls while in a GUI

            // Movement
            float lx = applyDead(c.axis(ControllerGLFW.AXIS_LX));
            float ly = applyDead(c.axis(ControllerGLFW.AXIS_LY));
            set(mc.options.keyUp, ly < -0.15f);
            set(mc.options.keyDown, ly > 0.15f);
            set(mc.options.keyLeft, lx < -0.15f);
            set(mc.options.keyRight, lx > 0.15f);

            // Sprint (L3), Sneak (Circle hold), Jump (Cross)
            boolean l3 = c.button(GLFW.GLFW_GAMEPAD_BUTTON_LEFT_THUMB) > 0.5f;
            set(mc.options.keySprint, l3);
            boolean circle = c.button(GLFW.GLFW_GAMEPAD_BUTTON_B) > 0.5f;
            set(mc.options.keyShift, circle);
            boolean cross = c.button(GLFW.GLFW_GAMEPAD_BUTTON_A) > 0.5f;
            set(mc.options.keyJump, cross);

            // Inventory (Options)
            boolean options = c.button(GLFW.GLFW_GAMEPAD_BUTTON_START) > 0.5f;
            if (options && !lastOptions) {
                click(mc.options.keyInventory);
            }
            lastOptions = options;

            // Swap hands (Triangle) + extra key
            boolean triangle = c.button(GLFW.GLFW_GAMEPAD_BUTTON_Y) > 0.5f;
            if (triangle && !lastTriangle) {
                click(mc.options.keySwapOffhand);
                pressExtra(Config.extraKeyOnTriangle);
            }
            lastTriangle = triangle;

            // Touchpad => extra key (GLFW doesn't expose touchpad; we reuse "back" button as alt if needed)
            boolean touch = c.button(GLFW.GLFW_GAMEPAD_BUTTON_BACK) > 0.5f;
            if (touch && !lastTouch) {
                pressExtra(Config.extraKeyOnTouchpad);
            }
            lastTouch = touch;

            if (circle && !lastCircle) {
                pressExtra(Config.extraKeyOnCircle);
            }
            lastCircle = circle;

            // Hotbar L1/R1
            boolean l1 = c.button(GLFW.GLFW_GAMEPAD_BUTTON_LEFT_BUMPER) > 0.5f;
            boolean r1 = c.button(GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER) > 0.5f;
            if (l1) p.getInventory().selected = (p.getInventory().selected + 8) % 9;
            if (r1) p.getInventory().selected = (p.getInventory().selected + 1) % 9;

            // D-pad: left/right hotbar, up inv, down drop
            float dpadX = c.axis(ControllerGLFW.AXIS_DPAD_X);
            float dpadY = c.axis(ControllerGLFW.AXIS_DPAD_Y);
            if (dpadX > 0.5f) p.getInventory().selected = (p.getInventory().selected + 1) % 9;
            if (dpadX < -0.5f) p.getInventory().selected = (p.getInventory().selected + 8) % 9;
            if (dpadY < -0.5f) click(mc.options.keyInventory);
            if (dpadY > 0.5f) click(mc.options.keyDrop);

            // Look (right stick)
            float rx = curveLook(applyDead(c.axis(ControllerGLFW.AXIS_RX)));
            float ry = curveLook(applyDead(c.axis(ControllerGLFW.AXIS_RY)));
            float sens = Config.lookSensitivity;
            p.setYRot(p.getYRot() + rx * 6.0f * sens);
            float newPitch = p.getXRot() + (Config.invertY ? ry : -ry) * 6.0f * sens;
            if (newPitch > 90f) newPitch = 90f;
            if (newPitch < -90f) newPitch = -90f;
            p.setXRot(newPitch);

            // Triggers: Use/Attack
            boolean use = c.axis(ControllerGLFW.AXIS_L2) > 0.4f || c.button(GLFW.GLFW_GAMEPAD_BUTTON_LEFT_TRIGGER) > 0.5f;
            boolean attack = c.axis(ControllerGLFW.AXIS_R2) > 0.4f || c.button(GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_TRIGGER) > 0.5f;
            set(mc.options.keyUse, use);
            set(mc.options.keyAttack, attack);
        }

        private void set(KeyMapping km, boolean down) {
            km.setDown(down);
            if (down) km.clickCount++;
        }

        private void click(KeyMapping km) {
            km.setDown(true);
            km.clickCount++;
            km.setDown(false);
        }

        private void pressExtra(String keyName) {
            if (keyName == null || keyName.trim().isEmpty()) return;
            int code = KeyNames.toGlfw(keyName);
            if (code == -1) return;
            // Find any KeyMapping bound to this GLFW key code and toggle it for one tick
            for (KeyMapping km : Minecraft.getInstance().options.keyMappings) {
                if (km.getKey().getValue() == code) {
                    click(km);
                }
            }
        }
    }
}

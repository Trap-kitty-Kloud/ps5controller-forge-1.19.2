# PS5 Controller Support (Forge 1.19.2)

A simple **client-side** Forge 1.19.2 mod that reads a **PS5 DualSense** using **GLFW gamepad API** (LWJGL3) and maps it to vanilla keybinds and look.
Keyboard & mouse still work. You can optionally have certain buttons "press" any keybind code to interop with other mods (e.g., Tensura).

## Build
- JDK 17
- Run:
  ```bash
  gradlew build
  ```
- Put `build/libs/ps5controller-1.0.0.jar` in your `mods` for MC **1.19.2**.

## Config
On first run, `config/ps5controller.json` is created. Options:
```json
{
  "invertY": false,
  "deadzone": 0.15,
  "moveCurve": 1.5,
  "lookSensitivity": 2.0,
  "lookCurve": 1.2,
  "extraKeyOnTouchpad": "",
  "extraKeyOnTriangle": "",
  "extraKeyOnCircle": ""
}
```
Set `extraKeyOn*` to a key name like `"G"`, `"V"`, `"SPACE"`, etc. The mod will find any KeyMapping bound to that GLFW key code (including other mods') and **click** it for you.

## Defaults
- Left stick → WASD
- Right stick → camera look (invert via config)
- X (Cross) → Jump
- Circle → Sneak (hold)
- Triangle → Swap Offhand + optional extra key
- Square → Inventory toggle (bind this yourself by setting `extraKeyOnCircle` or use D-pad Up)
- L3 → Sprint (hold)
- L1/R1 → Hotbar prev/next
- L2 → Use item, R2 → Attack
- D-pad: Left/Right = hotbar, Up = inventory, Down = drop
- Touchpad click (simulated with "Back" button due to GLFW limitations) → extra key

> Note: GLFW uses an Xbox-style mapping; DualSense is normalized to this layout by GLFW's mapping database.

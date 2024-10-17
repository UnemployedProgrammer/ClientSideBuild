package de.sebastian.clientsidebuild.client;

import de.sebastian.clientsidebuild.misc.BlockStateModifier;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
import org.lwjgl.glfw.GLFW;

public class DirectionScreen extends Screen {

    private boolean wPressed = false;
    private boolean aPressed = false;
    private boolean sPressed = false;
    private boolean dPressed = false;
    private boolean spacePressed = false;
    private boolean shiftPressed = false;
    private Direction dir = Direction.UP;

    public DirectionScreen() {
        super(Text.literal("Set Direction"));
    }

    @Override
    protected void init() {
        ButtonWidget save = ButtonWidget.builder(Text.of(Text.translatable("button.clientsidebuilding.save")), (btn) -> {
            ClientsidebuildClient.BLOCK_STATE_MODIFIER = new BlockStateModifier().setDirection(dir);
        }).dimensions(width / 2 - (width - 40) / 2, height - 30, width - 40, 20).build();

        ButtonWidget next = ButtonWidget.builder(Text.of(Text.translatable("button.clientsidebuilding.up")), (btn) -> {
            dir = BlockStateModifier.directionNext(dir);
            btn.setMessage(Text.translatable("button.clientsidebuilding." + dir.asString().toLowerCase()));
        }).dimensions(width / 2 - (width - 40) / 2, 20, width - 40, 20).build();

        // Register the button widget.
        this.addDrawableChild(save);
        this.addDrawableChild(next);

    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.client.world == null) {
            this.renderPanoramaBackground(context, delta);
        }

        if (!(wPressed || aPressed || sPressed || dPressed || spacePressed || shiftPressed)) {
            this.applyBlur(delta);
            this.renderDarkening(context);
        }

    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        context.drawText(this.textRenderer, "Save", 40, 40 - this.textRenderer.fontHeight - 10, 0xFFFFFFFF, true);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        switch (keyCode) {
            case GLFW.GLFW_KEY_W -> {
                if (!wPressed) {
                    ClientsidebuildClient.RENDER_OFFSET_Z -= 1; // Move forward
                    wPressed = true;
                }
                return true;
            }
            case GLFW.GLFW_KEY_S -> {
                if (!sPressed) {
                    ClientsidebuildClient.RENDER_OFFSET_Z += 1; // Move backward
                    sPressed = true;
                }
                return true;
            }
            case GLFW.GLFW_KEY_A -> {
                if (!aPressed) {
                    ClientsidebuildClient.RENDER_OFFSET_X -= 1; // Move left
                    aPressed = true;
                }
                return true;
            }
            case GLFW.GLFW_KEY_D -> {
                if (!dPressed) {
                    ClientsidebuildClient.RENDER_OFFSET_X += 1; // Move right
                    dPressed = true;
                }
                return true;
            }
            case GLFW.GLFW_KEY_SPACE -> {
                if (!spacePressed) {
                    ClientsidebuildClient.RENDER_OFFSET_Y += 1; // Move up
                    spacePressed = true;
                }
                return true;
            }
            case GLFW.GLFW_KEY_LEFT_SHIFT -> {
                if (!shiftPressed) {
                    ClientsidebuildClient.RENDER_OFFSET_Y -= 1; // Move down
                    shiftPressed = true;
                }
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        switch (keyCode) {
            case GLFW.GLFW_KEY_W -> {
                if (wPressed) {
                    ClientsidebuildClient.RENDER_OFFSET_Z += 1; // Reset forward movement
                    wPressed = false;
                }
                return true;
            }
            case GLFW.GLFW_KEY_S -> {
                if (sPressed) {
                    ClientsidebuildClient.RENDER_OFFSET_Z -= 1; // Reset backward movement
                    sPressed = false;
                }
                return true;
            }
            case GLFW.GLFW_KEY_A -> {
                if (aPressed) {
                    ClientsidebuildClient.RENDER_OFFSET_X += 1; // Reset left movement
                    aPressed = false;
                }
                return true;
            }
            case GLFW.GLFW_KEY_D -> {
                if (dPressed) {
                    ClientsidebuildClient.RENDER_OFFSET_X -= 1; // Reset right movement
                    dPressed = false;
                }
                return true;
            }
            case GLFW.GLFW_KEY_SPACE -> {
                if (spacePressed) {
                    ClientsidebuildClient.RENDER_OFFSET_Y -= 1; // Reset upward movement
                    spacePressed = false;
                }
                return true;
            }
            case GLFW.GLFW_KEY_LEFT_SHIFT -> {
                if (shiftPressed) {
                    ClientsidebuildClient.RENDER_OFFSET_Y += 1; // Reset downward movement
                    shiftPressed = false;
                }
                return true;
            }
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

}

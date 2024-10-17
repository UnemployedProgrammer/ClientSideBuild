package de.sebastian.clientsidebuild.client;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import de.sebastian.clientsidebuild.misc.BlockStateModifier;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import static net.minecraft.server.command.CommandManager.*;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.PosArgument;
import net.minecraft.item.BlockItem;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class ClientsidebuildClient implements ClientModInitializer {

    public static Logger logger = LoggerFactory.getLogger("clientsidebuilding_client");
    private static KeyBinding keyBinding;
    private static KeyBinding keyBinding2;
    private static KeyBinding keyBinding3;
    private static KeyBinding keyBinding4;
    public static Boolean ENABLED = true;
    private Map<BlockPos, BlockState> latestStates = new HashMap<>();
    public static int RENDER_OFFSET_X = 0;
    public static int RENDER_OFFSET_Y = 0;
    public static int RENDER_OFFSET_Z = 0;
    public static BlockPos knownPos1;
    public static BlockPos knownPos2;
    public static BlockStateModifier BLOCK_STATE_MODIFIER = new BlockStateModifier();


    @Override
    public void onInitializeClient() {
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            if(ENABLED) {
                latestStates.clear();
                MinecraftClient client = MinecraftClient.getInstance();
                //BlockPos pos = client.player.getBlockPos();
                BlockState state = null;

                if(client.player.getMainHandStack().getItem() instanceof BlockItem blockItem) {
                    state = BLOCK_STATE_MODIFIER.getBlockStateWithFacingDirectionBlock(blockItem.getBlock(), client.player);
                }

                if(state != null) {

                    if(knownPos1 == null) {
                        ENABLED = false;
                        //client.player.sendMessage(Text.literal("EMERGENCY_DISABLE"));
                        return;
                    }
                    if(knownPos2 == null) {
                        ENABLED = false;
                        //client.player.sendMessage(Text.literal("EMERGENCY_DISABLE"));
                        return;
                    }

                    // Iterate through all block positions between 'from' and 'to'
                    Iterator<BlockPos> positions = BlockPos.iterate(knownPos1, knownPos2).iterator();

                    while (positions.hasNext()) {
                        BlockPos currentPos = positions.next();

                        latestStates.put(currentPos, state);

                        // Example: set block to air (this can be changed to any other operation)
                        renderBlock(context, state, currentPos);
                    }
                }
            }
        });

        //Keybinds

        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "keybinds.clientsidebuilding.toggle", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_KP_0, // The keycode of the key
                "keybinds.clientsidebuilding" // The translation key of the keybinding's category.
        ));

        keyBinding2 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "keybinds.clientsidebuilding.place", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_KP_ENTER, // The keycode of the key
                "keybinds.clientsidebuilding" // The translation key of the keybinding's category.
        ));

        keyBinding3 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "keybinds.clientsidebuilding.pos1", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_KP_1, // The keycode of the key
                "keybinds.clientsidebuilding" // The translation key of the keybinding's category.
        ));

        keyBinding4 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "keybinds.clientsidebuilding.pos2", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_KP_2, // The keycode of the key
                "keybinds.clientsidebuilding")); // The translation key of the keybinding's category.

        ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
            while (keyBinding.wasPressed()) {
                if(!ENABLED) {
                    ENABLED = true;
                } else {
                    ENABLED = false;
                }
                minecraftClient.player.sendMessage(Text.translatable("info.clientsidebuilding.toggled"), false);
            }
            while (keyBinding2.wasPressed()) {
                if(ENABLED) {
                    latestStates.entrySet().forEach(entry -> {
                        ServerBlockPlacer.placeBlockWithState(entry.getKey(), entry.getValue());
                    });

                }
            }
            while (keyBinding3.wasPressed()) {
                knownPos1 = getHoveringBlock(minecraftClient);
                minecraftClient.player.sendMessage(Text.literal(knownPos1.toShortString()));
            }
            while (keyBinding4.wasPressed()) {
                knownPos2 = getHoveringBlock(minecraftClient);
                minecraftClient.player.sendMessage(Text.literal(knownPos2.toShortString()));
            }
        });

    }

    public BlockPos getHoveringBlock(MinecraftClient minecraft) {
        HitResult crosshairTarget = minecraft.crosshairTarget;
        if (!(crosshairTarget instanceof BlockHitResult blockHitResult)) {
            minecraft.player.sendMessage(Text.translatable("info.clientsidebuilding.errblockposfound"));
            return minecraft.player.getBlockPos();
        }
        ClientWorld world = minecraft.world;
        return blockHitResult.getBlockPos();
    }

    public static BlockPos getWithOffset(BlockPos pos) {
        BlockPos blockPos = pos;

        blockPos = blockPos.add(RENDER_OFFSET_X, RENDER_OFFSET_Y, RENDER_OFFSET_Z);

        return blockPos;
    }

    private static void renderBlock(WorldRenderContext context, BlockState state, BlockPos posM) {
        MinecraftClient client = MinecraftClient.getInstance();
        VertexConsumerProvider providers = context.consumers();
        if (client.player != null && providers != null && client.world != null) {

            BlockPos pos = getWithOffset(posM);
            VertexConsumer consumer = providers.getBuffer(RenderLayers.getBlockLayer(state));

            Vec3d camPos = context.camera().getPos();

            MatrixStack matrices = context.matrixStack();
            if (matrices == null)
                return;

            matrices.push();
            matrices.translate(-camPos.x, -camPos.y, -camPos.z);
            matrices.translate(pos.getX(), pos.getY(), pos.getZ());
            client.getBlockRenderManager().renderBlock(state, pos, client.world, matrices, consumer, false, client.world.getRandom());
            matrices.pop();
        }
    }
}

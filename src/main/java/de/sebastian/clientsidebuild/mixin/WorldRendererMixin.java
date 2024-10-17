package de.sebastian.clientsidebuild.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Inject(method = "render", at = @At("HEAD"))
    public void injectCustomRender(RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
        BlockPos fakeBlockPos = new BlockPos(100, 64, 100); // Example coordinates

        BlockState fakeBlockState = Blocks.GOLD_BLOCK.getDefaultState(); // Fake gold block

        // Get the Minecraft client instance
        MinecraftClient client = MinecraftClient.getInstance();

        // Ensure the world is loaded and not null
        if (client.world != null) {
            // Get the block render manager to render the block
            BlockRenderManager blockRenderManager = client.getBlockRenderManager();

            // Render the block manually at the specified position
            //blockRenderManager.renderBlockAsEntity(fakeBlockState, fakeBlockPos, client.world, null, client.getBufferBuilders().getEntityVertexConsumers(), 15728880);
        }
    }
}


package de.sebastian.clientsidebuild.client;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class ServerBlockPlacer {
    public static void placeBlockWithState(BlockPos blockPos, BlockState blockState) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client != null && client.player != null) {
            // Extract the facing direction from the BlockState
            Direction facing = Direction.UP;
            if(blockState.contains(Properties.FACING)) {
                facing = blockState.get(Properties.FACING);
            }

            // Create a BlockHitResult based on the block position and extracted facing direction
            BlockHitResult blockHitResult = new BlockHitResult(
                    client.player.getPos(),
                    facing,      // Use the direction from BlockState
                    blockPos,    // Block position to place
                    false
            );

            // Send the block interaction packet to the server
            PlayerInteractBlockC2SPacket packet = new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, blockHitResult, 0);
            client.getNetworkHandler().sendPacket(packet);

            // Optionally, log or display feedback for testing purposes
            ClientsidebuildClient.logger.info("Placed block at " + blockPos + " with BlockState " + blockState);
        }
    }
}

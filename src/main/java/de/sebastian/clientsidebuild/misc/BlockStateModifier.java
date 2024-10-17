package de.sebastian.clientsidebuild.misc;

import de.sebastian.clientsidebuild.Clientsidebuild;
import de.sebastian.clientsidebuild.client.ClientsidebuildClient;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.command.FillCommand;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Direction;

public class BlockStateModifier {
    private Direction direction;
    private Block block;

    public Direction getDirection() {
        return direction;
    }

    public BlockStateModifier setDirection(Direction direction) {
        this.direction = direction;
        return this;
    }

    public Block getBlock() {
        return block;
    }

    public BlockStateModifier setBlock(Block block) {
        this.block = block;
        return this;
    }

    public BlockState getBlockState() {
        if(this.direction == null) {
            ClientsidebuildClient.logger.error("No Direction in BlockStateModifier!");
            throw new NullPointerException();
        }

        if(this.block == null) {
            ClientsidebuildClient.logger.error("No Block in BlockStateModifier!");
            throw new NullPointerException();
        }

        BlockState blockState = block.getDefaultState();
        blockState.with(Properties.FACING, direction);

        return blockState;
    }

    // Get block state with direction handling
    public BlockState getBlockStateWithBlock(Block with) {
        if (this.direction == null) {
            //ClientsidebuildClient.logger.error("No Direction in BlockStateModifier!");
            throw new NullPointerException();
        }

        BlockState blockState = with.getDefaultState();

        // Check if the block supports the FACING property before setting it
        if (blockState.contains(Properties.FACING)) {
            blockState = blockState.with(Properties.FACING, direction);
        } else {
            //ClientsidebuildClient.logger.warn("Block does not support FACING property: " + with);
        }

        return blockState;
    }

    public static BlockState getBlockStateWithFacingDirectionBlock(Block with) {
        BlockState blockState = with.getDefaultState();
        Direction direction1 = Direction.UP;
        //if(MinecraftClient.getInstance().crosshairTarget instanceof Block)

        //direction1 = with.getPlacementState(new ItemPlacementContext(MinecraftClient.getInstance().player, Hand.MAIN_HAND, MinecraftClient.getInstance().player.getMainHandStack()))

        if (blockState.contains(Properties.FACING)) {
            blockState = blockState.with(Properties.FACING, direction1);
        } else {
            //ClientsidebuildClient.logger.warn("Block does not support FACING property: " + with);
        }

        return blockState;
    }

    @Deprecated
    public enum DirectionProperties {
        HORIZONTAL(Properties.HORIZONTAL_FACING),
        FACING(Properties.FACING),
        EXCLUSIVE_HOPPER(Properties.HOPPER_FACING)
        ;

        final Property state;
        private DirectionProperties(Property realState) {
            state = realState;
        }

        public Boolean canApply(Block blockToTest) {
            if(this == EXCLUSIVE_HOPPER && blockToTest == Blocks.HOPPER) {
                return true;
            }
            if(this == HORIZONTAL && blockToTest == Blocks.HOPPER) {
                return true;
            }
            if(this == EXCLUSIVE_HOPPER && blockToTest == Blocks.HOPPER) {
                return true;
            }
            return false;
        }

        public Property getState() {
            return state;
        }
    }

    public static Direction directionNext(Direction dir) {
        // Switch statement to return the next direction in the custom sequence
        switch (dir) {
            case NORTH:
                return Direction.EAST;
            case EAST:
                return Direction.SOUTH;
            case SOUTH:
                return Direction.WEST;
            case WEST:
                return Direction.UP; // After WEST, move UP
            case UP:
                return Direction.DOWN; // After UP, move DOWN
            case DOWN:
                return Direction.NORTH; // After DOWN, move back to NORTH
            default:
                return dir; // Default case, return the same direction
        }
    }

}

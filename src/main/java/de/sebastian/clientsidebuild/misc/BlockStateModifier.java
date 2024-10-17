package de.sebastian.clientsidebuild.misc;

import de.sebastian.clientsidebuild.Clientsidebuild;
import de.sebastian.clientsidebuild.client.ClientsidebuildClient;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.command.FillCommand;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

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

    public BlockState getBlockStateWithFacingDirectionBlock(Block block, PlayerEntity player) {
        BlockState state = block.getDefaultState();

        // Check for FACING property
        if (state.contains(Properties.FACING)) {
            Direction facingDirection = player.getHorizontalFacing().getOpposite();
            state = state.with(Properties.FACING, facingDirection);
        }
        // Check for HORIZONTAL_FACING property (for blocks like stairs, furnaces)
        else if (state.contains(Properties.HORIZONTAL_FACING)) {
            Direction horizontalFacing = player.getHorizontalFacing().getOpposite();
            state = state.with(Properties.HORIZONTAL_FACING, horizontalFacing);
        }

        // Set the block's axis if it's a block like logs
        if (state.contains(Properties.AXIS)) {
            // Determine axis based on player's pitch (vertical look direction)
            if (player.getPitch(1.0F) < -45) {
                state = state.with(Properties.AXIS, Direction.Axis.Y);  // Looking up
            } else if (player.getPitch(1.0F) > 45) {
                state = state.with(Properties.AXIS, Direction.Axis.Y);  // Looking down
            } else {
                state = state.with(Properties.AXIS, player.getHorizontalFacing().getAxis());  // Horizontal look direction
            }
        }

        return state;
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

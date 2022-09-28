package org.magmafoundation.magma.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.bukkit.craftbukkit.v1_12_R1.block.CraftBlockEntityState;
import org.bukkit.inventory.InventoryHolder;

import java.util.logging.LogManager;

// This class has been taken from Mohist (https://github.com/MohistMC/Mohist)
// and modified to fit Magma needs.
public class InventoryOwner {

    public static InventoryHolder get(TileEntity te) {
        return get(te.world, te.getPos());
    }

    public static InventoryHolder get(IInventory inventory) {
        try {
            return inventory.getOwner();
        } catch (AbstractMethodError e) {
            return (inventory instanceof TileEntity) ? get((TileEntity) inventory) : null;
        }
    }

    public static InventoryHolder get(World world, BlockPos pos) {
        if (world == null) return null;
        // Spigot start
        org.bukkit.block.Block block = world.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ());
        if (block == null) {
            LogManager.getLogManager().getLogger("Magma").info(String.format("No block for owner at %s %d %d %d", world.getWorld(), pos.getX(), pos.getY(), pos.getZ()));
            return null;
        }
        // Spigot end
        org.bukkit.block.BlockState state = block.getState();
        if (state instanceof InventoryHolder) {
            return (InventoryHolder) state;
        } else if (state instanceof CraftBlockEntityState) {
            TileEntity te = ((CraftBlockEntityState) state).getTileEntity();
            if (te instanceof IInventory) {
                return new CraftCustomInventory((IInventory) te);
            }
        }
        return null;
    }
}

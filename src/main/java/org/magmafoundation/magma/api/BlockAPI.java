package org.magmafoundation.magma.api;

import net.minecraft.block.BlockFarmland;
import net.minecraft.util.ResourceLocation;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers;

import java.util.ArrayList;
import java.util.List;

// Parts of this class have been taken from Mohist (https://github.com/MohistMC/Mohist)
public class BlockAPI {

    public static List<ResourceLocation> vanilla_block = new ArrayList<>();

    /**
     * Determine whether {@link Material} is Farmland
     *
     * @param material - the material you want to check
     * @return boolean - whether the provided material is farmland
     */
    public static boolean isFarmland(Material material) {
        return material == Material.SOIL || (isForgeBlock(material) && CraftMagicNumbers.getBlock(material) instanceof BlockFarmland);
    }

    /**
     * Determine whether {@link org.bukkit.block.Block} is Farmland
     *
     * @param block - the block you want to check
     * @return boolean - whether the provided block is farmland
     */
    public static boolean isFarmland(org.bukkit.block.Block block) {
        return isFarmland(block.getType());
    }

    /**
     * Determine whether {@link Material} is a modded block
     *
     * @param material - the material you want to check
     * @return boolean - whether the provided material is a modded block
     */
    public static boolean isForgeBlock(Material material) {
        return material.getMaterialType() == Material.MaterialType.MOD_BLOCK;
    }
}

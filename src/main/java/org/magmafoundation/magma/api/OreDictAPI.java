package org.magmafoundation.magma.api;

import net.minecraftforge.oredict.OreDictionary;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class OreDictAPI {

    /**
     * Get oredict entries as a list of {@link ItemStack}s for a key
     *
     * @param key Name of the oredict entry
     * @return List of {@link ItemStack}s
     */
    public static List<ItemStack> getOreDictItems(String key) {
        List<ItemStack> items = new ArrayList<>();
        OreDictionary.getOres(key).forEach(itemStack -> items.add(CraftItemStack.asBukkitCopy(itemStack)));
        return items;
    }

    /**
     * Add an {@link ItemStack} into an oredict entry
     *
     * @param key Name of the oredict entry
     * @param itemStack The ItemStack you want to add
     */
    public static void addOreDictEntry(String key, ItemStack itemStack) {
        OreDictionary.registerOre(key, CraftItemStack.asNMSCopy(itemStack));
    }
}

/*
 * Magma Server
 * Copyright (C) 2019-2022.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.magmafoundation.magma.forge;

import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.registries.GameData;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.banner.PatternType;
import org.bukkit.craftbukkit.v1_12_R1.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.v1_12_R1.potion.CraftPotionEffectType;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;
import org.magmafoundation.magma.configuration.MagmaConfig;
import org.magmafoundation.magma.configuration.value.values.BooleanValue;
import org.magmafoundation.magma.entity.CraftCustomEntity;

/**
 * ForgeInject
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 02/05/2022 - 7:28 pm
 */
public class ForgeInject {

    private static final BooleanValue magmaDebugPrint = MagmaConfig.instance.debugPrintBukkitMatterials;


    public static void injectForge() {
        addForgeItems();
        addForgeBlocks();
        addForgeEntites();
        addForgeEnchants();
        addForgePotions();
        addForgeBanners();
        addForgeSounds();
    }


    private static void addForgeItems() {
        for (Map.Entry<ResourceLocation, Item> entry : ForgeRegistries.ITEMS.getEntries()) {
            ResourceLocation key = entry.getKey();
            Item item = entry.getValue();
            if (!key.getResourceDomain().equals("minecraft")) {
                String materialName = key.toString().toUpperCase().replaceAll("(:|\\s)", "_").replaceAll("\\W", "");
                Material material = Material.addMaterial(EnumHelper.addEnum(Material.class, materialName, new Class[]{Integer.TYPE, Integer.TYPE, Material.MaterialType.class}, new Object[]{Item.getIdFromItem(item), item.getItemStackLimit(), Material.MaterialType.MOD_ITEM}));
                if (magmaDebugPrint.getValues()) {
                    if (material != null) {
                        MinecraftServer.LOGGER.info(String.format("Injected new Forge item material %s with ID %d.", material.name(), material.getId()));
                    } else {
                        MinecraftServer.LOGGER.info(String.format("Inject item failure %s with ID %d.", materialName, Item.getIdFromItem(item)));
                    }
                }
            }
        }
    }

    private static void addForgeBlocks() {
        for (Material material : Material.values()) {
            if (material.getId() < 256)
                Material.addBlockMaterial(material);
        }
        for (Map.Entry<ResourceLocation, Block> entry : ForgeRegistries.BLOCKS.getEntries()) {
            ResourceLocation key = entry.getKey();
            Block block = entry.getValue();
            if (!key.getResourceDomain().equals("minecraft")) {
                String materialName = key.toString().toUpperCase().replaceAll("(:|\\s)", "_").replaceAll("\\W", "");
                Material material = Material.addBlockMaterial(EnumHelper.addEnum(Material.class, materialName, new Class[]{Integer.TYPE, Material.MaterialType.class}, new Object[]{Block.getIdFromBlock(block), Material.MaterialType.MOD_BLOCK}));
                if (magmaDebugPrint.getValues()) {
                    if (material != null) {
                        MinecraftServer.LOGGER.info(String.format("Injected new Forge block material %s with ID %d.", material.name(), material.getId()));
                    } else {
                        MinecraftServer.LOGGER.info(String.format("Inject block failure %s with ID %d.", materialName, Block.getIdFromBlock(block)));
                    }
                }
            }
        }
    }

    private static void addForgeEntites() {
        Map<String, EntityType> NAME_MAP = ReflectionHelper.getPrivateValue(EntityType.class, null, "NAME_MAP");
        Map<Short, EntityType> ID_MAP = ReflectionHelper.getPrivateValue(EntityType.class, null, "ID_MAP");

        for (Map.Entry<String, Class<? extends Entity>> entity : EntityRegistry.entityClassMap.entrySet()) {
            String entityname = entity.getKey();
            String entityType = entityname.replace("-", "_").toUpperCase();

            int typeId = GameData.getEntityRegistry().getID(EntityRegistry.getEntry(entity.getValue()));
            EntityType bukkitType = EnumHelper.addEnum(EntityType.class, entityType, new Class[]{String.class, Class.class, Integer.TYPE, Boolean.TYPE}, new Object[]{entityname, CraftCustomEntity.class, typeId, false});

            NAME_MAP.put(entityname.toLowerCase(), bukkitType);
            ID_MAP.put((short) typeId, bukkitType);
        }
    }

    private static void addForgeEnchants() {
        for (Object enchantment : Enchantment.REGISTRY) {
            org.bukkit.enchantments.Enchantment.registerEnchantment(new CraftEnchantment((Enchantment) enchantment));
        }
        org.bukkit.enchantments.Enchantment.stopAcceptingRegistrations();
    }

    private static void addForgePotions() {
        for (Object effect : Potion.REGISTRY) {
            PotionEffectType.registerPotionEffectType(new CraftPotionEffectType((Potion) effect));
        }
        PotionEffectType.stopAcceptingRegistrations();
    }

    private static void addForgeBanners() {
        Map<String, PatternType> PATTERN_MAP = ReflectionHelper.getPrivateValue(PatternType.class, null, "byString");
        for (int i = 0; i < BannerPattern.values().length; i++) {
            PatternType patternType = EnumHelper.addEnum(PatternType.class, BannerPattern.values()[i].name(), new Class[]{String.class}, new Object[]{BannerPattern.values()[i].getHashname()});
            PATTERN_MAP.put(BannerPattern.values()[i].getHashname(), patternType);
            if (MagmaConfig.instance.debugPrintBukkitBannerPatterns.getValues()) {
                MinecraftServer.LOGGER.info(String.format("Injected new Forge Banner Pattern %s with ID %s", PatternType.values()[i].toString(), PatternType.values()[i].getIdentifier()));
            }
        }

    }

    private static void addForgeSounds() {
        for (Map.Entry<ResourceLocation, SoundEvent> entry : ForgeRegistries.SOUND_EVENTS.getEntries()) {
            ResourceLocation key = entry.getKey();
            if (!key.getResourceDomain().equals("minecraft")) {
                String soundName = key.toString().toUpperCase().replaceAll("(:|\\s)", "_").replaceAll("\\W", "");
                String soundNameWithOutModName = key.toString().toUpperCase().replaceAll("" + key.getResourceDomain().toUpperCase() + ":", "").replaceAll("(:|\\s)", "_").replaceAll("\\W", "");
                if (MagmaConfig.instance.debugPrintSounds.getValues()) {
                    MinecraftServer.LOGGER.info(String.format("Injecting new Forge sound %s (%s).", soundNameWithOutModName, soundName));
                }

                Sound sound = EnumHelper.addEnum(Sound.class, soundNameWithOutModName, new Class[]{}, new Object[]{});
                if (MagmaConfig.instance.debugPrintSounds.getValues()) {
                    if (sound != null) {
                        MinecraftServer.LOGGER.info(String.format("Added new Enum to " + Sound.class + ": %s.", sound.name()));
                    } else {
                        MinecraftServer.LOGGER.info(String.format("Add sound failure %s (%s).", soundNameWithOutModName, soundName));
                    }
                }
            }
        }
    }

}

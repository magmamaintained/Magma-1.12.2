package org.magmafoundation.magma.api;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;

// This class has been taken from Mohist (https://github.com/MohistMC/Mohist)
public class EntityAPI {

    /**
     * The number of vanilla villager professions
     */
    public static final int VANILLA_PROFESSIONS = 7;

    /**
     * Check if an entity is a modded villager
     *
     * @param entity the entity you want to check
     * @return boolean - whether the villager is modded
     */
    public static boolean isForgeVillager(Entity entity) {
        if (entity instanceof Villager) {
            Villager villager = (Villager) entity;
            Villager.Profession profession = villager.getProfession();
            return profession.ordinal() > VANILLA_PROFESSIONS;
        }
        return false;
    }
}

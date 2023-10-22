package org.magmafoundation.magma.asm;

import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;

public class ASMPatcher {
    public interface IEntityPlayerMP {
        CraftPlayer getBukkitEntity();
    }
}

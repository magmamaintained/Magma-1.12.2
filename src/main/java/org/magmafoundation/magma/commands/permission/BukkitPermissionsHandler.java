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

package org.magmafoundation.magma.commands.permission;

import com.mojang.authlib.GameProfile;

import java.util.*;
import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.IPermissionHandler;
import net.minecraftforge.server.permission.context.IContext;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.util.permissions.DefaultPermissions;
import org.magmafoundation.magma.api.ServerAPI;

public class BukkitPermissionsHandler implements IPermissionHandler {


    @Override
    public void registerNode(String node, DefaultPermissionLevel level, String desc) {
        Permission permission = new Permission(node, desc, fromForge(level));
        DefaultPermissions.registerPermission(permission, false);
    }

    @Override
    public Collection<String> getRegisteredNodes() {
        //Mohist start
        List<String> list = new ArrayList<>();
        for (Permission permission : Bukkit.getPluginManager().getPermissions()) {
            String name = permission.getName();
            list.add(name);
        }
        return list;
        //Mohist end
    }

    @Override
    public boolean hasPermission(GameProfile profile, String node, @Nullable IContext context) {
        //Mohist start
        if (context != null) {
            EntityPlayer player = context.getPlayer();
            if (player != null) {
                return player.getBukkitEntity().hasPermission(node);
            }

        }
        Player player = Bukkit.getServer().getPlayer(profile.getId());
        if (player != null) {
            return player.hasPermission(node);
        } else {
            Permission perm = Bukkit.getServer().getPluginManager().getPermission(node);
            boolean isOp = ServerAPI.getNMSServer().getPlayerList().canSendCommands(profile);
            if (perm != null) {
                return perm.getDefault().getValue(isOp);
            } else {
                return Permission.DEFAULT_PERMISSION.getValue(isOp);
            }
        }
        //Mohist end
    }

    @Override
    public String getNodeDescription(String node) {
        //Mohist start
        Permission permission = Bukkit.getPluginManager().getPermission(node);
        return permission == null ? "" : permission.getDescription();
        //Mohist end
    }

    private PermissionDefault fromForge(DefaultPermissionLevel level) {
        switch (level) {
            case ALL:
                return PermissionDefault.TRUE;
            case OP:
                return PermissionDefault.OP;
            case NONE:
                return PermissionDefault.FALSE;
        }
        return PermissionDefault.FALSE;
    }
}

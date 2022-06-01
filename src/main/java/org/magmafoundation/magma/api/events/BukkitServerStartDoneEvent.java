package org.magmafoundation.magma.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

// Parts of this class have been taken from Mohist (https://github.com/MohistMC/Mohist)
public class BukkitServerStartDoneEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private String time;

    public BukkitServerStartDoneEvent(String time) {
        this.time = time;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public String getStartupTime() {
        return this.time;
    }
}

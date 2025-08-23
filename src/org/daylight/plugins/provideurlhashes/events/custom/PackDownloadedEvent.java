package org.daylight.plugins.provideurlhashes.events.custom;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PackDownloadedEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private String packId;
    private String packUri;
    private String packHash;
    private CommandSender sender;

    public PackDownloadedEvent(String packId, String packUri, String packHash) {
        this.packId = packId;
        this.packUri = packUri;
        this.packHash = packHash;
    }

    public PackDownloadedEvent setSender(CommandSender sender) {
        this.sender = sender;
        return this;
    }

    public String getPackId() {
        return packId;
    }

    public String getPackUri() {
        return packUri;
    }

    public String getPackHash() {
        return packHash;
    }

    public CommandSender getSenderInitiator() {
        return sender;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
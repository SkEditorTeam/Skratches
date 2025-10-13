package me.glicz.skratches;

import ch.njol.skript.Skript;
import me.glicz.skanalyzer.bridge.SkriptBridge;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;

public final class Skratches {
    static {
        Bukkit.getServicesManager().register(
                SkriptBridge.class, new SkriptBridgeImpl(), Skript.getInstance(), ServicePriority.Highest
        );
    }

    private Skratches() {
    }
}

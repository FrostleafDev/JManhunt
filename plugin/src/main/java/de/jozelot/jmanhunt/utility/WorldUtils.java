package de.jozelot.jmanhunt.utility;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;

public class WorldUtils {

    public static void changeTime(World world, String timeSetting) {
        long ticks = switch (timeSetting.toUpperCase()) {
            case "DAY" -> 1000L;
            case "NOON" -> 6000L;
            case "NIGHT" -> 13000L;
            case "MIDNIGHT" -> 18000L;
            default -> {
                try {
                    yield Long.parseLong(timeSetting);
                } catch (NumberFormatException e) {
                    yield 1000L;
                }
            }
        };
        world.setTime(ticks);
    }

    public static void changeWeather(World world, String type) {
        switch (type.toLowerCase()) {
            case "clear" -> world.setStorm(false);
            case "rain" -> world.setStorm(true);
            case "thunder" -> {
                world.setStorm(true);
                world.setThundering(true);
            }
        }
        world.setWeatherDuration(12000);
    }

    public static void applyGamerules() {
        Bukkit.getWorlds().forEach(world -> {
            world.setGameRule(GameRule.LOCATOR_BAR, false);
        });
    }
}

package de.jozelot.jmanhunt.player;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.event.ManhuntTeamAssignEvent;
import de.jozelot.jmanhunt.api.inventory.item.ManhuntItem;
import de.jozelot.jmanhunt.api.inventory.menu.InventoryType;
import de.jozelot.jmanhunt.api.player.ManhuntPlayer;
import de.jozelot.jmanhunt.api.player.ManhuntTeam;
import de.jozelot.jmanhunt.api.player.Sound;
import de.jozelot.jmanhunt.inventory.menu.Menu;
import de.jozelot.jmanhunt.inventory.menu.MenuManager;
import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

public class ManhuntPlayerImpl implements ManhuntPlayer {

    private final JManhunt plugin;

    private ManhuntTeam team;
    private final UUID uuid;
    private String lastKnownName;
    private boolean alive;
    private int lives;
    private int kills;
    private int deaths;
    private boolean online = false;

    private ManhuntPlayer tracking = null;

    public ManhuntPlayerImpl(UUID uuid, String lastKnownName, JManhunt plugin) {
        this.uuid = uuid;
        this.team = ManhuntTeam.NONE;
        this.alive = true;
        this.lives = 1;
        this.kills = 0;
        this.deaths = 0;
        this.plugin = plugin;
        this.lastKnownName = lastKnownName;
    }

    public ManhuntPlayerImpl(UUID uuid, JManhunt plugin) {
        this(uuid, Bukkit.getOfflinePlayer(uuid).getName(), plugin);
    }

    @Override
    public ManhuntTeam getTeam() {
        return team;
    }

    @Override
    public void forceSetTeam(ManhuntTeam team) {
        setTeamIntern(team);
    }

    @Override
    public void setTeam(ManhuntTeam team) {
        if (this.team != ManhuntTeam.NONE && team != ManhuntTeam.NONE) { plugin.getLogger().log(Level.WARNING, "Some plugin accessing the api tried to change the team of " + lastKnownName + " without resetting it!"); return;}
        setTeamIntern(team);
    }

    public void setTeamIntern(ManhuntTeam team) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            ManhuntTeamAssignEvent event = new ManhuntTeamAssignEvent(this, this.team, team);
            Bukkit.getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                return;
            }

            this.team = event.getNewTeam();
            giveCompass();
        });
    }

    @Override
    public UUID getUniqueId() {
        return uuid;
    }

    @Override
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    @Override
    public @NotNull String getLastKnownName() {
        return lastKnownName != null ? lastKnownName : "Unknown";
    }

    public void setName(String name) {
        this.lastKnownName = name;
    }

    @Override
    public boolean isEliminated() {
        return !alive;
    }

    @Override
    public void eliminate() {
        if (!alive) return;

        if (!(team == ManhuntTeam.RUNNER)) return;
        alive = false;
    }

    @Override
    public void revive() {
        if (alive) return;

        if (!(team == ManhuntTeam.RUNNER)) return;
        alive = true;
    }

    @Override
    public int getLives() {
        return lives;
    }

    @Override
    public void setLives(int lives) {
        if (!(team == ManhuntTeam.RUNNER)) return;
        if (this.lives == 0) {
            revive();
        }
        this.lives = lives;
    }

    @Override
    public int getKills() {
        return kills;
    }

    @Override
    public int addKill() {
        return ++kills;
    }

    @Override
    public int removeKill() {
        return --kills;
    }

    @Override
    public void setKills(int kills) {
        this.kills = kills;
    }

    @Override
    public int getDeaths() {
        return deaths;
    }

    @Override
    public int addDeath() {
        return ++deaths;
    }

    @Override
    public int removeDeath() {
        return --deaths;
    }

    @Override
    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    @Override
    public void playSound(@NotNull Sound sound) {
        var sounds = plugin.getBootstrap().getConfigManager().getSounds();
        String soundKey;

        switch (sound) {
            case SUCCESS -> soundKey = sounds.getSuccess();
            case ERROR -> soundKey = sounds.getError();
            case NOTIFY -> soundKey = sounds.getNotify();
            case EXPERIENCE -> soundKey = sounds.getExperience();
            case WARNING -> soundKey = sounds.getWarning();
            case PLING -> soundKey = sounds.getPling();
            case null, default -> soundKey = sounds.getPling();
        }

        try {
            org.bukkit.Sound bukkitSound = org.bukkit.Sound.valueOf(soundKey.toUpperCase());

            getPlayer().playSound(getPlayer().getLocation(), bukkitSound, SoundCategory.UI, 1.0f, 1.0f);

        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid sound name in config: " + soundKey);

            getPlayer().playSound(getPlayer().getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.UI, 1.0f, 1.0f);
        }
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    @Override
    public boolean isOnline() {
       return online;
    }

    @Override
    public Optional<ManhuntPlayer> getTracking() {
        if (tracking == null) return Optional.empty();
        if (!tracking.isOnline()) return Optional.empty();
        return Optional.ofNullable(tracking);
    }

    @Override
    public void setTracking(ManhuntPlayer target) {
        tracking = target;
        var compass = plugin.getBootstrap().getItemManager().getTrackingCompass();
        compass.applyUpdate(compass.getItemStack(), getPlayer());
    }

    @Override
    public void giveCompass() {
        if (getPlayer() == null) {
            if (plugin.getBootstrap().isDebugMode()) plugin.getLogger().info("[Debug] giveCompass canceled: Player object is null for " + lastKnownName);
            return;
        }
        removeCompass();

        if (!plugin.getBootstrap().getConfigManager().getCompass().isEnabled()) {
            if (plugin.getBootstrap().isDebugMode()) plugin.getLogger().info("[Debug] giveCompass canceled: Compass is deactivated in config");
            return;
        }

        boolean isRunning = plugin.getBootstrap().getPhaseManager().isRunning();
        boolean isPaused = plugin.getBootstrap().getPhaseManager().isPaused();
        if (!isRunning && !isPaused) {
            if (plugin.getBootstrap().isDebugMode()) plugin.getLogger().info("[Debug] giveCompass canceled: Game not running (isRunning: " + isRunning + ", isPaused: " + isPaused + ")");
            return;
        }

        if (!(team == ManhuntTeam.HUNTER)) {
            if (plugin.getBootstrap().isDebugMode()) plugin.getLogger().info("[Debug] giveCompass canceled: Player " + lastKnownName + " is not a hunter (Team: " + team + ")");
            return;
        }

        if (plugin.getBootstrap().isDebugMode()) plugin.getLogger().info("[Debug] Give compass to " + lastKnownName);
        getPlayer().getInventory().addItem(plugin.getBootstrap().getItemManager().getTrackingCompass().getItemStack());
    }

    @Override
    public void removeCompass() {
        String compassId = plugin.getBootstrap().getItemManager().getTrackingCompass().getId();
        Player player = getPlayer();

        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);

            if (item == null || !item.hasItemMeta()) continue;

            String id = item.getItemMeta().getPersistentDataContainer()
                    .get(ManhuntItem.ITEM_ID, PersistentDataType.STRING);

            if (compassId.equals(id)) {
                player.getInventory().setItem(i, null);
            }
        }
    }

    @Override
    public void openInventory(InventoryType type) {
        openInventory(type, null, null);
    }

    @Override
    public void openInventory(InventoryType type, InventoryType previousType) {
        openInventory(type, previousType, null);
    }

    @Override
    public void openInventory(InventoryType type, InventoryType previousType, Object data) {
        MenuManager mm = plugin.getBootstrap().getMenuManager();

        Menu menu = mm.createMenu(type, getPlayer(), data);

        if (menu != null) {
            menu.open(this, previousType);
        }
    }

}

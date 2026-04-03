package de.jozelot.jmanhunt.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.game.GameState;
import de.jozelot.jmanhunt.api.game.ManhuntEndReason;
import de.jozelot.jmanhunt.api.player.ManhuntPlayer;
import de.jozelot.jmanhunt.api.player.ManhuntTeam;
import de.jozelot.jmanhunt.api.player.Sound;
import de.jozelot.jmanhunt.commands.manager.IManhuntCommand;
import de.jozelot.jmanhunt.player.ManhuntPlayerImpl;
import de.jozelot.jmanhunt.storage.LangManager;
import de.jozelot.jmanhunt.utility.PlaySoundUtils;
import de.jozelot.jmanhunt.utility.PluginMessages;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class ManhuntCommand implements IManhuntCommand {

    private final JManhunt plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();
    private final LangManager lang;

    public ManhuntCommand(JManhunt plugin) {
        this.plugin = plugin;
        this.lang = plugin.getBootstrap().getLangManager();
    }

    private final Map<UUID, PendingAction> pendingActions = new HashMap<>();
    private final long TIMEOUT = 10000;

    private record PendingAction(ActionType type, long timestamp, Runnable execution) {}

    private enum ActionType {
        RESET, END
    }

    @Override
    public void register(Collection<String> names) {
        if (names == null || names.isEmpty()) return;

        String mainName = names.iterator().next();

        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();

            var mainBuilder = Commands.literal(mainName)
                    .requires(stack -> stack.getSender().hasPermission("jmanhunt.command"))
                    .executes(context -> {
                        PlaySoundUtils.playPling(context.getSource().getSender(), plugin);

                        if (!context.getSource().getSender().hasPermission("jmanhunt.ui")) {
                            PluginMessages.sendHelpMessage(context.getSource().getSender(), plugin);
                            return Command.SINGLE_SUCCESS;
                        }

                        // TODO: OPEN UI
                        return Command.SINGLE_SUCCESS;
                    })

                    // --- SUBCOMMAND: HELP ---
                    .then(Commands.literal("help")
                            .requires(stack -> stack.getSender().hasPermission("jmanhunt.command"))
                            .executes(context -> {
                                PlaySoundUtils.playPling(context.getSource().getSender(), plugin);
                                PluginMessages.sendHelpMessage(context.getSource().getSender(), plugin);
                                return Command.SINGLE_SUCCESS;
                            }))

                    // --- SUBCOMMAND: RELOAD ---
                    .then(Commands.literal("reload")
                            .requires(stack -> stack.getSender().hasPermission("jmanhunt.command.reload"))
                            .executes(context -> {
                                plugin.getBootstrap().reload();
                                context.getSource().getSender().sendMessage(mm.deserialize(lang.format("command-jmanhunt-reload-success", Map.of())));
                                PlaySoundUtils.playSuccess(context.getSource().getSender(), plugin);
                                return Command.SINGLE_SUCCESS;
                            }))

                    // --- SUBCOMMAND: START ---
                    .then(Commands.literal("start")
                            .requires(stack -> stack.getSender().hasPermission("jmanhunt.command.start"))
                            .executes(context -> {
                                GameState state = plugin.getBootstrap().getGameManager().getGameState();
                                String messageKey = "";
                                boolean success = false;

                                switch (state) {
                                    case SETUP -> messageKey = "command-jmanhunt-start-denied-in-setup";
                                    case PRE_GAME -> {
                                        messageKey = "command-jmanhunt-start-success";
                                        plugin.getBootstrap().getPhaseManager().start();
                                        success = true;
                                    }
                                    case RUNNING -> messageKey = "command-jmanhunt-start-denied-already-running";
                                    case PAUSE -> messageKey = "command-jmanhunt-start-denied-paused";
                                    case ENDED -> messageKey = "command-jmanhunt-start-denied-already-over";
                                }

                                context.getSource().getSender().sendMessage(mm.deserialize(lang.format(messageKey, Map.of())));
                                PlaySoundUtils.playSound(context.getSource().getSender(), success ? Sound.SUCCESS : Sound.ERROR, plugin);

                                return Command.SINGLE_SUCCESS;
                            }))

                    // --- SUBCOMMAND: START ---
                    .then(Commands.literal("end")
                            .requires(stack -> stack.getSender().hasPermission("jmanhunt.command.end"))
                            .executes(context -> {
                                GameState state = plugin.getBootstrap().getGameManager().getGameState();
                                String messageKey = "";
                                boolean success = false;

                                switch (state) {
                                    case SETUP -> messageKey = "command-jmanhunt-end-denied-in-setup";
                                    case PRE_GAME -> messageKey = "command-jmanhunt-end-denied-in-pre-game";
                                    case RUNNING, PAUSE -> {
                                        plugin.getBootstrap().getPhaseManager().start();
                                        success = true;
                                        messageKey = ""; // command-jmanhunt-end-success
                                    }
                                    case ENDED -> messageKey = "command-jmanhunt-end-denied-already-over";
                                }

                                PlaySoundUtils.playSound(context.getSource().getSender(), success ? Sound.WARNING : Sound.ERROR, plugin);
                                if (success) {
                                    context.getSource().getSender().sendMessage(mm.deserialize(String.join("<newline>", lang.formatList("command-jmanhunt-end-information", null))));

                                    UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000000");

                                    if (context.getSource().getSender() instanceof Player player) uuid = player.getUniqueId();

                                    pendingActions.put(uuid, new PendingAction(ActionType.END, System.currentTimeMillis(), () -> {
                                        plugin.getBootstrap().getGameManager().getPhaseManager().end(ManhuntEndReason.MANHUNT_CANCELED);
                                    }));

                                } else context.getSource().getSender().sendMessage(mm.deserialize(lang.format(messageKey, Map.of())));
                                return Command.SINGLE_SUCCESS;
                            }))

                    // --- SUBCOMMAND: RESET ---
                    .then(Commands.literal("reset")
                            .requires(stack -> stack.getSender().hasPermission("jmanhunt.command.reset"))
                            .executes(context -> {
                                context.getSource().getSender().sendMessage(mm.deserialize(String.join("<newline>", lang.formatList("command-jmanhunt-reset-information", null))));

                                PlaySoundUtils.playSound(context.getSource().getSender(), Sound.WARNING, plugin);
                                UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000000");

                                if (context.getSource().getSender() instanceof Player player) uuid = player.getUniqueId();

                                pendingActions.put(uuid, new PendingAction(ActionType.RESET, System.currentTimeMillis(), () -> {
                                    plugin.getBootstrap().getGameManager().wipeSystem();
                                }));

                                return Command.SINGLE_SUCCESS;
                            })
                    )

                    // --- SUBCOMMAND: CONFIRM ---
                    .then(Commands.literal("confirm")
                            .requires(stack -> stack.getSender().hasPermission("jmanhunt.command"))
                            .executes(context -> {
                                var sender = context.getSource().getSender();
                                UUID uuid = (sender instanceof Player player) ? player.getUniqueId() : UUID.fromString("00000000-0000-0000-0000-000000000000");

                                PendingAction action = pendingActions.get(uuid);

                                if (action == null) {
                                    sender.sendMessage(mm.deserialize(lang.format("command-jmanhunt-confirm-no-action", null)));
                                    PlaySoundUtils.playSound(sender, Sound.ERROR, plugin);
                                    return Command.SINGLE_SUCCESS;
                                }

                                if (System.currentTimeMillis() - action.timestamp() > TIMEOUT) {
                                    pendingActions.remove(uuid);
                                    sender.sendMessage(mm.deserialize(lang.format("command-jmanhunt-confirm-expired", null)));
                                    PlaySoundUtils.playSound(sender, Sound.ERROR, plugin);
                                    return Command.SINGLE_SUCCESS;
                                }

                                String messageKey = (action.type() == ActionType.RESET) ? "command-jmanhunt-reset-success" : "command-jmanhunt-end-success";
                                sender.sendMessage(mm.deserialize(lang.format(messageKey, null)));
                                PlaySoundUtils.playSound(sender, Sound.SUCCESS, plugin);

                                action.execution().run();
                                pendingActions.remove(uuid);

                                return Command.SINGLE_SUCCESS;
                            }))

                    // --- SUBCOMMAND: TEAM ---
                    .then(Commands.literal("team")
                            .requires(stack -> stack.getSender().hasPermission("jmanhunt.command.teams"))
                            .then(Commands.argument("teamName", StringArgumentType.word())
                                    .suggests((context, builder) -> {
                                        String input = builder.getRemaining().toLowerCase();

                                        List<String> teams = List.of("runner", "hunter", "spectator");

                                        teams.stream()
                                                .filter(team -> team.startsWith(input))
                                                .forEach(builder::suggest);

                                        return builder.buildFuture();
                                    })

                                            // --- ADD ---
                                    .then(Commands.literal("add")
                                            .then(Commands.argument("player", StringArgumentType.word())
                                                    .suggests((context, builder) -> {
                                                        String input = builder.getRemaining().toLowerCase();
                                                        plugin.getBootstrap().getManhuntPlayerManager().getPlayers().stream()
                                                                .filter(mp -> mp.getTeam() == ManhuntTeam.NONE)
                                                                .map(ManhuntPlayer::getLastKnownName)
                                                                .filter(name -> name.toLowerCase().startsWith(input))
                                                                .forEach(builder::suggest);
                                                        return builder.buildFuture();
                                                    })
                                                    .executes(context -> {
                                                        String teamArg = StringArgumentType.getString(context, "teamName").toUpperCase();
                                                        String playerName = StringArgumentType.getString(context, "player");

                                                        try {
                                                            ManhuntTeam newTeam = ManhuntTeam.valueOf(teamArg);
                                                            if (!plugin.getBootstrap().getPhaseManager().canAddToTeam(newTeam)) {
                                                                context.getSource().getSender().sendMessage(mm.deserialize(lang.format("command-jmanhunt-team-add-error-wrong-phase", null)));
                                                                PlaySoundUtils.playError(context.getSource().getSender(), plugin);
                                                            }

                                                            plugin.getBootstrap().getManhuntPlayerManager().getOrCreatePlayerByName(playerName, player -> {

                                                                if (player == null) {
                                                                    context.getSource().getSender().sendMessage(mm.deserialize(
                                                                            lang.format("command-jmanhunt-player-not-found", Map.of("player_name", playerName))
                                                                    ));
                                                                    PlaySoundUtils.playError(context.getSource().getSender(), plugin);
                                                                    return;
                                                                }

                                                                if (player.getTeam() != ManhuntTeam.NONE) {
                                                                    context.getSource().getSender().sendMessage(mm.deserialize(
                                                                            lang.format("command-jmanhunt-team-add-error-already-in-team",
                                                                                    Map.of("player_name", player.getLastKnownName(), "team", player.getTeam().name().toLowerCase()))
                                                                    ));
                                                                    PlaySoundUtils.playError(context.getSource().getSender(), plugin);
                                                                    return;
                                                                }

                                                                player.setTeamIntern(newTeam);
                                                                context.getSource().getSender().sendMessage(mm.deserialize(lang.format("command-jmanhunt-team-add-success",
                                                                        Map.of("player_name", player.getLastKnownName(), "team", newTeam.name().toLowerCase()))));

                                                                PlaySoundUtils.playSuccess(context.getSource().getSender(), plugin);
                                                            });
                                                        } catch (IllegalArgumentException e) {
                                                            PlaySoundUtils.playError(context.getSource().getSender(), plugin);
                                                        }
                                                        return Command.SINGLE_SUCCESS;
                                                    })
                                            )
                                    )

                                    .then(Commands.literal("remove")
                                                    .then(Commands.argument("player", StringArgumentType.word())
                                                            .suggests((context, builder) -> {
                                                                String input = builder.getRemaining().toLowerCase();

                                                                String teamArg;
                                                                try {
                                                                    teamArg = context.getArgument("teamName", String.class);
                                                                } catch (IllegalArgumentException e) {
                                                                    String[] args = context.getInput().split(" ");
                                                                    int removeIndex = -1;
                                                                    for (int i = 0; i < args.length; i++) {
                                                                        if (args[i].equalsIgnoreCase("remove")) {
                                                                            removeIndex = i;
                                                                            break;
                                                                        }
                                                                    }
                                                                    if (removeIndex > 0) {
                                                                        teamArg = args[removeIndex - 1];
                                                                    } else {
                                                                        return builder.buildFuture();
                                                                    }
                                                                }

                                                                if (teamArg == null || teamArg.isEmpty()) return builder.buildFuture();

                                                                final String finalTeam = teamArg.toUpperCase();

                                                                plugin.getBootstrap().getManhuntPlayerManager().getPlayers().stream()
                                                                        .filter(mp -> {
                                                                            try {
                                                                                return mp.getTeam().name().equalsIgnoreCase(finalTeam);
                                                                            } catch (Exception e) {
                                                                                return false;
                                                                            }
                                                                        })
                                                                        .map(ManhuntPlayer::getLastKnownName)
                                                                        .filter(name -> name.toLowerCase().startsWith(input))
                                                                        .forEach(builder::suggest);

                                                                return builder.buildFuture();
                                                            })
                                                            .executes(context -> {
                                                                String playerName = StringArgumentType.getString(context, "player");
                                                                String teamName = StringArgumentType.getString(context, "teamName").toUpperCase();

                                                                try {
                                                                    ManhuntTeam targetTeam = ManhuntTeam.valueOf(teamName);
                                                                    if (!plugin.getBootstrap().getPhaseManager().canRemoveFromTeam(targetTeam)) {
                                                                        context.getSource().getSender().sendMessage(mm.deserialize(lang.format("command-jmanhunt-team-remove-error-wrong-phase", null)));
                                                                        PlaySoundUtils.playError(context.getSource().getSender(), plugin);
                                                                    }

                                                                    plugin.getBootstrap().getManhuntPlayerManager().getPlayerByName(playerName, player1 -> {
                                                                        ManhuntPlayerImpl player = (ManhuntPlayerImpl) player1;
                                                                        if (player == null) {
                                                                            context.getSource().getSender().sendMessage(mm.deserialize(
                                                                                    lang.format("command-jmanhunt-player-not-found", Map.of("player_name", playerName))
                                                                            ));
                                                                            PlaySoundUtils.playError(context.getSource().getSender(), plugin);
                                                                            return;
                                                                        }

                                                                        if (player.getTeam() != targetTeam) {
                                                                            context.getSource().getSender().sendMessage(mm.deserialize(
                                                                                    lang.format("command-jmanhunt-team-remove-error-not-in-team",
                                                                                            Map.of("player_name", player.getLastKnownName(), "team", teamName.toLowerCase()))
                                                                            ));
                                                                            PlaySoundUtils.playError(context.getSource().getSender(), plugin);
                                                                            return;
                                                                        }

                                                                        player.setTeamIntern(ManhuntTeam.NONE);
                                                                        context.getSource().getSender().sendMessage(mm.deserialize(
                                                                                lang.format("command-jmanhunt-team-remove-success",
                                                                                        Map.of("player_name", player.getLastKnownName(), "team", teamName.toLowerCase()))
                                                                        ));
                                                                        PlaySoundUtils.playSuccess(context.getSource().getSender(), plugin);
                                                                    });
                                                                } catch (IllegalArgumentException e) {
                                                                    PlaySoundUtils.playError(context.getSource().getSender(), plugin);
                                                                }

                                                                return Command.SINGLE_SUCCESS;
                                                            })
                                                    )
                                            )

                                    .then(Commands.literal("list")
                                            .executes(context -> {
                                                var manager = plugin.getBootstrap().getManhuntPlayerManager();
                                                var sender = context.getSource().getSender();

                                                String teamArg = StringArgumentType.getString(context, "teamName").toUpperCase();
                                                ManhuntTeam team;
                                                try {
                                                    team = ManhuntTeam.valueOf(teamArg);
                                                } catch (IllegalArgumentException e) {
                                                    PlaySoundUtils.playError(context.getSource().getSender(), plugin);
                                                    return Command.SINGLE_SUCCESS;
                                                }

                                                List<String> playerList = manager.getPlayers().stream()
                                                        .filter(p -> p.getTeam() == team)
                                                        .map(p -> lang.format("command-jmanhunt-team-list-player", Map.of(
                                                                "player_name", p.getLastKnownName(),
                                                                "team", team.name().toLowerCase(),
                                                                "color", p.isOnline() ? "<green>" : "<red>"
                                                        )))
                                                        .toList();

                                                if (playerList.isEmpty()) {
                                                    sender.sendMessage(mm.deserialize(lang.format("command-jmanhunt-team-list-empty", Map.of("team", team.name().toLowerCase()))));
                                                    PlaySoundUtils.playError(context.getSource().getSender(), plugin);
                                                    return Command.SINGLE_SUCCESS;
                                                }

                                                sender.sendMessage(mm.deserialize(lang.format("command-jmanhunt-team-list-header", Map.of("team", team.name()))));

                                                String line = lang.format("command-jmanhunt-team-list-line", Map.of(
                                                        "players", String.join("<gray>, ", playerList)
                                                ));
                                                sender.sendMessage(mm.deserialize(line));

                                                sender.sendMessage(mm.deserialize(lang.format("command-jmanhunt-team-list-footer", null)));
                                                PlaySoundUtils.playPling(context.getSource().getSender(), plugin);
                                                return Command.SINGLE_SUCCESS;
                                            }))

                                    .then(Commands.literal("clear")
                                            .executes(context -> {
                                                String teamArg = StringArgumentType.getString(context, "teamName").toUpperCase();
                                                ManhuntTeam team = ManhuntTeam.valueOf(teamArg);
                                                var manager = plugin.getBootstrap().getManhuntPlayerManager();

                                                int count = 0;
                                                for (ManhuntPlayer player : manager.getPlayers()) {
                                                    ManhuntPlayerImpl p = (ManhuntPlayerImpl) player;
                                                    if (p.getTeam() == team) {
                                                        p.setTeamIntern(ManhuntTeam.NONE);
                                                        count++;
                                                    }
                                                }

                                                context.getSource().getSender().sendMessage(mm.deserialize(
                                                        lang.format("command-jmanhunt-team-clear-success", Map.of(
                                                                "count", String.valueOf(count),
                                                                "team", team.name().toLowerCase()
                                                        ))
                                                ));
                                                PlaySoundUtils.playSuccess(context.getSource().getSender(), plugin);
                                                return Command.SINGLE_SUCCESS;
                                            }))
                    ))

                    // --- SUBCOMMAND: LIST (GLOBAL) ---
                    .then(Commands.literal("list")
                            .executes(context -> {
                                var manager = plugin.getBootstrap().getManhuntPlayerManager();
                                var sender = context.getSource().getSender();

                                boolean anyTeamPopulated = manager.getPlayers().stream()
                                        .anyMatch(p -> p.getTeam() != ManhuntTeam.NONE);

                                if (!anyTeamPopulated) {
                                    sender.sendMessage(mm.deserialize(lang.format("command-jmanhunt-list-empty", null)));
                                    PlaySoundUtils.playError(context.getSource().getSender(), plugin);
                                    return Command.SINGLE_SUCCESS;
                                }

                                sender.sendMessage(mm.deserialize(lang.format("command-jmanhunt-list-header", null)));

                                for (ManhuntTeam team : List.of(ManhuntTeam.RUNNER, ManhuntTeam.HUNTER, ManhuntTeam.SPECTATOR)) {
                                    List<String> playerList = manager.getPlayers().stream()
                                            .filter(p -> p.getTeam() == team)
                                            .map(p -> lang.format("command-jmanhunt-list-player", Map.of(
                                                    "player_name", p.getLastKnownName(),
                                                    "team", team.name().toLowerCase(),
                                                    "color", p.isOnline() ? "<green>" : "<red>"
                                            )))
                                            .toList();

                                    if (!playerList.isEmpty()) {
                                        String line = lang.format("command-jmanhunt-list-line", Map.of(
                                                "team", team.name(),
                                                "players", String.join("<gray>, ", playerList)
                                        ));
                                        sender.sendMessage(mm.deserialize(line));
                                    }
                                }

                                sender.sendMessage(mm.deserialize(lang.format("command-jmanhunt-list-footer", null)));
                                PlaySoundUtils.playPling(context.getSource().getSender(), plugin);
                                return Command.SINGLE_SUCCESS;
                            }));

            LiteralCommandNode<CommandSourceStack> mainNode = mainBuilder.build();

            commands.register(mainNode, "Main JManhunt command");

            for (String name : names) {
                if (name.equalsIgnoreCase(mainName)) continue;

                commands.register(
                        Commands.literal(name)
                                .requires(mainBuilder.getRequirement())
                                .executes(mainBuilder.getCommand())
                                .redirect(mainNode)
                                .build(),
                        "Alias for JManhunt"
                );
            }
        });
    }
}
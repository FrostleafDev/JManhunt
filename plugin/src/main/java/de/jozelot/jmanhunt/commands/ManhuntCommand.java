package de.jozelot.jmanhunt.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.game.GameState;
import de.jozelot.jmanhunt.api.player.ManhuntPlayer;
import de.jozelot.jmanhunt.api.player.Sound;
import de.jozelot.jmanhunt.commands.manager.IManhuntCommand;
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

                                action.execution().run();
                                pendingActions.remove(uuid);

                                String messageKey = (action.type() == ActionType.RESET) ? "command-jmanhunt-reset-success" : "command-jmanhunt-end-success";
                                sender.sendMessage(mm.deserialize(lang.format(messageKey, null)));
                                PlaySoundUtils.playSound(sender, Sound.SUCCESS, plugin);

                                return Command.SINGLE_SUCCESS;
                            }))
                    // --- SUBCOMMAND: TEAM ---
                    .then(Commands.literal("team")
                            .requires(stack -> stack.getSender().hasPermission("jmanhunt.setup.teams"))
                            .then(Commands.argument("teamName", StringArgumentType.word())
                                    .suggests((context, builder) -> {
                                        builder.suggest("runner");
                                        builder.suggest("hunter");
                                        builder.suggest("spectator");
                                        return builder.buildFuture();
                                    })

                                    .then(Commands.literal("add")
                                            .then(Commands.argument("player", ArgumentTypes.player())
                                                    .executes(context -> {
                                                        String team = StringArgumentType.getString(context, "teamName");
                                                        return Command.SINGLE_SUCCESS;
                                                    })
                                            )
                                    )

                                    .then(Commands.literal("remove")
                                            .then(Commands.argument("player", ArgumentTypes.player())
                                                    .executes(context -> {
                                                        return Command.SINGLE_SUCCESS;
                                                    })))

                                    .then(Commands.literal("list")
                                            .executes(context -> {
                                                return Command.SINGLE_SUCCESS;
                                            }))

                                    .then(Commands.literal("clear")
                                            .executes(context -> {
                                                return Command.SINGLE_SUCCESS;
                                            })))
                    );

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
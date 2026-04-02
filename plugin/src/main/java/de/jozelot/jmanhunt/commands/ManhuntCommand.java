package de.jozelot.jmanhunt.commands;

import com.mojang.brigadier.Command;
import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.game.GameState;
import de.jozelot.jmanhunt.api.player.ManhuntPlayer;
import de.jozelot.jmanhunt.api.player.Sound;
import de.jozelot.jmanhunt.player.ManhuntPlayerImpl;
import de.jozelot.jmanhunt.storage.LangManager;
import de.jozelot.jmanhunt.utility.PluginMessages;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class ManhuntCommand {

    private final JManhunt plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();
    private final LangManager lang;

    public ManhuntCommand(JManhunt plugin) {
        this.plugin = plugin;
        this.lang = plugin.getBootstrap().getLangManager();
    }

    public void register() {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();

            commands.register(
                    Commands.literal("jmanhunt")
                            .requires(stack -> stack.getSender().hasPermission("jmanhunt.command"))
                            .executes(stack -> {

                                if (!stack.getSource().getSender().hasPermission("jmanhunt.ui")) {
                                    PluginMessages.sendHelpMessage(stack.getSource().getSender(), plugin);
                                    return Command.SINGLE_SUCCESS;
                                }
                                // TODO: ADD UI

                                return Command.SINGLE_SUCCESS;
                            })

                            // --- SUBCOMMAND: HELP ---
                            .then(Commands.literal("help")
                                    .requires(stack -> stack.getSender().hasPermission("jmanhunt.command"))
                                    .executes(stack -> {

                                        if (!stack.getSource().getSender().hasPermission("jmanhunt.ui")) {
                                            PluginMessages.sendHelpMessage(stack.getSource().getSender(), plugin);
                                            return Command.SINGLE_SUCCESS;
                                        }
                                        // TODO: ADD UI

                                        return Command.SINGLE_SUCCESS;
                                    }))

                            // --- SUBCOMMAND: RELOAD ---
                            .then(Commands.literal("reload")
                                    .requires(stack -> stack.getSender().hasPermission("jmanhunt.command.reload"))
                                    .executes(context -> {

                                        plugin.getBootstrap().reload();
                                        context.getSource().getSender().sendMessage(mm.deserialize(lang.format("command-jmanhunt-reload-success", null)));

                                        if (!(context.getSource().getSender() instanceof Player player)) {
                                            return Command.SINGLE_SUCCESS;
                                        }
                                        ManhuntPlayer manhuntPlayer = plugin.getBootstrap().getManhuntPlayerManager().getPlayer(player);

                                        manhuntPlayer.playSound(Sound.SUCCESS);


                                        return Command.SINGLE_SUCCESS;
                                    })
                            )

                            // --- SUBCOMMAND: START ---
                            .then(Commands.literal("start")
                                    .requires(stack -> stack.getSender().hasPermission("jmanhunt.command.start"))
                                    .executes(context -> {
                                        GameState state = plugin.getBootstrap().getGameManager().getGameState();

                                        String message = "";
                                        boolean success = false;

                                        switch (state) {
                                            case SETUP -> {
                                                message = lang.format("command-jmanhunt-start-denied-in-setup", null);
                                            }
                                            case PRE_GAME -> {
                                                message = lang.format("command-jmanhunt-start-success", null);
                                                plugin.getBootstrap().getPhaseManager().start();
                                                success = true;
                                            }
                                            case RUNNING -> {
                                                message = lang.format("command-jmanhunt-start-denied-already-running", null);
                                            }
                                            case PAUSE -> {
                                                message = lang.format("command-jmanhunt-start-denied-paused", null);
                                            }
                                            case ENDED -> {
                                                message = lang.format("command-jmanhunt-start-denied-already-over", null);
                                            }
                                        }

                                        context.getSource().getSender().sendMessage(mm.deserialize(message));

                                        if (!(context.getSource().getSender() instanceof Player player)) {
                                            return Command.SINGLE_SUCCESS;
                                        }
                                        ManhuntPlayer manhuntPlayer = plugin.getBootstrap().getManhuntPlayerManager().getPlayer(player);

                                        if (success) {
                                            manhuntPlayer.playSound(Sound.SUCCESS);
                                        } else manhuntPlayer.playSound(Sound.ERROR);


                                        return Command.SINGLE_SUCCESS;
                                    })
                            )

                            .build(),
                    "Hauptbefehl für JManhunt",
                    List.of("jm", "manhunt")
            );
        });
    }
}

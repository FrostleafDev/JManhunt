package de.jozelot.jmanhunt.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.game.GameState;
import de.jozelot.jmanhunt.api.game.ManhuntEndReason;
import de.jozelot.jmanhunt.api.player.ManhuntPlayer;
import de.jozelot.jmanhunt.api.player.ManhuntTeam;
import de.jozelot.jmanhunt.commands.manager.IManhuntCommand;
import de.jozelot.jmanhunt.player.ManhuntPlayerManagerImpl;
import de.jozelot.jmanhunt.storage.LangManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public class DebugCommand implements IManhuntCommand {

    private final JManhunt plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();
    private final LangManager lang;

    public DebugCommand(JManhunt plugin) {
        this.plugin = plugin;
        this.lang = plugin.getBootstrap().getLangManager();
    }

    @Override
    public void register(Collection<String> names) {
        if (names == null || names.isEmpty()) return;

        String mainName = names.iterator().next();

        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();

            var mainBuilder = Commands.literal(mainName)
                    .then(Commands.literal("team")
                            .then(Commands.argument("player", ArgumentTypes.player())
                                    .then(Commands.argument("team", StringArgumentType.word())
                                            .suggests((context, builder) -> {
                                                String input = builder.getRemaining().toLowerCase();
                                                for (ManhuntTeam team : ManhuntTeam.values()) {
                                                    if (team.name().toLowerCase().startsWith(input)) {
                                                        builder.suggest(team.name());
                                                    }
                                                }
                                                return builder.buildFuture();
                                            })
                                            .executes(context -> {
                                                var playerResolver = context.getArgument("player", PlayerSelectorArgumentResolver.class);
                                                Player target = playerResolver.resolve(context.getSource()).get(0);
                                                String teamStr = StringArgumentType.getString(context, "team").toUpperCase();

                                                try {
                                                    ManhuntTeam team = ManhuntTeam.valueOf(teamStr);
                                                    ManhuntPlayer mPlayer = plugin.getBootstrap().getManhuntPlayerManager().getPlayer(target.getUniqueId());

                                                    mPlayer.forceSetTeam(team);

                                                    context.getSource().getSender().sendMessage(mm.deserialize(
                                                            "<gray>Set team of <white>" + target.getName() + " <gray>to <white>" + team.name()));
                                                } catch (Exception e) {
                                                    context.getSource().getSender().sendMessage(mm.deserialize("<red>Invalid team: " + teamStr));
                                                }
                                                return Command.SINGLE_SUCCESS;
                                            })
                                    )
                            )
                    )
                    .then(Commands.literal("hunter")
                            .then(Commands.literal("target")
                                    .then(Commands.literal("set")
                                            .then(Commands.argument("targetHunter", ArgumentTypes.player())
                                                    .then(Commands.argument("targetRunner", ArgumentTypes.player())
                                                            .executes(ctx -> {
                                                                var hunterResolver = ctx.getArgument("targetHunter", PlayerSelectorArgumentResolver.class);
                                                                var runnerResolver = ctx.getArgument("targetRunner", PlayerSelectorArgumentResolver.class);

                                                                Player hunter = hunterResolver.resolve(ctx.getSource()).get(0);
                                                                Player runner = runnerResolver.resolve(ctx.getSource()).get(0);

                                                                ManhuntPlayer mHunter = plugin.getBootstrap().getManhuntPlayerManager().getPlayer(hunter.getUniqueId());
                                                                mHunter.setTracking(plugin.getBootstrap().getManhuntPlayerManager().getPlayer(runner));

                                                                return Command.SINGLE_SUCCESS;
                                                            })
                                                    )
                                            )
                                    )
                                    .then(Commands.literal("get")
                                            .then(Commands.argument("targetHunter", ArgumentTypes.player())
                                                    .executes(context -> {
                                                        var hunterResolver = context.getArgument("targetHunter", PlayerSelectorArgumentResolver.class);
                                                        Player hunter = hunterResolver.resolve(context.getSource()).get(0);
                                                        ManhuntPlayer mHunter = plugin.getBootstrap().getManhuntPlayerManager().getPlayer(hunter.getUniqueId());
                                                        context.getSource().getSender().sendMessage(mm.deserialize("<gray>Target from <white>" + hunter.getName() + " <gray>: <white>" + mHunter.getTracking().get().getPlayer().getName()));
                                                        return Command.SINGLE_SUCCESS;
                                                    })
                                            )
                            )))
                    .then(Commands.literal("state")
                            .then(Commands.literal("get")
                                    .executes(context -> {
                                        context.getSource().getSender().sendMessage(mm.deserialize("<gray>Current Gamestate: <white>" + plugin.getBootstrap().getGameManager().getGameState().name()));
                                        return Command.SINGLE_SUCCESS;
                                    }))
                            .then(Commands.literal("set")
                                    .then(Commands.argument("state", StringArgumentType.word())
                                            .suggests((context, builder) -> {
                                                String input = builder.getRemaining().toLowerCase();
                                                for (GameState state : GameState.values()) {
                                                    if (!state.name().startsWith(input)) continue;
                                                    builder.suggest(state.name());
                                                }
                                                return builder.buildFuture();
                                            })
                                            .executes(context -> {
                                                String state = StringArgumentType.getString(context, "state").toUpperCase();
                                                try {
                                                    GameState gameState = GameState.valueOf(state);
                                                    plugin.getBootstrap().getGameManager().setGameState(gameState);
                                                    context.getSource().getSender().sendMessage(mm.deserialize("<gray>Changed Gamestate to: <white>" + state));
                                                } catch (Exception e) {
                                                    context.getSource().getSender().sendMessage(mm.deserialize("<gray>Can't change Gamestate to: <white>" + state + "<newline><red>" + e));
                                                }
                                                return Command.SINGLE_SUCCESS;
                                            })
                                    )
                            ))
                    .then(Commands.literal("timer")
                            .then(Commands.literal("start")
                                    .executes(context -> {
                                        plugin.getBootstrap().getTimerManager().start();
                                        context.getSource().getSender().sendMessage(mm.deserialize("<gray>Timer was started"));
                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                            .then(Commands.literal("stop")
                                    .executes(context -> {
                                        plugin.getBootstrap().getTimerManager().stop();
                                        context.getSource().getSender().sendMessage(mm.deserialize("<gray>Timer was stopped"));
                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                            .then(Commands.literal("get")
                                    .executes(context -> {
                                        context.getSource().getSender().sendMessage(mm.deserialize("<gray>Timer is currently at: <white>" + plugin.getBootstrap().getTimerManager().getTimer().getElapsedSeconds() + "s"));
                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                            .then(Commands.literal("set")
                                    .then(Commands.argument("timerSet", IntegerArgumentType.integer(0))
                                            .suggests((context, builder) -> {
                                                List<Integer> timerSets = List.of(0, 60, 3600, 7200);
                                                timerSets.forEach(builder::suggest);
                                                return builder.buildFuture();
                                            })
                                            .executes(context -> {
                                                plugin.getBootstrap().getTimerManager().getTimer().setElapsedSeconds(IntegerArgumentType.getInteger(context, "timerSet"));
                                                context.getSource().getSender().sendMessage(mm.deserialize("<gray>Timer set to: <white>" + IntegerArgumentType.getInteger(context, "timerSet")+ "s"));
                                                return Command.SINGLE_SUCCESS;
                                            })
                                    )
                            )
                            .then(Commands.literal("add")
                                    .then(Commands.argument("timerAdd", IntegerArgumentType.integer(0))
                                            .suggests((context, builder) -> {
                                                List<Integer> timerSets = List.of(0, 60, 3600, 7200);
                                                timerSets.forEach(builder::suggest);
                                                return builder.buildFuture();
                                            })
                                            .executes(context -> {
                                                plugin.getBootstrap().getTimerManager().getTimer().setElapsedSeconds(plugin.getBootstrap().getTimerManager().getTimer().getElapsedSeconds() + IntegerArgumentType.getInteger(context, "timerAdd"));
                                                context.getSource().getSender().sendMessage(mm.deserialize("<gray>Timer added seconds: <white>" + IntegerArgumentType.getInteger(context, "timerAdd")+ "s<gray>. Now: <white>" + plugin.getBootstrap().getTimerManager().getTimer().getElapsedSeconds() + "s"));
                                                return Command.SINGLE_SUCCESS;
                                            })
                                    )
                            )
                            .then(Commands.literal("remove")
                                    .then(Commands.argument("timerRemove", IntegerArgumentType.integer(0))
                                            .suggests((context, builder) -> {
                                                List<Integer> timerSets = List.of(0, 60, 3600, 7200);
                                                timerSets.forEach(builder::suggest);
                                                return builder.buildFuture();
                                            })
                                            .executes(context -> {
                                                plugin.getBootstrap().getTimerManager().getTimer().setElapsedSeconds(plugin.getBootstrap().getTimerManager().getTimer().getElapsedSeconds() - IntegerArgumentType.getInteger(context, "timerRemove"));
                                                context.getSource().getSender().sendMessage(mm.deserialize("<gray>Timer removed seconds: <white>" + IntegerArgumentType.getInteger(context, "timerRemove")+ "s<gray>. Now: <white>" + plugin.getBootstrap().getTimerManager().getTimer().getElapsedSeconds() + "s"));
                                                return Command.SINGLE_SUCCESS;
                                            })
                                    )
                            )
                    )
                    .then(Commands.literal("game")
                            .then(Commands.literal("start")
                                    .executes(context -> {
                                        plugin.getBootstrap().getPhaseManager().start();
                                        return Command.SINGLE_SUCCESS;
                                    }))
                            .then(Commands.literal("pause")
                                    .executes(context -> {
                                        plugin.getBootstrap().getPhaseManager().pause();
                                        return Command.SINGLE_SUCCESS;
                                    }))
                            .then(Commands.literal("resume")
                                    .executes(context -> {
                                        plugin.getBootstrap().getPhaseManager().resume();
                                        return Command.SINGLE_SUCCESS;
                                    }))
                            .then(Commands.literal("open")
                                    .executes(context -> {
                                        plugin.getBootstrap().getPhaseManager().open();
                                        return Command.SINGLE_SUCCESS;
                                    }))

                            .then(Commands.literal("close")
                                    .executes(context -> {
                                        plugin.getBootstrap().getPhaseManager().close();
                                        return Command.SINGLE_SUCCESS;
                                    }))
                            .then(Commands.literal("end")
                                    .executes(context -> {
                                        plugin.getBootstrap().getPhaseManager().end(ManhuntEndReason.MANHUNT_CANCELED);
                                        return Command.SINGLE_SUCCESS;
                                    }))
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
            };
        });
    }
}

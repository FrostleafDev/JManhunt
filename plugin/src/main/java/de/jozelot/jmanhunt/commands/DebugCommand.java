package de.jozelot.jmanhunt.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.game.ManhuntEndReason;
import de.jozelot.jmanhunt.commands.manager.IManhuntCommand;
import de.jozelot.jmanhunt.player.ManhuntPlayerManagerImpl;
import de.jozelot.jmanhunt.storage.LangManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.Collection;

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
                    ;

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

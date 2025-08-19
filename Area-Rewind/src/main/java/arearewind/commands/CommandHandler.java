package arearewind.commands;

import arearewind.commands.admin.*;
import arearewind.commands.analysis.*;
import arearewind.commands.area.*;
import arearewind.commands.backup.*;
import arearewind.commands.export.*;
import arearewind.commands.info.*;
import arearewind.commands.maintenance.*;
import arearewind.commands.management.*;
import arearewind.commands.navigation.*;
import arearewind.commands.registry.CommandRegistry;
import arearewind.commands.utility.*;
import arearewind.listeners.PlayerInteractionListener;
import arearewind.managers.*;
import arearewind.util.ConfigurationManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class CommandHandler implements CommandExecutor, TabCompleter {

        private final CommandRegistry commandRegistry;
        private final Map<UUID, Long> lastUsage = new HashMap<>();
        private final RestoreBlockCommand restoreBlockCommand;
        private final ConfigCommand configCommand;
        private static final long RATE_LIMIT_MS = 1000;

        public CommandHandler(JavaPlugin plugin, AreaManager areaManager, BackupManager backupManager,
                        GUIManager guiManager, VisualizationManager visualizationManager,
                        PermissionManager permissionManager, ConfigurationManager configManager,
                        FileManager fileManager, IntervalManager intervalManager) {
                this.commandRegistry = new CommandRegistry();
                this.restoreBlockCommand = new RestoreBlockCommand(plugin, areaManager, backupManager, guiManager,
                                visualizationManager, permissionManager, configManager, fileManager, intervalManager);
                this.configCommand = new ConfigCommand(plugin, areaManager, backupManager, guiManager,
                                visualizationManager, permissionManager, configManager, fileManager, intervalManager);
                registerCommands(plugin, areaManager, backupManager, guiManager, visualizationManager,
                                permissionManager, configManager, fileManager, intervalManager);
        }

        private void registerCommands(JavaPlugin plugin, AreaManager areaManager, BackupManager backupManager,
                        GUIManager guiManager, VisualizationManager visualizationManager,
                        PermissionManager permissionManager, ConfigurationManager configManager,
                        FileManager fileManager, IntervalManager intervalManager) {

                commandRegistry.registerCommand(new Pos1Command(plugin, areaManager, backupManager, guiManager,
                                visualizationManager, permissionManager, configManager, fileManager, intervalManager));
                commandRegistry.registerCommand(new Pos2Command(plugin, areaManager, backupManager, guiManager,
                                visualizationManager, permissionManager, configManager, fileManager, intervalManager));
                commandRegistry.registerCommand(new SaveCommand(plugin, areaManager, backupManager, guiManager,
                                visualizationManager, permissionManager, configManager, fileManager, intervalManager));
                commandRegistry.registerCommand(new ContractCommand(plugin, areaManager, backupManager, guiManager,
                                visualizationManager, permissionManager, configManager, fileManager, intervalManager));
                commandRegistry.registerCommand(new ExpandCommand(plugin, areaManager, backupManager, guiManager,
                                visualizationManager, permissionManager, configManager, fileManager, intervalManager));
                commandRegistry.registerCommand(new DeleteCommand(plugin, areaManager, backupManager, guiManager,
                                visualizationManager, permissionManager, configManager, fileManager, intervalManager));
                commandRegistry.registerCommand(new RenameCommand(plugin, areaManager, backupManager, guiManager,
                                visualizationManager, permissionManager, configManager, fileManager, intervalManager));
                commandRegistry.registerCommand(new SpeedCommand(plugin, areaManager, backupManager, guiManager,
                                visualizationManager, permissionManager, configManager, fileManager, intervalManager));

                commandRegistry.registerCommand(new BackupCommand(plugin, areaManager, backupManager, guiManager,
                                visualizationManager, permissionManager, configManager, fileManager, intervalManager));
                commandRegistry.registerCommand(new RestoreCommand(plugin, areaManager, backupManager, guiManager,
                                visualizationManager, permissionManager, configManager, fileManager, intervalManager));
                commandRegistry.registerCommand(restoreBlockCommand);
                commandRegistry.registerCommand(new RollbackCommand(plugin, areaManager, backupManager, guiManager,
                                visualizationManager, permissionManager, configManager, fileManager, intervalManager));
                commandRegistry.registerCommand(new UndoCommand(plugin, areaManager, backupManager, guiManager,
                                visualizationManager, permissionManager, configManager, fileManager, intervalManager));
                commandRegistry.registerCommand(new RedoCommand(plugin, areaManager, backupManager, guiManager,
                                visualizationManager, permissionManager, configManager, fileManager, intervalManager));

                commandRegistry.registerCommand(new ListCommand(plugin, areaManager, backupManager, guiManager,
                                visualizationManager, permissionManager, configManager, fileManager, intervalManager));
                commandRegistry.registerCommand(new InfoCommand(plugin, areaManager, backupManager, guiManager,
                                visualizationManager, permissionManager, configManager, fileManager, intervalManager));
                commandRegistry.registerCommand(new HistoryCommand(plugin, areaManager, backupManager, guiManager,
                                visualizationManager, permissionManager, configManager, fileManager, intervalManager));

                commandRegistry.registerCommand(new TeleportCommand(plugin, areaManager, backupManager, guiManager,
                                visualizationManager, permissionManager, configManager, fileManager, intervalManager));

                commandRegistry.registerCommand(new TrustCommand(plugin, areaManager, backupManager, guiManager,
                                visualizationManager, permissionManager, configManager, fileManager, intervalManager));
                commandRegistry.registerCommand(new UntrustCommand(plugin, areaManager, backupManager, guiManager,
                                visualizationManager, permissionManager, configManager, fileManager, intervalManager));
                commandRegistry.registerCommand(new PermissionCommand(plugin, areaManager, backupManager, guiManager,
                                visualizationManager, permissionManager, configManager, fileManager, intervalManager));
                commandRegistry.registerCommand(new IntervalCommand(plugin, areaManager, backupManager, guiManager,
                                visualizationManager, permissionManager, configManager, fileManager, intervalManager));
                commandRegistry.registerCommand(new SetIconCommand(plugin, areaManager, backupManager, guiManager,
                                visualizationManager, permissionManager, configManager, fileManager, intervalManager));

                commandRegistry.registerCommand(new CleanupCommand(plugin, areaManager, backupManager, guiManager,
                                visualizationManager, permissionManager, configManager, fileManager, intervalManager));

                commandRegistry.registerCommand(new HelpCommand(plugin, areaManager, backupManager, guiManager,
                                visualizationManager, permissionManager, configManager, fileManager, intervalManager,
                                commandRegistry));
                commandRegistry.registerCommand(new GUICommand(plugin, areaManager, backupManager, guiManager,
                                visualizationManager, permissionManager, configManager, fileManager, intervalManager));
                commandRegistry.registerCommand(new StatusCommand(plugin, areaManager, backupManager, guiManager,
                                visualizationManager, permissionManager, configManager, fileManager, intervalManager));
                commandRegistry.registerCommand(new ToolCommand(plugin, areaManager, backupManager, guiManager,
                                visualizationManager, permissionManager, configManager, fileManager, intervalManager));
                commandRegistry.registerCommand(configCommand);
                commandRegistry.registerCommand(new PreviewCommand(plugin, areaManager, backupManager, guiManager,
                                visualizationManager, permissionManager, configManager, fileManager, intervalManager));
                commandRegistry.registerCommand(new HideCommand(plugin, areaManager, backupManager, guiManager,
                                visualizationManager, permissionManager, configManager, fileManager, intervalManager));

                commandRegistry.registerCommand(new ReloadCommand(plugin, areaManager, backupManager, guiManager,
                                visualizationManager, permissionManager, configManager, fileManager, intervalManager));

                commandRegistry.registerCommand(new ScanCommand(plugin, areaManager, backupManager, guiManager,
                                visualizationManager, permissionManager, configManager, fileManager, intervalManager));
                commandRegistry.registerCommand(new DiffCommand(plugin, areaManager, backupManager, guiManager,
                                visualizationManager, permissionManager, configManager, fileManager, intervalManager));

                commandRegistry.registerCommand(new ExportCommand(plugin, areaManager, backupManager, guiManager,
                                visualizationManager, permissionManager, configManager, fileManager, intervalManager));
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
                if (!(sender instanceof Player)) {
                        if (args.length > 0 && args[0].equalsIgnoreCase("restoreblock")) {
                                String[] commandArgs = Arrays.copyOfRange(args, 1, args.length);
                                return restoreBlockCommand.executeForCommandBlock(sender, commandArgs);
                        } else {
                                sender.sendMessage(ChatColor.RED + "Only players can use this command!");
                                sender.sendMessage(ChatColor.GRAY
                                                + "Command blocks can use: /rewind restoreblock <area> <backup_id> [world]");
                                return true;
                        }
                }

                Player player = (Player) sender;

                if (isRateLimited(player)) {
                        player.sendMessage(ChatColor.RED + "Please wait before using another command!");
                        return true;
                }

                if (args.length == 0) {
                        return commandRegistry.executeCommand(player, "gui", new String[0]);
                }

                String subCommand = args[0].toLowerCase();
                String[] commandArgs = Arrays.copyOfRange(args, 1, args.length);
                arearewind.commands.base.Command registeredCommand = commandRegistry.getCommand(subCommand);
                if (registeredCommand != null) {
                        updateLastUsage(player);
                        return commandRegistry.executeCommand(player, subCommand, commandArgs);
                }

                player.sendMessage(ChatColor.RED + "Unknown command: " + subCommand);
                player.sendMessage(ChatColor.GRAY + "Use " + ChatColor.GREEN + "/rewind help" +
                                ChatColor.GRAY + " for available commands");

                updateLastUsage(player);
                return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
                if (!(sender instanceof Player)) {
                        return new ArrayList<>();
                }

                Player player = (Player) sender;

                if (args.length == 1) {
                        return commandRegistry.getCommandCompletions(args[0]);
                }

                if (args.length > 1) {
                        String subCommand = args[0].toLowerCase();
                        String[] commandArgs = Arrays.copyOfRange(args, 1, args.length);
                        return commandRegistry.getTabCompletions(player, subCommand, commandArgs);
                }

                return new ArrayList<>();
        }

        private boolean isRateLimited(Player player) {
                long now = System.currentTimeMillis();
                long last = lastUsage.getOrDefault(player.getUniqueId(), 0L);
                return now - last < RATE_LIMIT_MS;
        }

        private void updateLastUsage(Player player) {
                lastUsage.put(player.getUniqueId(), System.currentTimeMillis());
        }

        public void setPlayerInteractionListener(PlayerInteractionListener listener) {
                configCommand.setPlayerInteractionListener(listener);
        }

        public CommandRegistry getCommandRegistry() {
                return commandRegistry;
        }
}

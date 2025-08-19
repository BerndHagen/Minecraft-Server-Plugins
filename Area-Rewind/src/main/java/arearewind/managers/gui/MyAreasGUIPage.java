package arearewind.managers.gui;

import arearewind.data.ProtectedArea;
import arearewind.managers.AreaManager;
import arearewind.managers.BackupManager;
import arearewind.managers.GUIManager;
import arearewind.managers.IntervalManager;
import arearewind.managers.PermissionManager;
import arearewind.managers.gui.GUIPaginationHelper.PaginationInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyAreasGUIPage implements IGUIPage {

    private final GUIManager guiManager;
    private final AreaManager areaManager;
    private final BackupManager backupManager;
    private final PermissionManager permissionManager;
    private final IntervalManager intervalManager;
    private static final int ITEMS_PER_PAGE = 35;
    private static final int NAVIGATION_ROW_START = 45;
    private static final int FIRST_PAGE = 0;

    public MyAreasGUIPage(GUIManager guiManager, AreaManager areaManager,
            BackupManager backupManager, PermissionManager permissionManager, IntervalManager intervalManager) {
        this.guiManager = guiManager;
        this.areaManager = areaManager;
        this.backupManager = backupManager;
        this.permissionManager = permissionManager;
        this.intervalManager = intervalManager;
    }

    @Override
    public void openGUI(Player player) {
        openGUI(player, FIRST_PAGE);
    }

    @Override
    public void openGUI(Player player, int page) {
        if (!permissionManager.canUseGUI(player)) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use the GUI!");
            return;
        }

        List<Map.Entry<String, ProtectedArea>> myAreas = getMyAreas(player);

        PaginationInfo paginationInfo = GUIPaginationHelper.calculatePagination(
                myAreas.size(), ITEMS_PER_PAGE, page);

        GUIPaginationHelper.updatePaginationData(player.getUniqueId(),
                paginationInfo.getCurrentPage(), paginationInfo.getMaxPage(), getPageType(), null);

        String title = ChatColor.DARK_GREEN + "My Protected Areas" +
                (paginationInfo.getMaxPage() > 0
                        ? ChatColor.DARK_GREEN + " (" + (paginationInfo.getCurrentPage() + 1) + "/"
                                + (paginationInfo.getMaxPage() + 1) + ")"
                        : "");
        Inventory gui = Bukkit.createInventory(null, 54, title);

        int slot = 0;
        for (int i = paginationInfo.getStartIndex(); i < paginationInfo.getEndIndex(); i++) {
            Map.Entry<String, ProtectedArea> entry = myAreas.get(i);
            String areaName = entry.getKey();
            ProtectedArea area = entry.getValue();

            ItemStack item = area.getIconItem() != null ? area.getIconItem().clone()
                    : new ItemStack(Material.GRASS_BLOCK);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + areaName);

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Owner: " + ChatColor.YELLOW + "You");
            lore.add(ChatColor.GRAY + "Size: " + area.getSize() + " blocks");
            lore.add(ChatColor.GRAY + "Backups: " + backupManager.getBackupHistory(areaName).size());

            var intervalConfig = intervalManager.getIntervalConfig(areaName);
            if (intervalConfig != null) {
                lore.add(ChatColor.AQUA + "Auto-Restore: " + ChatColor.GREEN + intervalConfig.minutes + "m (#"
                        + intervalConfig.backupId + ")");
            } else {
                lore.add(ChatColor.AQUA + "Auto-Restore: " + ChatColor.RED + "Inactive");
            }

            lore.add("");
            lore.add(ChatColor.YELLOW + "Click: Manage Area & Backups");
            lore.add(ChatColor.YELLOW + "Middle Click: Set Icon");

            meta.setLore(lore);
            item.setItemMeta(meta);
            gui.setItem(slot++, item);
        }

        if (paginationInfo.getMaxPage() > 0) {
            GUIPaginationHelper.addPaginationButtons(gui, paginationInfo,
                    NAVIGATION_ROW_START, NAVIGATION_ROW_START + 8, NAVIGATION_ROW_START + 4);
        }

        addNavigationItems(gui, paginationInfo);
        player.openInventory(gui);
        guiManager.registerOpenGUI(player, getPageType());
    }

    @Override
    public void handleClick(Player player, InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null || !item.hasItemMeta())
            return;

        String displayName = ChatColor.stripColor(item.getItemMeta().getDisplayName());

        GUIPaginationHelper.PaginationAction paginationAction = GUIPaginationHelper.checkPaginationClick(item);
        if (paginationAction != GUIPaginationHelper.PaginationAction.NONE) {
            handlePaginationAction(player, paginationAction);
            return;
        }

        if (displayName.equals("Close")) {
            player.closeInventory();
            return;
        } else if (displayName.equals("Refresh")) {
            player.closeInventory();
            GUIPaginationHelper.PaginationData paginationData = GUIPaginationHelper
                    .getPaginationData(player.getUniqueId(), getPageType(), null);
            openGUI(player, paginationData.getCurrentPage());
            return;
        } else if (displayName.equals("Settings")) {
            player.closeInventory();
            guiManager.openSettingsGUI(player);
            return;
        } else if (displayName.equals("All Areas")) {
            player.closeInventory();
            guiManager.openAreasGUI(player);
            return;
        }

        ProtectedArea area = areaManager.getArea(displayName);
        if (area == null)
            return;

        if (event.isLeftClick()) {
            player.closeInventory();
            guiManager.openBackupsGUI(player, displayName);
        } else if (event.getClick().name().contains("MIDDLE")) {
            player.closeInventory();
            guiManager.openMaterialSelector(player, "area", displayName, null);
        }
    }

    @Override
    public String getPageType() {
        return "my-areas";
    }

    @Override
    public void handlePaginationAction(Player player, GUIPaginationHelper.PaginationAction action) {
        GUIPaginationHelper.PaginationData paginationData = GUIPaginationHelper.getPaginationData(player.getUniqueId(),
                getPageType(), null);

        int newPage = paginationData.getCurrentPage();

        switch (action) {
            case PREVIOUS:
                if (newPage > 0) {
                    newPage--;
                }
                break;
            case NEXT:
                if (newPage < paginationData.getMaxPage()) {
                    newPage++;
                }
                break;
            default:
                return;
        }

        player.closeInventory();
        openGUI(player, newPage);
    }


    private List<Map.Entry<String, ProtectedArea>> getMyAreas(Player player) {
        List<Map.Entry<String, ProtectedArea>> myAreas = new ArrayList<>();

        for (Map.Entry<String, ProtectedArea> entry : areaManager.getProtectedAreas().entrySet()) {
            ProtectedArea area = entry.getValue();

            if (area.getOwner().equals(player.getUniqueId())) {
                myAreas.add(entry);
            }
        }

        myAreas.sort((o1, o2) -> Long.compare(
                o2.getValue().getCreationDate(),
                o1.getValue().getCreationDate()));

        return myAreas;
    }

    private void addNavigationItems(Inventory gui, PaginationInfo paginationInfo) {
        int refreshSlot = paginationInfo.getMaxPage() > 0 ? NAVIGATION_ROW_START + 1 : NAVIGATION_ROW_START;
        int allAreasSlot = paginationInfo.getMaxPage() > 0 ? NAVIGATION_ROW_START + 2 : NAVIGATION_ROW_START + 1;
        int closeSlot = paginationInfo.getMaxPage() > 0 ? NAVIGATION_ROW_START + 3 : NAVIGATION_ROW_START + 4;
        int settingsSlot = paginationInfo.getMaxPage() > 0 ? NAVIGATION_ROW_START + 7 : NAVIGATION_ROW_START + 8;

        ItemStack refreshItem = new ItemStack(Material.EMERALD);
        ItemMeta refreshMeta = refreshItem.getItemMeta();
        refreshMeta.setDisplayName(ChatColor.GREEN + "Refresh");
        refreshItem.setItemMeta(refreshMeta);
        gui.setItem(refreshSlot, refreshItem);

        ItemStack allAreasItem = new ItemStack(Material.CYAN_CONCRETE);
        ItemMeta allAreasMeta = allAreasItem.getItemMeta();
        allAreasMeta.setDisplayName(ChatColor.AQUA + "All Areas");
        List<String> allAreasLore = new ArrayList<>();
        allAreasLore.add(ChatColor.GRAY + "Show all accessible areas");
        allAreasLore.add(ChatColor.YELLOW + "Click to switch");
        allAreasMeta.setLore(allAreasLore);
        allAreasItem.setItemMeta(allAreasMeta);
        gui.setItem(allAreasSlot, allAreasItem);

        ItemStack closeItem = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = closeItem.getItemMeta();
        closeMeta.setDisplayName(ChatColor.RED + "Close");
        closeItem.setItemMeta(closeMeta);
        gui.setItem(closeSlot, closeItem);

        ItemStack settingsItem = new ItemStack(Material.COMPARATOR);
        ItemMeta settingsMeta = settingsItem.getItemMeta();
        settingsMeta.setDisplayName(ChatColor.YELLOW + "Settings");
        settingsItem.setItemMeta(settingsMeta);
        gui.setItem(settingsSlot, settingsItem);
    }
}

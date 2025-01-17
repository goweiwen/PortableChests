package cyanogenoid.portablechests;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void on(InventoryPickupItemEvent e) {
        if (!(PortableChests.isContainer(e.getInventory()) || !PortableChests.isPortableContainer(e.getItem().getItemStack()))) return;
        if (!PortableChests.canNestItemStack(e.getItem().getItemStack())) e.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(InventoryMoveItemEvent e) {
        if (e.getDestination().getType().equals(InventoryType.SHULKER_BOX)) return;
        if (!(PortableChests.isContainer(e.getDestination()) || !PortableChests.isPortableContainer(e.getItem()))) return;
        if (!PortableChests.canNestItemStack(e.getItem())) e.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(InventoryClickEvent e) {
        if (e.getInventory().getType().equals(InventoryType.SHULKER_BOX)) return;
        if (!PortableChests.isContainer(e.getInventory())) return;
        ItemStack itemStack = this.findMovingItemStack(e);
        if (!PortableChests.isPortableContainer(itemStack)) return;
        if (this.isPlayerMovingItemStackToContainer(e) && !PortableChests.canNestItemStack(itemStack, e.getWhoClicked())) e.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(InventoryDragEvent e) {
        if (e.getInventory().getType().equals(InventoryType.SHULKER_BOX)) return;
        if (!PortableChests.isContainer(e.getInventory())) return;
        if (!PortableChests.isPortableContainer(e.getOldCursor())) return;
        if (e.getRawSlots().stream().anyMatch(slotID -> slotID <= e.getInventory().getSize() && !PortableChests.canNestItemStack(e.getOldCursor(), e.getWhoClicked()))) e.setCancelled(true);
    }


    private boolean isPlayerMovingItemStackToContainer(InventoryClickEvent e) {
        return e.getClickedInventory() != null && (e.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY) && e.getClickedInventory().getType().equals(InventoryType.PLAYER)) ||
                ((e.getAction().equals(InventoryAction.HOTBAR_MOVE_AND_READD) || e.getAction().equals(InventoryAction.HOTBAR_SWAP))  && PortableChests.isContainer(e.getClickedInventory())) ||
                ((e.getAction().equals(InventoryAction.PLACE_ALL) || e.getAction().equals(InventoryAction.PLACE_ONE) || e.getAction().equals(InventoryAction.SWAP_WITH_CURSOR)) && PortableChests.isContainer(e.getClickedInventory()));
    }

    private ItemStack findMovingItemStack(InventoryClickEvent e) {
        if (e.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) return e.getCurrentItem();
        if (e.getAction().equals(InventoryAction.HOTBAR_MOVE_AND_READD) || e.getAction().equals(InventoryAction.HOTBAR_SWAP)) return e.getWhoClicked().getInventory().getItem(e.getHotbarButton());
        if (e.getAction().equals(InventoryAction.PLACE_ALL) || e.getAction().equals(InventoryAction.PLACE_ONE) || e.getAction().equals(InventoryAction.SWAP_WITH_CURSOR)) return e.getCursor();
        return null;
    }
}

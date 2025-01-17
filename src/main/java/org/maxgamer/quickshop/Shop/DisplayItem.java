package org.maxgamer.quickshop.Shop;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.*;
import org.maxgamer.quickshop.QuickShop;
import org.maxgamer.quickshop.Util.Util;

/**
 * @author Netherfoam A display item, that spawns a block above the chest and
 * cannot be interacted with.
 */
public interface DisplayItem {
    /**
     * Spawn new Displays
     */
    void spawn();

    /**
     * Respawn the displays, if it not exist, it will spawn new one.
     */
    void respawn();

    /**
     * Add the protect flags for entity or entity's hand item.
     * Target entity will got protect by QuickShop
     *
     * @param entity Target entity
     */
    void safeGuard(Entity entity);

    /**
     * Check target Entity is or not a QuickShop display Entity.
     * @param entity Target entity
     * @return Is or not
     */
    boolean checkIsShopEntity(Entity entity);

    /**
     * Remove this shop's display in the whole world.(Not whole server)
     * @return Success
     */
    boolean removeDupe();

    /**
     * Remove the display entity.
     */
    void remove();


    /**
     * Get display should at location.
     * Not display current location.
     * @return Should at
     */
    Location getDisplayLocation();

    /**
     * Get the display entity
     * @return Target entity
     */
    Entity getDisplay();

    /**
     * Check the display is or not moved.
     * @return Moved
     */
    boolean checkDisplayIsMoved();

    /**
     * Check the display is or not need respawn
     * @return Need
     */
    boolean checkDisplayNeedRegen();

    /**
     * Fix the display moved issue.
     */
    void fixDisplayMoved();

    /**
     * Fix display need respawn issue.
     */
    void fixDisplayNeedRegen();

    /**
     * Check the display is or not already spawned
     *
     * @return Spawned
     */
    boolean isSpawned();

    /**
     * Create a new itemStack with protect flag.
     * @param itemStack Old itemStack
     * @return New itemStack with protect flag.
     */
    static ItemStack createGuardItemStack(@NotNull ItemStack itemStack, @NotNull Shop shop) {
        itemStack = itemStack.clone();
        ItemMeta iMeta = itemStack.getItemMeta();
        if (QuickShop.instance.getConfig().getBoolean("shop.display-item-use-name")) {
            iMeta.setDisplayName("QuickShop DisplayItem");
        }
        java.util.List<String> lore = new ArrayList<String>();
        Gson gson = new Gson();
        ShopProtectionFlag shopProtectionFlag = new ShopProtectionFlag(shop.getLocation().toString(), Util
                .serialize(shop.getItem()));
        String protectFlag = gson.toJson(shopProtectionFlag);
        for (int i = 0; i < 21; i++) {
            lore.add(protectFlag); //Create 20 lines lore to make sure no stupid plugin accident remove mark.
        }
        iMeta.setLore(lore);
        itemStack.setItemMeta(iMeta);
        return itemStack;
    }

    /**
     * Check the itemStack is contains protect flag.
     * @param itemStack Target ItemStack
     * @return Contains protect flag.
     */
    static boolean checkIsGuardItemStack(@NotNull ItemStack itemStack) {
        itemStack = itemStack.clone();
        if (!itemStack.hasItemMeta())
            return false;
        ItemMeta iMeta = itemStack.getItemMeta();
        if (iMeta.hasDisplayName()) {
            if (iMeta.getDisplayName().toLowerCase().contains("quickshop displayitem"))
                return true;
        }
        if (!iMeta.hasLore())
            return false;
            List<String> lores = iMeta.getLore();
        Gson gson = new Gson();
            for (String lore : lores) {
                try {
                    ShopProtectionFlag shopProtectionFlag = gson.fromJson(lore, ShopProtectionFlag.class);
                    if (shopProtectionFlag == null)
                        continue;
                    if (shopProtectionFlag.getMark() == null)
                        continue;
                    if (shopProtectionFlag.getMark().equals("QuickShop DisplayItem"))
                        return true;
                } catch (JsonSyntaxException e) {
                    //Ignore
                }
            }
        return false;
    }

    /**
     * Check the itemStack is target shop's display
     *
     * @param itemStack Target ItemStack
     * @param shop      Target shop
     * @return Is target shop's display
     */
    static boolean checkIsTargetShopDisplay(@NotNull ItemStack itemStack, @NotNull Shop shop) {
        itemStack = itemStack.clone();
        if (!itemStack.hasItemMeta())
            return false;
        ItemMeta iMeta = itemStack.getItemMeta();
        if (!iMeta.hasLore())
            return false;
        List<String> lores = iMeta.getLore();
        Gson gson = new Gson();
        for (String lore : lores) {
            try {
                ShopProtectionFlag shopProtectionFlag = gson.fromJson(lore, ShopProtectionFlag.class);
                if (shopProtectionFlag == null)
                    continue;
                if (shopProtectionFlag.getMark() == null)
                    continue;
                if (shopProtectionFlag.getShopLocation().equals(shop.getLocation().toString()))
                    return true;
            } catch (JsonSyntaxException e) {
                //Ignore
            }
        }
        return false;
    }

    /**
     * Get plugin now is using which one DisplayType
     * @return Using displayType.
     */
    static DisplayType getNowUsing() {
        return DisplayType.fromID(QuickShop.instance.getConfig().getInt("shop.display-type"));
    }

}
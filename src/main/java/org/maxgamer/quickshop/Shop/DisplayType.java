package org.maxgamer.quickshop.Shop;

import org.jetbrains.annotations.*;

public enum DisplayType {
    UNKNOWN(-1), REALITEM(0), ARMORSTAND(1), VIRTUALITEM(2);
    private int id;

    DisplayType(int id) {
        this.id = id;
    }

    public static DisplayType fromID(int id) {
        for (DisplayType type : DisplayType.values()) {
            if (type.id == id) {
                return type;
            }
        }
        return null;
    }

    public static int toID(@NotNull DisplayType displayType) {
        return displayType.id;
    }

    public int toID() {
        return id;
    }

    public static DisplayType typeIs(@Nullable DisplayItem displayItem) {
        if (displayItem instanceof RealDisplayItem)
            return REALITEM;
        if (displayItem instanceof ArmorStandDisplayItem)
            return ARMORSTAND;
        return UNKNOWN;
    }
}

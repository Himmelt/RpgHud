package net.spellcraftgaming.rpghud;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public enum EnumOptionsRPG {

    HUD_ENABLED("HUD_ENABLED", 0, "Enabled RPG-Hud", false, true),
    UPDATE_INFO("UPDATE_INFO", 1, "Recieve Update Infos", false, true),
    SHOW_ARMOR("SHOW_ARMOR", 2, "Show Armor", false, true),
    SHOW_NUMBERS_HEALTH("SHOW_NUMBERS_HEALTH", 3, "Show Health Value", false, true),
    SHOW_NUMBERS_STAMINA("SHOW_NUMBERS_STAMINA", 4, "Show Stamina Value", false, true),
    SHOW_NUMBERS_EXPERIENCE("SHOW_NUMBERS_EXPERIENCE", 5, "Show Experience Value", false, true),
    ENABLE_STATUS("ENABLE_STATUS", 6, "Enable Status", false, true),
    ENABLE_STATUS_BLINK("ENABLE_STATUS_BLINK", 7, "Enable Status Blink", false, true),
    RENDER_PLAYER_FACE("RENDER_PLAYER_FACE", 8, "Render Player Face", false, true),
    HUD_TYPE("HUD_TYPE", 9, "Hud Type", false, false),
    SIZE_ARMOR("SIZE_ARMOR", 10, "Armor Size", false, false),
    SIZE_STATUS("SIZE_STATUS", 11, "Status Size", false, false),
    SIZE_WIDGET("SIZE_WIDGET", 12, "Widget Size", false, false),
    COLOR_HEALTH("COLOR_HEALTH", 13, "Health Color", false, false),
    COLOR_STAMINA("COLOR_STAMINA", 14, "Stamina Color", false, false),
    COLOR_AIR("COLOR_AIR", 15, "Air Color", false, false),
    COLOR_EXPERIENCE("COLOR_EXPERIENCE", 16, "Experience Color", false, false),
    COLOR_JUMPBAR("COLOR_JUMPBAR", 17, "Jumpbar Color", false, false);

    private final boolean enumFloat;
    private final boolean enumBoolean;
    private final String enumString;

    public static EnumOptionsRPG getEnumOptions(int ordinal) {
        EnumOptionsRPG[] aenumoptions = values();
        for (EnumOptionsRPG enumoptions : aenumoptions) {
            if (enumoptions.returnEnumOrdinal() == ordinal) {
                return enumoptions;
            }
        }
        return null;
    }

    EnumOptionsRPG(String var1, int var2, String par3Str, boolean par4, boolean par5) {
        this.enumString = par3Str;
        this.enumFloat = par4;
        this.enumBoolean = par5;
    }

    public boolean getEnumFloat() {
        return this.enumFloat;
    }

    public boolean getEnumBoolean() {
        return this.enumBoolean;
    }

    public int returnEnumOrdinal() {
        return this.ordinal();
    }

    public String getEnumString() {
        return this.enumString;
    }
}

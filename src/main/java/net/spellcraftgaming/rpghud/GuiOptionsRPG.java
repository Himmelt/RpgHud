package net.spellcraftgaming.rpghud;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiOptionsRPG extends GuiScreen {

    private static final EnumOptionsRPG[] widgetOptions = new EnumOptionsRPG[]{EnumOptionsRPG.HUD_ENABLED, EnumOptionsRPG.UPDATE_INFO, EnumOptionsRPG.HUD_TYPE, EnumOptionsRPG.RENDER_PLAYER_FACE};
    private static final EnumOptionsRPG[] barOptions = new EnumOptionsRPG[]{EnumOptionsRPG.SHOW_NUMBERS_EXPERIENCE, EnumOptionsRPG.SHOW_NUMBERS_STAMINA, EnumOptionsRPG.SHOW_NUMBERS_HEALTH, EnumOptionsRPG.COLOR_EXPERIENCE, EnumOptionsRPG.COLOR_AIR, EnumOptionsRPG.COLOR_HEALTH, EnumOptionsRPG.COLOR_STAMINA, EnumOptionsRPG.COLOR_JUMPBAR};
    private static final EnumOptionsRPG[] armorHelperOptions = new EnumOptionsRPG[]{EnumOptionsRPG.SHOW_ARMOR, EnumOptionsRPG.SIZE_ARMOR};
    private static final EnumOptionsRPG[] statusEffectOptions = new EnumOptionsRPG[]{EnumOptionsRPG.ENABLE_STATUS, EnumOptionsRPG.ENABLE_STATUS_BLINK, EnumOptionsRPG.SIZE_STATUS};
    private final GuiScreen parentScreen;
    private final GameSettingsRPG options;
    private String screenTitle = "RPG-Hud Options";
    public final int MAIN = 0;
    public final int WIDGET = 1;
    public final int BARS = 2;
    public final int ARMORHELPER = 3;
    public final int STATUSEFFECTS = 4;
    public int guiType = 0;

    public GuiOptionsRPG(GuiScreen guiScreen, GameSettingsRPG settingsRPG, int par3GuiType) {
        this.guiType = par3GuiType;
        if (guiScreen instanceof GuiOptions) {
            this.parentScreen = new GuiIngameMenu();
        } else {
            this.parentScreen = guiScreen;
        }
        this.options = settingsRPG;
    }

    public void initGui() {
        int i = 0;
        this.screenTitle = I18n.format("options.title");
        EnumOptionsRPG[] enumOptionsRPGS = null;
        switch (this.guiType) {
            case 0:
                this.screenTitle = "RPG Hud Navigation Menu";
                break;
            case 1:
                enumOptionsRPGS = widgetOptions;
                this.screenTitle = "RPG Hud Main Settings";
                break;
            case 2:
                enumOptionsRPGS = barOptions;
                this.screenTitle = "RPG Hud Bar Settings";
                break;
            case 3:
                enumOptionsRPGS = armorHelperOptions;
                this.screenTitle = "RPG Hud Armor Helper Settings";
                break;
            case 4:
                enumOptionsRPGS = statusEffectOptions;
                this.screenTitle = "RPG Hud Status Effect Settings";
        }

        if (this.guiType != 0) {
            for (EnumOptionsRPG optionsRPG : enumOptionsRPGS) {
                GuiSmallButtonRPG smallButtonRPG = new GuiSmallButtonRPG(optionsRPG.returnEnumOrdinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), optionsRPG, this.options.getKeyBinding(optionsRPG));
                this.buttonList.add(smallButtonRPG);
                ++i;
            }
        } else {
            this.buttonList.add(new GuiButton(50, this.width / 2 - 100, this.height / 6 - 12, "Edit Main Settings"));
            this.buttonList.add(new GuiButton(51, this.width / 2 - 100, this.height / 6 - 12 + 24, "Edit Bar Settings"));
            this.buttonList.add(new GuiButton(52, this.width / 2 - 100, this.height / 6 - 12 + 48, "Edit Armor Helper Settings"));
            this.buttonList.add(new GuiButton(53, this.width / 2 - 100, this.height / 6 - 12 + 72, "Edit Status Effect Settings"));
        }

        this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, I18n.format("gui.done")));
    }

    protected void actionPerformed(GuiButton guiButton) {
        if (guiButton.enabled) {
            if (guiButton.id < 100 && guiButton instanceof GuiSmallButtonRPG) {
                this.options.setOptionValue(((GuiSmallButtonRPG) guiButton).returnEnumOptions(), 1);
                guiButton.displayString = this.options.getKeyBinding(EnumOptionsRPG.getEnumOptions(guiButton.id));
            }

            if (guiButton.id == 200) {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(this.parentScreen);
            }

            if (guiButton.id == 50) {
                mc.displayGuiScreen(new GuiOptionsRPG(this, RPGHud.rpg_settings, 1));
            }

            if (guiButton.id == 51) {
                mc.displayGuiScreen(new GuiOptionsRPG(this, RPGHud.rpg_settings, 2));
            }

            if (guiButton.id == 52) {
                mc.displayGuiScreen(new GuiOptionsRPG(this, RPGHud.rpg_settings, 3));
            }

            if (guiButton.id == 53) {
                mc.displayGuiScreen(new GuiOptionsRPG(this, RPGHud.rpg_settings, 4));
            }
        }
    }

    public void drawScreen(int par1, int par2, float par3) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.screenTitle, this.width / 2, 15, 16777215);
        super.drawScreen(par1, par2, par3);
    }
}

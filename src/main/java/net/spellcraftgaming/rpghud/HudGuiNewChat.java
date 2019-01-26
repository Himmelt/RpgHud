package net.spellcraftgaming.rpghud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.IChatComponent;

public class HudGuiNewChat extends GuiNewChat {

    private final Minecraft mc;

    public HudGuiNewChat(Minecraft mc) {
        super(mc);
        this.mc = mc;
    }

    public IChatComponent func_146236_a(int width, int height) {
        boolean creative = mc.thePlayer.capabilities.isCreativeMode;
        ScaledResolution scale = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        height -= (creative || RPGHud.rpg_settings.hudtype != 3 ? 0 : 38) * scale.getScaleFactor();
        return super.func_146236_a(width, height);
    }
}

package net.spellcraftgaming.rpghud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.resources.I18n;

public class GuiIngameMenuRPG extends GuiIngameMenu {

    public void initGui() {
        super.initGui();
        this.buttonList.add(new GuiButton(10, this.width / 2 - 100, this.height / 4 + 96 + 38, 200, 20, I18n.format("RPGHud Options")));
    }

    protected void actionPerformed(GuiButton par1GuiButton) {
        super.actionPerformed(par1GuiButton);
        switch (par1GuiButton.id) {
            case 10:
                Minecraft var10000 = this.mc;
                GuiOptionsRPG var10001 = new GuiOptionsRPG(this, RPGHud.rpg_settings, 0);
                var10000.displayGuiScreen(var10001);
            default:
        }
    }
}

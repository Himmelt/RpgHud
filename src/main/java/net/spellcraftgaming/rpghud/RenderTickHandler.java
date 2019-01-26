package net.spellcraftgaming.rpghud;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;

@SideOnly(Side.CLIENT)
public class RenderTickHandler {

    private boolean started = false;
    private boolean update = false;
    private boolean updated = false;
    private Minecraft mc;

    public RenderTickHandler(Minecraft mc) {
        this.mc = mc;
    }

    @SubscribeEvent
    public void onRenderTick(RenderTickEvent event) {
        if (event.phase == Phase.START) {
            this.tickStart();
        }
    }

    public void tickStart() {
        if (!started) {
            if (RPGHud.rpg_settings.hud_enabled) {
                this.mc.ingameGUI = new GuiInGameForgeRPG(this.mc);
            }
            this.started = true;
        }

        if (!update && this.mc.currentScreen instanceof GuiMainMenu && !updated) {
            this.update = true;
        }

        if (this.mc.currentScreen instanceof GuiIngameMenu) {
            this.mc.displayGuiScreen(new GuiIngameMenuRPG());
        }
    }
}

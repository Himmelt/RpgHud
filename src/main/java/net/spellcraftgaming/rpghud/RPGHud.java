package net.spellcraftgaming.rpghud;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.client.Minecraft;

import static net.spellcraftgaming.rpghud.RPGHud.*;

@Mod(
        modid = MODID,
        version = VERSION,
        name = NAME
)
public class RPGHud {

    public static final String MODID = "rpghud";
    public static final String VERSION = "1.7.10-2.5.1";
    public static final String NAME = "RPG Hud";
    public static GameSettingsRPG rpg_settings;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        if (FMLCommonHandler.instance().getSide().isClient()) {
            rpg_settings = new GameSettingsRPG(Minecraft.getMinecraft(), Minecraft.getMinecraft().mcDataDir);
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(new RenderTickHandler(Minecraft.getMinecraft()));
    }
}

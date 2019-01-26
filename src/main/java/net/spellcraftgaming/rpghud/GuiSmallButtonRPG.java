package net.spellcraftgaming.rpghud;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;

@SideOnly(Side.CLIENT)
public class GuiSmallButtonRPG extends GuiButton {

    private final EnumOptionsRPG enumOptions;

    public GuiSmallButtonRPG(int par1, int par2, int par3, String par4Str) {
        this(par1, par2, par3, null, par4Str);
    }

    public GuiSmallButtonRPG(int par1, int par2, int par3, int par4, int par5, String par6Str) {
        super(par1, par2, par3, par4, par5, par6Str);
        this.enumOptions = null;
    }

    public GuiSmallButtonRPG(int par1, int par2, int par3, EnumOptionsRPG par4EnumOptions, String par5Str) {
        super(par1, par2, par3, 150, 20, par5Str);
        this.enumOptions = par4EnumOptions;
    }

    public EnumOptionsRPG returnEnumOptions() {
        return this.enumOptions;
    }
}

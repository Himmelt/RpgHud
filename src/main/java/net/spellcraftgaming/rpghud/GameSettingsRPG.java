package net.spellcraftgaming.rpghud;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.GuiIngameForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

@SideOnly(Side.CLIENT)
public class GameSettingsRPG {

    private static final Logger logger = LogManager.getLogger();
    private static final String[] COLOR = new String[]{"Red", "Blue", "Green", "Yellow", "White", "Grey"};
    private static final String[] SIZE = new String[]{"Small", "Default", "Large"};
    private static final String[] HUD_TYPE = new String[]{"Default", "Extended Widget", "Full Texture", "Hotbar Widget"};
    public boolean hud_enabled = true;
    public final boolean update = false;
    public boolean show_armor = true;
    public boolean show_numbers_health = true;
    public boolean show_numbers_stamina = true;
    public boolean show_numbers_experience = true;
    public boolean enable_status = true;
    public boolean enable_status_blink = true;
    public boolean render_player_face = true;
    public int hudtype = 0;
    public int size_widget = 1;
    public int size_armor = 1;
    public int size_status = 1;
    public int color_health = 0;
    public int color_stamina = 2;
    public int color_air = 1;
    public int color_experience = 3;
    public int color_jumpbar = 5;
    public int status_direction = 0;
    protected Minecraft mc;
    private File optionsFile;


    public GameSettingsRPG(Minecraft par1Minecraft, File par2File) {
        this.mc = par1Minecraft;
        this.optionsFile = new File(par2File, "optionsRPG.txt");
        this.loadOptions();
    }

    public void setOptionValue(EnumOptionsRPG par1EnumOptions, int par2) {
        if (par1EnumOptions == EnumOptionsRPG.HUD_ENABLED) {
            this.hud_enabled = !this.hud_enabled;
            if (!this.hud_enabled) {
                this.mc.ingameGUI = new GuiIngameForge(this.mc);
            } else {
                this.mc.ingameGUI = new GuiInGameForgeRPG(this.mc);
            }
        }

        if (par1EnumOptions == EnumOptionsRPG.SHOW_ARMOR) {
            this.show_armor = !this.show_armor;
        }

        if (par1EnumOptions == EnumOptionsRPG.SHOW_NUMBERS_HEALTH) {
            this.show_numbers_health = !this.show_numbers_health;
        }

        if (par1EnumOptions == EnumOptionsRPG.SHOW_NUMBERS_STAMINA) {
            this.show_numbers_stamina = !this.show_numbers_stamina;
        }

        if (par1EnumOptions == EnumOptionsRPG.SHOW_NUMBERS_EXPERIENCE) {
            this.show_numbers_experience = !this.show_numbers_experience;
        }

        if (par1EnumOptions == EnumOptionsRPG.ENABLE_STATUS) {
            this.enable_status = !this.enable_status;
        }

        if (par1EnumOptions == EnumOptionsRPG.ENABLE_STATUS_BLINK) {
            this.enable_status_blink = !this.enable_status_blink;
        }

        if (par1EnumOptions == EnumOptionsRPG.RENDER_PLAYER_FACE) {
            this.render_player_face = !this.render_player_face;
        }

        if (par1EnumOptions == EnumOptionsRPG.COLOR_HEALTH) {
            if (this.color_health >= 5) {
                this.color_health = 0;
            } else {
                this.color_health += par2;
            }
        }

        if (par1EnumOptions == EnumOptionsRPG.COLOR_STAMINA) {
            if (this.color_stamina >= 5) {
                this.color_stamina = 0;
            } else {
                this.color_stamina += par2;
            }
        }

        if (par1EnumOptions == EnumOptionsRPG.COLOR_AIR) {
            if (this.color_air >= 5) {
                this.color_air = 0;
            } else {
                this.color_air += par2;
            }

            System.out.println("air");
        }

        if (par1EnumOptions == EnumOptionsRPG.COLOR_EXPERIENCE) {
            if (this.color_experience >= 5) {
                this.color_experience = 0;
            } else {
                this.color_experience += par2;
            }
        }

        if (par1EnumOptions == EnumOptionsRPG.COLOR_JUMPBAR) {
            if (this.color_jumpbar >= 5) {
                this.color_jumpbar = 0;
            } else {
                this.color_jumpbar += par2;
            }
        }

        if (par1EnumOptions == EnumOptionsRPG.HUD_TYPE) {
            if (this.hudtype >= 3) {
                this.hudtype = 0;
            } else {
                this.hudtype += par2;
            }
        }

        if (par1EnumOptions == EnumOptionsRPG.SIZE_ARMOR) {
            if (this.size_armor >= 1) {
                this.size_armor = 0;
            } else {
                this.size_armor += par2;
            }
        }

        if (par1EnumOptions == EnumOptionsRPG.SIZE_STATUS) {
            if (this.size_status >= 1) {
                this.size_status = 0;
            } else {
                this.size_status += par2;
            }
        }

        this.saveOptions();
    }

    public boolean getOptionOrdinalValue(EnumOptionsRPG optionsRPG) {
        switch (optionsRPG.ordinal()) {
            case 1:
                return this.hud_enabled;
            case 2:
                return this.update;
            case 3:
                return this.show_armor;
            case 4:
                return this.show_numbers_health;
            case 5:
                return this.show_numbers_stamina;
            case 6:
                return this.show_numbers_experience;
            case 7:
                return this.enable_status;
            case 8:
                return this.enable_status_blink;
            case 9:
                return this.render_player_face;
            default:
                return false;
        }
    }

    private static String getTranslation(String[] par0ArrayOfStr, int par1) {
        if (par1 < 0 || par1 >= par0ArrayOfStr.length) {
            par1 = 0;
        }

        return I18n.format(par0ArrayOfStr[par1]);
    }

    public void loadOptions() {
        try {
            if (!this.optionsFile.exists()) {
                return;
            }

            BufferedReader var5 = new BufferedReader(new FileReader(this.optionsFile));
            String s = "";

            while ((s = var5.readLine()) != null) {
                try {
                    String[] var4 = s.split(":");
                    if (var4[0].equals("size_widget")) {
                        this.size_widget = Integer.parseInt(var4[1]);
                    }

                    if (var4[0].equals("size_armor")) {
                        this.size_armor = Integer.parseInt(var4[1]);
                    }

                    if (var4[0].equals("size_status")) {
                        this.size_status = Integer.parseInt(var4[1]);
                    }

                    if (var4[0].equals("color_health")) {
                        this.color_health = Integer.parseInt(var4[1]);
                    }

                    if (var4[0].equals("color_stamina")) {
                        this.color_stamina = Integer.parseInt(var4[1]);
                    }

                    if (var4[0].equals("color_air")) {
                        this.color_air = Integer.parseInt(var4[1]);
                    }

                    if (var4[0].equals("color_experience")) {
                        this.color_experience = Integer.parseInt(var4[1]);
                    }

                    if (var4[0].equals("color_jumpbar")) {
                        this.color_jumpbar = Integer.parseInt(var4[1]);
                    }

                    if (var4[0].equals("hudtype")) {
                        this.hudtype = Integer.parseInt(var4[1]);
                    }

                    if (var4[0].equals("hud_enabled")) {
                        this.hud_enabled = var4[1].equals("true");
                    }

                    if (var4[0].equals("show_armor")) {
                        this.show_armor = var4[1].equals("true");
                    }

                    if (var4[0].equals("enable_status")) {
                        this.enable_status = var4[1].equals("true");
                    }

                    if (var4[0].equals("enable_status_blink")) {
                        this.enable_status_blink = var4[1].equals("true");
                    }

                    if (var4[0].equals("render_player_face")) {
                        this.render_player_face = var4[1].equals("true");
                    }

                    if (var4[0].equals("show_numbers_health")) {
                        this.show_numbers_health = var4[1].equals("true");
                    }

                    if (var4[0].equals("show_numbers_stamina")) {
                        this.show_numbers_stamina = var4[1].equals("true");
                    }

                    if (var4[0].equals("show_numbers_experience")) {
                        this.show_numbers_experience = var4[1].equals("true");
                    }
                } catch (Exception var41) {
                    logger.warn("Skipping bad option: " + s);
                }
            }

            var5.close();
        } catch (Exception var51) {
            logger.error("Failed to load options", var51);
            var51.printStackTrace();
        }

    }

    private float parseFloat(String par1Str) {
        return par1Str.equals("true") ? 1.0F : (par1Str.equals("false") ? 0.0F : Float.parseFloat(par1Str));
    }

    public void saveOptions() {
        if (!FMLClientHandler.instance().isLoading()) {
            try {
                PrintWriter var2 = new PrintWriter(new FileWriter(this.optionsFile));
                var2.println("size_armor:" + this.size_armor);
                var2.println("size_status:" + this.size_status);
                var2.println("size_widget:" + this.size_widget);
                var2.println("color_health:" + this.color_health);
                var2.println("color_air:" + this.color_air);
                var2.println("color_stamina:" + this.color_stamina);
                var2.println("color_experience:" + this.color_experience);
                var2.println("color_jumpbar:" + this.color_jumpbar);
                var2.println("hudtype:" + this.hudtype);
                var2.println("hud_enabled:" + this.hud_enabled);
                var2.println("update_info:" + this.update);
                var2.println("show_armor:" + this.show_armor);
                var2.println("show_numbers_health:" + this.show_numbers_health);
                var2.println("show_numbers_stamina:" + this.show_numbers_stamina);
                var2.println("show_numbers_experience:" + this.show_numbers_experience);
                var2.println("enable_status:" + this.enable_status);
                var2.println("enable_status_blink:" + this.enable_status_blink);
                var2.println("render_player_face:" + this.enable_status_blink);
                var2.close();
            } catch (Exception var21) {
                logger.error("Failed to save options", var21);
                var21.printStackTrace();
            }
        }

    }

    public String getKeyBinding(EnumOptionsRPG par1EnumOptions) {
        String s = I18n.format(par1EnumOptions.getEnumString()) + ": ";
        if (par1EnumOptions.getEnumBoolean()) {
            boolean flag = this.getOptionOrdinalValue(par1EnumOptions);
            return flag ? s + I18n.format("options.on") : s + I18n.format("options.off");
        } else {
            return par1EnumOptions == EnumOptionsRPG.COLOR_AIR ? s + getTranslation(COLOR, this.color_air) : (par1EnumOptions == EnumOptionsRPG.COLOR_HEALTH ? s + getTranslation(COLOR, this.color_health) : (par1EnumOptions == EnumOptionsRPG.COLOR_STAMINA ? s + getTranslation(COLOR, this.color_stamina) : (par1EnumOptions == EnumOptionsRPG.COLOR_EXPERIENCE ? s + getTranslation(COLOR, this.color_experience) : (par1EnumOptions == EnumOptionsRPG.COLOR_JUMPBAR ? s + getTranslation(COLOR, this.color_jumpbar) : (par1EnumOptions == EnumOptionsRPG.HUD_TYPE ? s + getTranslation(HUD_TYPE, this.hudtype) : (par1EnumOptions == EnumOptionsRPG.SIZE_ARMOR ? s + getTranslation(SIZE, this.size_armor) : (par1EnumOptions == EnumOptionsRPG.SIZE_STATUS ? s + getTranslation(SIZE, this.size_status) : (par1EnumOptions == EnumOptionsRPG.SIZE_WIDGET ? s + getTranslation(SIZE, this.size_widget) : s))))))));
        }
    }
}

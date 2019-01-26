package net.spellcraftgaming.rpghud;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.*;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.*;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.*;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class GuiInGameForgeRPG extends GuiIngameForge {

    private static final int WHITE = 16777215;
    private ScaledResolution res = null;
    private FontRenderer fontrenderer = null;
    private RenderGameOverlayEvent eventParent;
    private static final String MC_VERSION = "1.7.10";
    private static final ResourceLocation WIDGITS = new ResourceLocation("textures/gui/widgets.png");
    private static final ResourceLocation INTERFACE = new ResourceLocation("spellcraftgaming:rpghud/interface.png");
    private static final ResourceLocation DAMAGE = new ResourceLocation("spellcraftgaming:rpghud/damage.png");
    private static final ResourceLocation ACHIEVEMENTS = new ResourceLocation("textures/gui/achievement/achievement_background.png");
    private static final ResourceLocation INVENTORY = new ResourceLocation("textures/gui/container/inventory.png");

    private static final Field chatGUI;

    static {
        Field field = null;
        try {
            field = GuiIngame.class.getDeclaredField("field_73840_e");
            field.setAccessible(true);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        chatGUI = field;
    }

    public GuiInGameForgeRPG(Minecraft mc) {
        super(mc);
        try {
            chatGUI.set(this, new HudGuiNewChat(mc));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void renderGameOverlay(float partialTicks, boolean hasScreen, int mouseX, int mouseY) {
        this.res = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
        this.eventParent = new RenderGameOverlayEvent(partialTicks, this.res, mouseX, mouseY);
        int width = this.res.getScaledWidth();
        int height = this.res.getScaledHeight();
        renderHealthMount = this.mc.thePlayer.ridingEntity instanceof EntityLivingBase;
        renderFood = this.mc.thePlayer.ridingEntity == null;
        renderJumpBar = this.mc.thePlayer.isRidingHorse();
        right_height = 39;
        left_height = 39;
        if (!this.pre(ElementType.ALL)) {
            this.fontrenderer = this.mc.fontRenderer;
            this.mc.entityRenderer.setupOverlayRendering();
            GL11.glEnable(3042);
            if (Minecraft.isFancyGraphicsEnabled()) {
                this.renderVignette(this.mc.thePlayer.getBrightness(partialTicks), width, height);
            } else {
                OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            }

            if (renderHelmet) {
                this.renderHelmet(this.res, partialTicks, hasScreen, mouseX, mouseY);
            }

            if (renderPortal && !this.mc.thePlayer.isPotionActive(Potion.confusion)) {
                this.renderPortal(width, height, partialTicks);
            }

            if (!this.mc.playerController.enableEverythingIsScrewedUpMode()) {
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                this.zLevel = -90.0F;
                this.rand.setSeed((long) (this.updateCounter * 312871));
                if (renderCrosshairs) {
                    this.renderCrosshairs(width, height);
                }

                if (renderBossHealth) {
                    this.renderBossHealth();
                }

                if (this.mc.playerController.shouldDrawHUD()) {
                    if (renderHealth) {
                        this.renderHealth(width, height);
                    }

                    if (renderArmor) {
                        this.renderArmor(width, height);
                    }

                    if (renderFood) {
                        this.renderFood(width, height);
                    }

                    if (renderHealthMount) {
                        this.renderHealthMount(width, height);
                    }

                    if (renderAir) {
                        this.renderAir(width, height);
                    }

                    if (RPGHud.rpg_settings.hud_enabled) {
                        this.drawRpgHud(partialTicks);
                    }
                }

                if (renderHotbar) {
                    this.renderHotbar(width, height, partialTicks);
                }
            }

            if (renderJumpBar) {
                this.renderJumpBar(width, height);
            } else if (renderExperiance) {
                this.renderExperience(width, height);
            }

            this.renderSleepFade(width, height);
            this.renderToolHightlight(width, height);
            this.renderHUDText(width, height);
            this.renderRecordOverlay(width, height, partialTicks);
            ScoreObjective objective = this.mc.theWorld.getScoreboard().func_96539_a(1);
            if (renderObjective && objective != null) {
                this.func_96136_a(objective, height, width, this.fontrenderer);
            }

            GL11.glEnable(3042);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glDisable(3008);
            this.renderChat(width, height);
            this.renderPlayerList(width, height);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(2896);
            GL11.glEnable(3008);
            this.post(ElementType.ALL);
        }
    }

    protected void renderCrosshairs(int width, int height) {
        if (!this.pre(ElementType.CROSSHAIRS)) {
            this.bind(Gui.icons);
            GL11.glEnable(3042);
            OpenGlHelper.glBlendFunc(775, 769, 1, 0);
            this.drawTexturedModalRect(width / 2 - 7, height / 2 - 7, 0, 0, 16, 16);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glDisable(3042);
            this.post(ElementType.CROSSHAIRS);
        }
    }

    protected void renderSleepFade(int width, int height) {
        if (this.mc.thePlayer.getSleepTimer() > 0) {
            this.mc.mcProfiler.startSection("sleep");
            GL11.glDisable(2929);
            GL11.glDisable(3008);
            int sleepTime = this.mc.thePlayer.getSleepTimer();
            float opacity = (float) sleepTime / 100.0F;
            if (opacity > 1.0F) {
                opacity = 1.0F - (float) (sleepTime - 100) / 10.0F;
            }

            int color = (int) (220.0F * opacity) << 24 | 1052704;
            drawRect(0, 0, width, height, color);
            GL11.glEnable(3008);
            GL11.glEnable(2929);
            this.mc.mcProfiler.endSection();
        }

    }

    protected void renderToolHightlight(int width, int height) {
        if (this.mc.gameSettings.heldItemTooltips) {
            this.mc.mcProfiler.startSection("toolHighlight");
            if (this.remainingHighlightTicks > 0 && this.highlightingItemStack != null) {
                String name = this.highlightingItemStack.getDisplayName();
                int opacity = (int) ((float) this.remainingHighlightTicks * 256.0F / 10.0F);
                if (opacity > 255) {
                    opacity = 255;
                }

                if (opacity > 0) {
                    int y = height - 59;
                    if (!this.mc.playerController.shouldDrawHUD()) {
                        y += 14;
                    }

                    GL11.glPushMatrix();
                    GL11.glEnable(3042);
                    OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                    FontRenderer font = this.highlightingItemStack.getItem().getFontRenderer(this.highlightingItemStack);
                    int x;
                    if (font != null) {
                        x = (width - font.getStringWidth(name)) / 2;
                        font.drawStringWithShadow(name, x, y, 16777215 | opacity << 24);
                    } else {
                        x = (width - this.fontrenderer.getStringWidth(name)) / 2;
                        this.fontrenderer.drawStringWithShadow(name, x, y, 16777215 | opacity << 24);
                    }

                    GL11.glDisable(3042);
                    GL11.glPopMatrix();
                }
            }

            this.mc.mcProfiler.endSection();
        }

    }

    protected void renderHUDText(int width, int height) {
        this.mc.mcProfiler.startSection("forgeHudText");
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        ArrayList left = new ArrayList();
        ArrayList right = new ArrayList();
        long event;
        if (this.mc.isDemo()) {
            event = this.mc.theWorld.getTotalWorldTime();
            if (event >= 120500L) {
                right.add(I18n.format("demo.demoExpired"));
            } else {
                right.add(I18n.format("demo.remainingTime", StringUtils.ticksToElapsedTime((int) (120500L - event))));
            }
        }

        if (this.mc.gameSettings.showDebugInfo && !this.pre(ElementType.DEBUG)) {
            this.mc.mcProfiler.startSection("debug");
            GL11.glPushMatrix();
            left.add("Minecraft 1.7.10 (" + this.mc.debug + ")");
            left.add(this.mc.debugInfoRenders());
            left.add(this.mc.getEntityDebug());
            left.add(this.mc.debugInfoEntities());
            left.add(this.mc.getWorldProviderName());
            left.add(null);
            event = Runtime.getRuntime().maxMemory();
            long msg = Runtime.getRuntime().totalMemory();
            long free = Runtime.getRuntime().freeMemory();
            long used = msg - free;
            right.add("Used memory: " + used * 100L / event + "% (" + used / 1024L / 1024L + "MB) of " + event / 1024L / 1024L + "MB");
            right.add("Allocated memory: " + msg * 100L / event + "% (" + msg / 1024L / 1024L + "MB)");
            int x1 = MathHelper.floor_double(this.mc.thePlayer.posX);
            int y = MathHelper.floor_double(this.mc.thePlayer.posY);
            int z = MathHelper.floor_double(this.mc.thePlayer.posZ);
            float yaw = this.mc.thePlayer.rotationYaw;
            int heading = MathHelper.floor_double((double) (this.mc.thePlayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
            left.add(String.format("x: %.5f (%d) // c: %d (%d)", this.mc.thePlayer.posX, x1, x1 >> 4, x1 & 15));
            left.add(String.format("y: %.3f (feet pos, %.3f eyes pos)", this.mc.thePlayer.boundingBox.minY, this.mc.thePlayer.posY));
            left.add(String.format("z: %.5f (%d) // c: %d (%d)", this.mc.thePlayer.posZ, z, z >> 4, z & 15));
            left.add(String.format("f: %d (%s) / %f", heading, Direction.directions[heading], MathHelper.wrapAngleTo180_float(yaw)));
            if (this.mc.theWorld != null && this.mc.theWorld.blockExists(x1, y, z)) {
                Chunk chunk = this.mc.theWorld.getChunkFromBlockCoords(x1, z);
                left.add(String.format("lc: %d b: %s bl: %d sl: %d rl: %d", chunk.getTopFilledSegment() + 15, chunk.getBiomeGenForWorldCoords(x1 & 15, z & 15, this.mc.theWorld.getWorldChunkManager()).biomeName, chunk.getSavedLightValue(EnumSkyBlock.Block, x1 & 15, y, z & 15), chunk.getSavedLightValue(EnumSkyBlock.Sky, x1 & 15, y, z & 15), chunk.getBlockLightValue(x1 & 15, y, z & 15, 0)));
            } else {
                left.add(null);
            }

            left.add(String.format("ws: %.3f, fs: %.3f, g: %b, fl: %d", this.mc.thePlayer.capabilities.getWalkSpeed(), this.mc.thePlayer.capabilities.getFlySpeed(), this.mc.thePlayer.onGround, this.mc.theWorld.getHeightValue(x1, z)));
            if (this.mc.entityRenderer != null && this.mc.entityRenderer.isShaderActive()) {
                left.add(String.format("shader: %s", this.mc.entityRenderer.getShaderGroup().getShaderGroupName()));
            }

            right.add(null);

            for (String brand : FMLCommonHandler.instance().getBrandings(false)) {
                right.add(brand);
            }

            GL11.glPopMatrix();
            this.mc.mcProfiler.endSection();
            this.post(ElementType.DEBUG);
        }

        Text var20 = new Text(this.eventParent, left, right);
        if (!MinecraftForge.EVENT_BUS.post(var20)) {
            int x;
            String var21;
            for (x = 0; x < left.size(); ++x) {
                var21 = (String) left.get(x);
                if (var21 != null) {
                    this.fontrenderer.drawStringWithShadow(var21, 2, 2 + x * 10, 16777215);
                }
            }

            for (x = 0; x < right.size(); ++x) {
                var21 = (String) right.get(x);
                if (var21 != null) {
                    int w = this.fontrenderer.getStringWidth(var21);
                    this.fontrenderer.drawStringWithShadow(var21, width - w - 10, 2 + x * 10, 16777215);
                }
            }
        }

        this.mc.mcProfiler.endSection();
        this.post(ElementType.TEXT);
    }

    protected void renderRecordOverlay(int width, int height, float partialTicks) {
        if (this.recordPlayingUpFor > 0) {
            this.mc.mcProfiler.startSection("overlayMessage");
            float hue = (float) this.recordPlayingUpFor - partialTicks;
            int opacity = (int) (hue * 256.0F / 20.0F);
            if (opacity > 255) {
                opacity = 255;
            }

            if (opacity > 0) {
                GL11.glPushMatrix();
                GL11.glTranslatef((float) (width / 2), (float) (height - 48), 0.0F);
                GL11.glEnable(3042);
                OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                int color = this.recordIsPlaying ? Color.HSBtoRGB(hue / 50.0F, 0.7F, 0.6F) & 16777215 : 16777215;
                this.fontrenderer.drawString(this.recordPlaying, -this.fontrenderer.getStringWidth(this.recordPlaying) / 2, -4, color | opacity << 24);
                GL11.glDisable(3042);
                GL11.glPopMatrix();
            }

            this.mc.mcProfiler.endSection();
        }

    }

    protected void renderChat(int width2, int height) {
        this.mc.mcProfiler.startSection("chat");
        boolean creative = mc.thePlayer.capabilities.isCreativeMode;
        Chat event = new Chat(this.eventParent, 0, height - (creative || RPGHud.rpg_settings.hudtype != 3 ? 48 : 86));
        if (!MinecraftForge.EVENT_BUS.post(event)) {
            GL11.glPushMatrix();
            GL11.glTranslatef((float) event.posX, (float) event.posY, 0.0F);
            persistantChatGUI.drawChat(this.updateCounter);
            GL11.glPopMatrix();
            this.post(ElementType.CHAT);
            this.mc.mcProfiler.endSection();
        }
    }

    protected void renderPlayerList(int width, int height) {
        ScoreObjective scoreobjective = this.mc.theWorld.getScoreboard().func_96539_a(0);
        NetHandlerPlayClient handler = this.mc.thePlayer.sendQueue;
        if (this.mc.gameSettings.keyBindPlayerList.getIsKeyPressed() && (!this.mc.isIntegratedServerRunning() || handler.playerInfoList.size() > 1 || scoreobjective != null)) {
            if (this.pre(ElementType.PLAYER_LIST)) {
                return;
            }

            this.mc.mcProfiler.startSection("playerList");
            List players = handler.playerInfoList;
            int maxPlayers = handler.currentServerMaxPlayers;
            int rows = maxPlayers;
            boolean columns = true;

            int var22;
            for (var22 = 1; rows > 20; rows = (maxPlayers + var22 - 1) / var22) {
                ++var22;
            }

            int columnWidth = 300 / var22;
            if (columnWidth > 150) {
                columnWidth = 150;
            }

            int left = (width - var22 * columnWidth) / 2;
            byte border = 10;
            drawRect(left - 1, border - 1, left + columnWidth * var22, border + 9 * rows, Integer.MIN_VALUE);

            for (int i = 0; i < maxPlayers; ++i) {
                int xPos = left + i % var22 * columnWidth;
                int yPos = border + i / var22 * 9;
                drawRect(xPos, yPos, xPos + columnWidth - 1, yPos + 8, 553648127);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glEnable(3008);
                if (i < players.size()) {
                    GuiPlayerInfo player = (GuiPlayerInfo) players.get(i);
                    ScorePlayerTeam team = this.mc.theWorld.getScoreboard().getPlayersTeam(player.name);
                    String displayName = ScorePlayerTeam.formatPlayerName(team, player.name);
                    this.fontrenderer.drawStringWithShadow(displayName, xPos, yPos, 16777215);
                    int ping;
                    if (scoreobjective != null) {
                        int pingIndex = xPos + this.fontrenderer.getStringWidth(displayName) + 5;
                        ping = xPos + columnWidth - 12 - 5;
                        if (ping - pingIndex > 5) {
                            Score score = scoreobjective.getScoreboard().func_96529_a(player.name, scoreobjective);
                            String scoreDisplay = EnumChatFormatting.YELLOW + "" + score.getScorePoints();
                            this.fontrenderer.drawStringWithShadow(scoreDisplay, ping - this.fontrenderer.getStringWidth(scoreDisplay), yPos, 16777215);
                        }
                    }

                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    this.mc.getTextureManager().bindTexture(Gui.icons);
                    byte var23 = 4;
                    ping = player.responseTime;
                    if (ping < 0) {
                        var23 = 5;
                    } else if (ping < 150) {
                        var23 = 0;
                    } else if (ping < 300) {
                        var23 = 1;
                    } else if (ping < 600) {
                        var23 = 2;
                    } else if (ping < 1000) {
                        var23 = 3;
                    }

                    this.zLevel += 100.0F;
                    this.drawTexturedModalRect(xPos + columnWidth - 12, yPos, 0, 176 + var23 * 8, 10, 8);
                    this.zLevel -= 100.0F;
                }
            }

            this.post(ElementType.PLAYER_LIST);
        }

    }

    protected void renderBossHealth() {
        if (!this.pre(ElementType.BOSSHEALTH)) {
            this.mc.mcProfiler.startSection("bossHealth");
            GL11.glEnable(3042);
            this.renderBossHealth2();
            GL11.glDisable(3042);
            this.mc.mcProfiler.endSection();
            this.post(ElementType.BOSSHEALTH);
        }
    }

    protected void renderBossHealth2() {
        if (BossStatus.bossName != null && BossStatus.statusBarTime > 0) {
            --BossStatus.statusBarTime;
            FontRenderer fontrenderer = this.mc.fontRenderer;
            ScaledResolution scaledresolution = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
            int i = scaledresolution.getScaledWidth();
            short short1 = 182;
            int j = i / 2 - short1 / 2;
            int k = (int) (BossStatus.healthScale * (float) (short1 + 1));
            byte b0 = 12;
            this.drawTexturedModalRect(j, b0, 0, 74, short1, 5);
            this.drawTexturedModalRect(j, b0, 0, 74, short1, 5);
            if (k > 0) {
                this.drawTexturedModalRect(j, b0, 0, 79, k, 5);
            }

            String s = BossStatus.bossName;
            fontrenderer.drawStringWithShadow(s, i / 2 - fontrenderer.getStringWidth(s) / 2, b0 - 10, 16777215);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(icons);
        }

    }

    protected void renderPortal(int width, int height, float partialTicks) {
        if (!this.pre(ElementType.PORTAL)) {
            float f1 = this.mc.thePlayer.prevTimeInPortal + (this.mc.thePlayer.timeInPortal - this.mc.thePlayer.prevTimeInPortal) * partialTicks;
            if (f1 > 0.0F) {
                this.func_130015_b(f1, width, height);
            }

            this.post(ElementType.PORTAL);
        }
    }

    private boolean pre(ElementType type) {
        return MinecraftForge.EVENT_BUS.post(new Pre(this.eventParent, type));
    }

    private void post(ElementType type) {
        MinecraftForge.EVENT_BUS.post(new Post(this.eventParent, type));
    }

    private void bind(ResourceLocation res) {
        this.mc.getTextureManager().bindTexture(res);
    }

    protected void renderHotbar(int width, int height, float partialTicks) {
        if (!this.pre(ElementType.HOTBAR)) {
            this.mc.mcProfiler.startSection("actionBar");
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.renderEngine.bindTexture(WIDGITS);
            InventoryPlayer inv = this.mc.thePlayer.inventory;
            if (this.mc.playerController.shouldDrawHUD()) {
                if (RPGHud.rpg_settings.hud_enabled && RPGHud.rpg_settings.hudtype != 3) {
                    this.drawTexturedModalRect(width / 2 - 91, height - 22 - 9, 0, 0, 182, 22);
                    this.drawTexturedModalRect(width / 2 - 91 - 1 + inv.currentItem * 20, height - 22 - 1 - 9, 0, 22, 24, 22);
                } else {
                    this.drawTexturedModalRect(49, height + 7 - 22 - 23 - 9, 0, 0, 182, 22);
                    this.drawTexturedModalRect(48 + inv.currentItem * 20, height - 22 + 7 - 23 - 1 - 9, 0, 22, 24, 22);
                }
            } else {
                this.drawTexturedModalRect(width / 2 - 91, height - 22 - (this.mc.playerController.shouldDrawHUD() ? 9 : 0), 0, 0, 182, 22);
                this.drawTexturedModalRect(width / 2 - 91 - 1 + inv.currentItem * 20, height - 22 - 1 - (this.mc.playerController.shouldDrawHUD() ? 9 : 0), 0, 22, 24, 22);
            }

            GL11.glDisable(3042);
            GL11.glEnable('\u803a');
            RenderHelper.enableGUIStandardItemLighting();

            for (int i = 0; i < 9; ++i) {
                int x = width / 2 - 90 + i * 20 + 2;
                if (RPGHud.rpg_settings.hud_enabled && RPGHud.rpg_settings.hudtype == 3 && this.mc.playerController.shouldDrawHUD()) {
                    x = 49 + i * 20 + 3;
                }

                int z = height - 16 - 3 - (this.mc.playerController.shouldDrawHUD() ? 9 : 0);
                if (RPGHud.rpg_settings.hud_enabled && RPGHud.rpg_settings.hudtype == 3 && this.mc.playerController.shouldDrawHUD()) {
                    z = height + 7 - 22 - 1 - 28;
                }

                this.renderInventorySlot(i, x, z, partialTicks);
            }

            RenderHelper.disableStandardItemLighting();
            GL11.glDisable('\u803a');
            this.mc.mcProfiler.endSection();
            this.post(ElementType.HOTBAR);
        }
    }

    protected void renderArmor(int width, int height) {
        if (!this.pre(ElementType.ARMOR)) {
            this.mc.mcProfiler.startSection("armor");
            GL11.glEnable(3042);
            int left = RPGHud.rpg_settings.hudtype == 3 ? 46 : width / 2 - 91;
            int top = RPGHud.rpg_settings.hudtype == 3 ? height - 64 : height - left_height;
            int level = ForgeHooks.getTotalArmorValue(this.mc.thePlayer);

            for (int i = 1; level > 0 && i < 20; i += 2) {
                if (i < level) {
                    this.drawTexturedModalRect(left + (this.mc.playerController.shouldDrawHUD() ? 48 : 0), top - (this.mc.playerController.shouldDrawHUD() ? 2 : 0), 34, 9, 9, 9);
                } else if (i == level) {
                    this.drawTexturedModalRect(left + (this.mc.playerController.shouldDrawHUD() ? 48 : 0), top - (this.mc.playerController.shouldDrawHUD() ? 2 : 0), 25, 9, 9, 9);
                } else if (i > level) {
                    this.drawTexturedModalRect(left + (this.mc.playerController.shouldDrawHUD() ? 48 : 0), top - (this.mc.playerController.shouldDrawHUD() ? 2 : 0), 16, 9, 9, 9);
                }

                left += 8;
            }

            left_height += 10;
            GL11.glDisable(3042);
            this.mc.mcProfiler.endSection();
            this.post(ElementType.ARMOR);
        }
    }

    private void renderHelmet(ScaledResolution res, float partialTicks, boolean hasScreen, int mouseX, int mouseY) {
        if (!this.pre(ElementType.HELMET)) {
            ItemStack itemstack = this.mc.thePlayer.inventory.armorItemInSlot(3);
            if (this.mc.gameSettings.thirdPersonView == 0 && itemstack != null && itemstack.getItem() != null) {
                if (itemstack.getItem() == Item.getItemFromBlock(Blocks.pumpkin)) {
                    this.renderPumpkinBlur(res.getScaledWidth(), res.getScaledHeight());
                } else {
                    itemstack.getItem().renderHelmetOverlay(itemstack, this.mc.thePlayer, res, partialTicks, hasScreen, mouseX, mouseY);
                }
            }

            this.post(ElementType.HELMET);
        }
    }

    protected void renderAir(int width, int height) {
        if (!this.pre(ElementType.AIR)) {
            this.mc.mcProfiler.startSection("air");
            GL11.glEnable(3042);
            int left = width / 2 + 91;
            int top = height - right_height;
            if (this.mc.thePlayer.isInsideOfMaterial(Material.water)) {
                int air = this.mc.thePlayer.getAir();
                int full = MathHelper.ceiling_double_int((double) (air - 2) * 10.0D / 300.0D);
                int partial = MathHelper.ceiling_double_int((double) air * 10.0D / 300.0D) - full;

                for (int i = 0; i < full + partial; ++i) {
                    if (!RPGHud.rpg_settings.hud_enabled) {
                        this.drawTexturedModalRect(left - i * 8 - 9, top, i < full ? 16 : 25, 18, 9, 9);
                    }
                }

                right_height += 10;
            }

            GL11.glDisable(3042);
            this.mc.mcProfiler.endSection();
            this.post(ElementType.AIR);
        }
    }

    public void renderHealth(int width, int height) {
        this.bind(icons);
        if (!this.pre(ElementType.HEALTH)) {
            this.mc.mcProfiler.startSection("health");
            GL11.glEnable(3042);
            boolean highlight = this.mc.thePlayer.hurtResistantTime / 3 % 2 == 1;
            if (this.mc.thePlayer.hurtResistantTime < 10) {
                highlight = false;
            }

            IAttributeInstance attrMaxHealth = this.mc.thePlayer.getEntityAttribute(SharedMonsterAttributes.maxHealth);
            int health = MathHelper.ceiling_float_int(this.mc.thePlayer.getHealth());
            int healthLast = MathHelper.ceiling_float_int(this.mc.thePlayer.prevHealth);
            float healthMax = (float) attrMaxHealth.getAttributeValue();
            float absorb = this.mc.thePlayer.getAbsorptionAmount();
            int healthRows = MathHelper.ceiling_float_int((healthMax + absorb) / 2.0F / 10.0F);
            int rowHeight = Math.max(10 - (healthRows - 2), 3);
            this.rand.setSeed((long) (this.updateCounter * 312871));
            int left = width / 2 - 91;
            int top = height - left_height;
            left_height += healthRows * rowHeight;
            if (rowHeight != 10) {
                left_height += 10 - rowHeight;
            }

            int regen = -1;
            if (this.mc.thePlayer.isPotionActive(Potion.regeneration)) {
                regen = this.updateCounter % 25;
            }

            int TOP = 9 * (this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled() ? 5 : 0);
            int BACKGROUND = highlight ? 25 : 16;
            int MARGIN = 16;
            if (this.mc.thePlayer.isPotionActive(Potion.poison)) {
                MARGIN += 36;
            } else if (this.mc.thePlayer.isPotionActive(Potion.wither)) {
                MARGIN += 72;
            }

            float absorbRemaining = absorb;

            for (int i = MathHelper.ceiling_float_int((healthMax + absorb) / 2.0F) - 1; i >= 0; --i) {
                int row = MathHelper.ceiling_float_int((float) (i + 1) / 10.0F) - 1;
                int x = left + i % 10 * 8;
                int y = top - row * rowHeight;
                if (health <= 4) {
                    y += this.rand.nextInt(2);
                }

                if (i == regen) {
                    y -= 2;
                }

                if (!RPGHud.rpg_settings.hud_enabled) {
                    this.drawTexturedModalRect(x, y, BACKGROUND, TOP, 9, 9);
                }

                if (highlight && i * 2 + 1 < healthLast) {
                    if (!RPGHud.rpg_settings.hud_enabled) {
                        this.drawTexturedModalRect(x, y, MARGIN + 54, TOP, 9, 9);
                    } else if (i * 2 + 1 == healthLast && !RPGHud.rpg_settings.hud_enabled) {
                        this.drawTexturedModalRect(x, y, MARGIN + 63, TOP, 9, 9);
                    }
                }

                if (absorbRemaining > 0.0F) {
                    if (absorbRemaining == absorb && absorb % 2.0F == 1.0F) {
                        if (!RPGHud.rpg_settings.hud_enabled) {
                            this.drawTexturedModalRect(x, y, MARGIN + 153, TOP, 9, 9);
                        } else if (!RPGHud.rpg_settings.hud_enabled) {
                            this.drawTexturedModalRect(x, y, MARGIN + 144, TOP, 9, 9);
                        }
                    }

                    absorbRemaining -= 2.0F;
                } else if (i * 2 + 1 < health) {
                    if (!RPGHud.rpg_settings.hud_enabled) {
                        this.drawTexturedModalRect(x, y, MARGIN + 36, TOP, 9, 9);
                    } else if (i * 2 + 1 == health && !RPGHud.rpg_settings.hud_enabled) {
                        this.drawTexturedModalRect(x, y, MARGIN + 45, TOP, 9, 9);
                    }
                }
            }

            GL11.glDisable(3042);
            this.mc.mcProfiler.endSection();
            this.post(ElementType.HEALTH);
        }
    }

    public void renderFood(int width, int height) {
        if (!this.pre(ElementType.FOOD)) {
            this.mc.mcProfiler.startSection("food");
            GL11.glEnable(3042);
            int left = width / 2 + 91;
            int top = height - right_height;
            right_height += 10;
            boolean unused = false;
            FoodStats stats = this.mc.thePlayer.getFoodStats();
            int level = stats.getFoodLevel();
            int levelLast = stats.getPrevFoodLevel();

            for (int i = 0; i < 10; ++i) {
                int idx = i * 2 + 1;
                int x = left - i * 8 - 9;
                int y = top;
                int icon = 16;
                byte backgound = 0;
                if (this.mc.thePlayer.isPotionActive(Potion.hunger)) {
                    icon += 36;
                    backgound = 13;
                }

                if (unused) {
                    backgound = 1;
                }

                if (this.mc.thePlayer.getFoodStats().getSaturationLevel() <= 0.0F && this.updateCounter % (level * 3 + 1) == 0) {
                    y = top + (this.rand.nextInt(3) - 1);
                }

                if (!RPGHud.rpg_settings.hud_enabled) {
                    this.drawTexturedModalRect(x, y, 16 + backgound * 9, 27, 9, 9);
                }

                if (unused && idx < levelLast) {
                    if (!RPGHud.rpg_settings.hud_enabled) {
                        this.drawTexturedModalRect(x, y, icon + 54, 27, 9, 9);
                    } else if (idx == levelLast && !RPGHud.rpg_settings.hud_enabled) {
                        this.drawTexturedModalRect(x, y, icon + 63, 27, 9, 9);
                    }
                }

                if (idx < level) {
                    if (!RPGHud.rpg_settings.hud_enabled) {
                        this.drawTexturedModalRect(x, y, icon + 36, 27, 9, 9);
                    } else if (idx == level && !RPGHud.rpg_settings.hud_enabled) {
                        this.drawTexturedModalRect(x, y, icon + 45, 27, 9, 9);
                    }
                }
            }

            GL11.glDisable(3042);
            this.mc.mcProfiler.endSection();
            this.post(ElementType.FOOD);
        }
    }

    protected void renderExperience(int width, int height) {
        this.bind(icons);
        if (!this.pre(ElementType.EXPERIENCE)) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(3042);
            if (this.mc.playerController.gameIsSurvivalOrAdventure()) {
                this.mc.mcProfiler.startSection("expBar");
                int cap = this.mc.thePlayer.xpBarCap();
                int left = width / 2 - 91;
                int color;
                if (cap > 0) {
                    short flag1 = 182;
                    color = (int) (this.mc.thePlayer.experience * (float) (flag1 + 1));
                    int text = height - 32 + 3;
                    if (!RPGHud.rpg_settings.hud_enabled) {
                        this.drawTexturedModalRect(left, text, 0, 64, flag1, 5);
                    }

                    if (color > 0 && !RPGHud.rpg_settings.hud_enabled) {
                        this.drawTexturedModalRect(left, text, 0, 69, color, 5);
                    }
                }

                this.mc.mcProfiler.endSection();
                if (this.mc.playerController.gameIsSurvivalOrAdventure() && this.mc.thePlayer.experienceLevel > 0 && !RPGHud.rpg_settings.hud_enabled) {
                    this.mc.mcProfiler.startSection("expLevel");
                    boolean flag11 = false;
                    color = flag11 ? 16777215 : 8453920;
                    String text1 = "" + this.mc.thePlayer.experienceLevel;
                    int x = (width - this.fontrenderer.getStringWidth(text1)) / 2;
                    int y = height - 31 - 4;
                    this.fontrenderer.drawString(text1, x + 1, y, 0);
                    this.fontrenderer.drawString(text1, x - 1, y, 0);
                    this.fontrenderer.drawString(text1, x, y + 1, 0);
                    this.fontrenderer.drawString(text1, x, y - 1, 0);
                    this.fontrenderer.drawString(text1, x, y, color);
                    this.mc.mcProfiler.endSection();
                }
            }

            GL11.glEnable(3042);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.post(ElementType.EXPERIENCE);
        }
    }

    protected void renderJumpBar(int width, int height) {
        this.bind(icons);
        if (!this.pre(ElementType.JUMPBAR)) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(3042);
            this.mc.mcProfiler.startSection("jumpBar");
            float charge = this.mc.thePlayer.getHorseJumpPower();
            boolean barWidth = true;
            int x = width / 2 - 91;
            int filled = (int) (charge * 183.0F);
            int top = height - 32 + 3;
            if (!RPGHud.rpg_settings.hud_enabled) {
                this.drawTexturedModalRect(x, top, 0, 84, 182, 5);
            }

            if (filled > 0 && !RPGHud.rpg_settings.hud_enabled) {
                this.drawTexturedModalRect(x, top, 0, 89, filled, 5);
            }

            GL11.glEnable(3042);
            this.mc.mcProfiler.endSection();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.post(ElementType.JUMPBAR);
        }
    }

    protected void renderHealthMount(int width, int height) {
        Entity tmp = this.mc.thePlayer.ridingEntity;
        if (tmp instanceof EntityLivingBase) {
            this.bind(icons);
            if (!this.pre(ElementType.HEALTHMOUNT)) {
                boolean unused = false;
                int left_align = width / 2 + 91;
                this.mc.mcProfiler.endStartSection("mountHealth");
                GL11.glEnable(3042);
                EntityLivingBase mount = (EntityLivingBase) tmp;
                int health = (int) Math.ceil((double) mount.getHealth());
                float healthMax = mount.getMaxHealth();
                int hearts = (int) (healthMax + 0.5F) / 2;
                if (hearts > 30) {
                    hearts = 30;
                }

                boolean MARGIN = true;
                int BACKGROUND = 52 + (unused ? 1 : 0);
                boolean HALF = true;
                boolean FULL = true;

                for (int heart = 0; hearts > 0; heart += 20) {
                    int top = height - right_height;
                    int rowCount = Math.min(hearts, 10);
                    hearts -= rowCount;

                    for (int i = 0; i < rowCount; ++i) {
                        int x = left_align - i * 8 - 9;
                        if (!RPGHud.rpg_settings.hud_enabled) {
                            this.drawTexturedModalRect(x, top, BACKGROUND, 9, 9, 9);
                        }

                        if (i * 2 + 1 + heart < health) {
                            if (!RPGHud.rpg_settings.hud_enabled) {
                                this.drawTexturedModalRect(x, top, 88, 9, 9, 9);
                            } else if (i * 2 + 1 + heart == health && !RPGHud.rpg_settings.hud_enabled) {
                                this.drawTexturedModalRect(x, top, 97, 9, 9, 9);
                            }
                        }
                    }

                    right_height += 10;
                }

                GL11.glDisable(3042);
                this.post(ElementType.HEALTHMOUNT);
            }
        }
    }

    private static void drawOutline(Gui par1, int par2, int par3, int par4, int par5, int par6) {
        Gui.drawRect(par2, par3, par2 + par4, par3 + 1, par6);
        Gui.drawRect(par2, par3, par2 + 1, par3 + par5, par6);
        Gui.drawRect(par2 + par4 - 1, par3, par2 + par4, par3 + par5, par6);
        Gui.drawRect(par2, par3 + par5 - 1, par2 + par4, par3 + par5, par6);
    }

    private static void drawBar(boolean outline, Gui par1, int par2, int par3, int par4, int par5, double par6, int par8, int par9, int par10, int par11) {
        drawCustomBar(outline, par1, par2, par3, par4, par5, par6, 0, par8, par9, par10, par11);
    }

    private static void drawCustomBar(boolean outline, Gui par1, int par2, int par3, int par4, int par5, double par6, int par7, int par8, int par9, int par10, int par11) {
        if (par6 < 0.0D) {
            par6 = 0.0D;
        }

        int var1 = (int) Math.round(par6 / 100.0D * (double) (par4 - 2));
        if (outline) {
            drawOutline(par1, par2, par3, par4, par5, par7);
        }

        int var2 = par4 - 2;
        if (var2 < 0) {
            var2 = 0;
        }

        int var3 = par5 - 2;
        if (var3 < 0) {
            var3 = 0;
        }

        int var4 = (int) Math.round((double) var3 / 6.0D * 2.75D);
        Gui.drawRect(par2 + 1, par3 + 1, par2 + 1 + var1, par3 + var4 + 1, par11);
        Gui.drawRect(par2 + 1, par3 + 1 + var4, par2 + 1 + var1, par3 + var3 + 1, par10);
        if (var2 - var1 > 0) {
            Gui.drawRect(par2 + 1 + var1, par3 + 1, par2 + 1 + var2, par3 + var4 + 1, par9);
            Gui.drawRect(par2 + 1 + var1, par3 + 1 + var4, par2 + 1 + var2, par3 + var3 + 1, par8);
        }

    }

    public void drawRpgHud(float partialTicks) {
        FontRenderer fontrenderer = this.mc.fontRenderer;
        ScaledResolution var1 = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
        IAttributeInstance attrMaxHealth = this.mc.thePlayer.getEntityAttribute(SharedMonsterAttributes.maxHealth);
        int var2 = (int) ((float) this.mc.thePlayer.xpBarCap() * this.mc.thePlayer.experience);
        int width = var1.getScaledWidth();
        int height = var1.getScaledHeight();
        int var3 = this.mc.thePlayer.experienceLevel;
        int var4 = (int) attrMaxHealth.getAttributeValue();
        int var5 = width - 29;
        int var6 = var1.getScaledWidth();
        int var7 = var6 / 2 - 91;
        boolean var8 = true;
        int[] red = new int[]{-4128768, -7667712};
        int[] blue = new int[]{-16753726, -16760180};
        int[] yellow = new int[]{-1118720, -2434560};
        int[] green = new int[]{-12860928, -13923328};
        int[] white = new int[]{-855310, -1644826};
        int[] grey = new int[]{-4210753, -8355712};
        int health = 0;
        float healthMax = 0.0F;
        boolean isPlayerMounted = false;
        EntityLivingBase mount;
        if (this.mc.thePlayer.ridingEntity instanceof EntityLivingBase) {
            isPlayerMounted = true;
            mount = (EntityLivingBase) this.mc.thePlayer.ridingEntity;
            health = (int) Math.ceil((double) mount.getHealth());
            healthMax = mount.getMaxHealth();
        }

        this.mc.getTextureManager().bindTexture(INTERFACE);
        switch (RPGHud.rpg_settings.hudtype) {
            case 0:
                this.drawTexturedModalRect(0, 0, 0, 0, 162, 50);
                if (isPlayerMounted) {
                    this.drawTexturedModalRect(51, 39, 163, 0, 92, 20);
                }
                break;
            case 1:
                this.drawTexturedModalRect(0, 0, 0, 50, 162, 50);
                if (isPlayerMounted) {
                    this.drawTexturedModalRect(51, 44, 163, 0, 92, 20);
                }
                break;
            case 2:
                this.drawTexturedModalRect(0, 0, 0, 50, 162, 50);
                if (isPlayerMounted) {
                    this.drawTexturedModalRect(51, 44, 163, 0, 92, 20);
                }
                break;
            case 3:
                this.drawTexturedModalRect(0, height - 16 - 52 + 7, 0, 170, 231, 52);
        }

        this.mc.getTextureManager().bindTexture(this.func_110817_a(this.mc.thePlayer));
        GL11.glScaled(0.5D, 0.25D, 0.5D);
        if (RPGHud.rpg_settings.render_player_face) {
            if (RPGHud.rpg_settings.hudtype != 3) {
                this.drawTexturedModalRect(34, 68, 32, 64, 32, 64);
                this.drawTexturedModalRect(34, 68, 160, 64, 32, 64);
            } else {
                this.drawTexturedModalRect(34, (height - 51 + 7) * 4, 32, 64, 32, 64);
                this.drawTexturedModalRect(34, (height - 51 + 7) * 4, 160, 64, 32, 64);
            }
        }

        GL11.glScaled(2.0D, 4.0D, 2.0D);
        this.mc.getTextureManager().bindTexture(INTERFACE);
        int[] experienceColor = new int[2];
        switch (RPGHud.rpg_settings.color_experience) {
            case 0:
                experienceColor[0] = red[0];
                experienceColor[1] = red[1];
                break;
            case 1:
                experienceColor[0] = blue[0];
                experienceColor[1] = blue[1];
                break;
            case 2:
                experienceColor[0] = green[0];
                experienceColor[1] = green[1];
                break;
            case 3:
                experienceColor[0] = yellow[0];
                experienceColor[1] = yellow[1];
                break;
            case 4:
                experienceColor[0] = white[0];
                experienceColor[1] = white[1];
                break;
            case 5:
                experienceColor[0] = grey[0];
                experienceColor[1] = grey[1];
        }

        switch (RPGHud.rpg_settings.hudtype) {
            case 0:
            case 3:
                drawCustomBar(true, this, 0, height - 10, width, 10, (double) var2 / (double) this.mc.thePlayer.xpBarCap() * 100.0D, -16777216, -12763843, -11776948, experienceColor[1], experienceColor[0]);
                break;
            case 1:
                drawCustomBar(true, this, 49, 35, 88, 8, (double) var2 / (double) this.mc.thePlayer.xpBarCap() * 100.0D, -16777216, -12763843, -11776948, experienceColor[1], experienceColor[0]);
                break;
            case 2:
                this.drawTexturedModalRect(49, 35, 0, 132, (int) (88.0D * ((double) var2 / (double) this.mc.thePlayer.xpBarCap())), 8);
        }

        String var9 = var2 + "/" + this.mc.thePlayer.xpBarCap();
        int var10 = MathHelper.ceiling_float_int(this.mc.thePlayer.getHealth());
        int[] healthColor = new int[4];
        switch (RPGHud.rpg_settings.color_health) {
            case 0:
                healthColor[0] = red[0];
                healthColor[1] = red[1];
                healthColor[2] = red[0] + 17408;
                healthColor[3] = red[1] + 17408;
                break;
            case 1:
                healthColor[0] = blue[0];
                healthColor[1] = blue[1];
                healthColor[2] = blue[0] + 17408;
                healthColor[3] = blue[1] + 17408;
                break;
            case 2:
                healthColor[0] = green[0];
                healthColor[1] = green[1];
                healthColor[2] = green[0] + 4456448;
                healthColor[3] = green[1] + 4456448;
                break;
            case 3:
                healthColor[0] = yellow[0];
                healthColor[1] = yellow[1];
                healthColor[2] = yellow[0] + 4352;
                healthColor[3] = yellow[1] + 8704;
                break;
            case 4:
                healthColor[0] = white[0];
                healthColor[1] = white[1];
                healthColor[2] = white[0] - 2236962;
                healthColor[3] = white[1] - 2236962;
                break;
            case 5:
                healthColor[0] = grey[0];
                healthColor[1] = grey[1];
                healthColor[2] = grey[0] - 2236962;
                healthColor[3] = grey[1] - 2236962;
        }

        switch (RPGHud.rpg_settings.hudtype) {
            case 0:
                if (!this.mc.thePlayer.isPotionActive(Potion.poison)) {
                    drawCustomBar(true, this, 49, 13, 110, 12, (double) var10 / (double) var4 * 100.0D, -16777216, 0, 0, healthColor[1], healthColor[0]);
                } else {
                    drawCustomBar(true, this, 49, 13, 110, 12, (double) var10 / (double) var4 * 100.0D, -16777216, 0, 0, healthColor[3], healthColor[2]);
                }
                break;
            case 1:
                if (!this.mc.thePlayer.isPotionActive(Potion.poison)) {
                    drawCustomBar(true, this, 49, 9, 110, 12, (double) var10 / (double) var4 * 100.0D, -16777216, 0, 0, healthColor[1], healthColor[0]);
                } else {
                    drawCustomBar(true, this, 49, 9, 110, 12, (double) var10 / (double) var4 * 100.0D, -16777216, 0, 0, healthColor[3], healthColor[2]);
                }
                break;
            case 2:
                this.drawTexturedModalRect(49, 9, 0, 100, (int) (110.0D * ((double) var10 / (double) var4)), 12);
                break;
            case 3:
                if (!isPlayerMounted) {
                    if (!this.mc.thePlayer.isPotionActive(Potion.poison)) {
                        drawCustomBar(true, this, 49, height - 16 - 40, 180, 10, (double) var10 / (double) var4 * 100.0D, -16777216, 0, 0, healthColor[1], healthColor[0]);
                    } else {
                        drawCustomBar(true, this, 49, height - 16 - 40, 180, 10, (double) var10 / (double) var4 * 100.0D, -16777216, 0, 0, healthColor[3], healthColor[2]);
                    }
                }
        }

        FoodStats var11 = this.mc.thePlayer.getFoodStats();
        int var12 = var11.getFoodLevel();
        int[] staminaColor = new int[4];
        switch (RPGHud.rpg_settings.color_stamina) {
            case 0:
                staminaColor[0] = red[0];
                staminaColor[1] = red[1];
                staminaColor[2] = red[0] + 17408;
                staminaColor[3] = red[1] + 17408;
                break;
            case 1:
                staminaColor[0] = blue[0];
                staminaColor[1] = blue[1];
                staminaColor[2] = blue[0] + 17408;
                staminaColor[3] = blue[1] + 17408;
                break;
            case 2:
                staminaColor[0] = green[0];
                staminaColor[1] = green[1];
                staminaColor[2] = green[0] + 4456448;
                staminaColor[3] = green[1] + 4456448;
                break;
            case 3:
                staminaColor[0] = yellow[0];
                staminaColor[1] = yellow[1];
                staminaColor[2] = yellow[0] + 4352;
                staminaColor[3] = yellow[1] + 8704;
                break;
            case 4:
                staminaColor[0] = white[0];
                staminaColor[1] = white[1];
                staminaColor[2] = white[0] - 2236962;
                staminaColor[3] = white[1] - 2236962;
                break;
            case 5:
                staminaColor[0] = grey[0];
                staminaColor[1] = grey[1];
                staminaColor[2] = grey[0] - 2236962;
                staminaColor[3] = grey[1] - 2236962;
        }

        switch (RPGHud.rpg_settings.hudtype) {
            case 0:
                if (!this.mc.thePlayer.isPotionActive(Potion.hunger)) {
                    drawCustomBar(true, this, 49, 26, 110, 12, (double) var12 / 20.0D * 100.0D, -16777216, 0, 0, staminaColor[1], staminaColor[0]);
                } else {
                    drawCustomBar(true, this, 49, 26, 110, 12, (double) var12 / 20.0D * 100.0D, -16777216, 0, 0, staminaColor[3], staminaColor[2]);
                }
                break;
            case 1:
                if (!this.mc.thePlayer.isPotionActive(Potion.hunger)) {
                    drawCustomBar(true, this, 49, 22, 110, 12, (double) var12 / 20.0D * 100.0D, -16777216, 0, 0, staminaColor[1], staminaColor[0]);
                } else {
                    drawCustomBar(true, this, 49, 22, 110, 12, (double) var12 / 20.0D * 100.0D, -16777216, 0, 0, staminaColor[3], staminaColor[2]);
                }
                break;
            case 2:
                this.drawTexturedModalRect(49, 22, 110, 100, (int) (110.0D * ((double) var12 / 20.0D)), 12);
                break;
            case 3:
                if (!this.mc.thePlayer.isPotionActive(Potion.hunger)) {
                    drawCustomBar(true, this, 49, height - 16 - 10, 180, 10, (double) var12 / 20.0D * 100.0D, -16777216, 0, 0, staminaColor[1], staminaColor[0]);
                } else {
                    drawCustomBar(true, this, 49, height - 16 - 10, 180, 10, (double) var12 / 20.0D * 100.0D, -16777216, 0, 0, staminaColor[3], staminaColor[2]);
                }
        }

        int var13 = this.mc.thePlayer.getAir();
        int[] airColor = new int[2];
        switch (RPGHud.rpg_settings.color_air) {
            case 0:
                airColor[0] = red[0];
                airColor[1] = red[1];
                break;
            case 1:
                airColor[0] = blue[0];
                airColor[1] = blue[1];
                break;
            case 2:
                airColor[0] = green[0];
                airColor[1] = green[1];
                break;
            case 3:
                airColor[0] = yellow[0];
                airColor[1] = yellow[1];
                break;
            case 4:
                airColor[0] = white[0];
                airColor[1] = white[1];
                break;
            case 5:
                airColor[0] = grey[0];
                airColor[1] = grey[1];
        }

        if (this.mc.thePlayer.isInsideOfMaterial(Material.water)) {
            if (RPGHud.rpg_settings.hudtype == 2) {
                this.drawTexturedModalRect(var7 + 21, height - 80, 0, 160, 141, 10);
                this.drawTexturedModalRect(var7 + 21, height - 80, 0, 140, (int) (141.0D * ((double) var13 / 300.0D)), 10);
            } else {
                drawCustomBar(true, this, var7 + 21, height - 80, 141, 10, (double) var13 / 300.0D * 100.0D, -16777216, -12763843, -11776948, airColor[1], airColor[0]);
            }
        }

        int color;
        if (isPlayerMounted) {
            switch (RPGHud.rpg_settings.hudtype) {
                case 0:
                    drawCustomBar(true, this, 53, 49, 88, 8, (double) health / (double) healthMax * 100.0D, -16777216, 0, 0, healthColor[1], healthColor[0]);
                    break;
                case 1:
                    drawCustomBar(true, this, 53, 54, 88, 8, (double) health / (double) healthMax * 100.0D, -16777216, 0, 0, healthColor[1], healthColor[0]);
                    break;
                case 2:
                    this.drawTexturedModalRect(53, 54, 0, 124, (int) (88.0D * ((double) health / (double) healthMax)), 8);
                    break;
                case 3:
                    drawCustomBar(false, this, 49, height - 32 - 24, 180, 10, (double) health / (double) healthMax * 100.0D, -16777216, 0, 0, healthColor[1], healthColor[0]);
            }

            float var141 = this.mc.thePlayer.getHorseJumpPower();
            boolean var151 = true;
            int var161 = width / 2 - 50;
            color = (int) (var141 * 100.0F);
            int[] text1 = new int[2];
            switch (RPGHud.rpg_settings.color_jumpbar) {
                case 0:
                    text1[0] = red[0];
                    text1[1] = red[1];
                    break;
                case 1:
                    text1[0] = blue[0];
                    text1[1] = blue[1];
                    break;
                case 2:
                    text1[0] = green[0];
                    text1[1] = green[1];
                    break;
                case 3:
                    text1[0] = yellow[0];
                    text1[1] = yellow[1];
                    break;
                case 4:
                    text1[0] = white[0];
                    text1[1] = white[1];
                    break;
                case 5:
                    text1[0] = grey[0];
                    text1[1] = grey[1];
            }

            if (RPGHud.rpg_settings.hudtype == 2) {
                this.drawTexturedModalRect(var7 + 21, height - 80, 0, 160, 141, 10);
                this.drawTexturedModalRect(var7 + 21, height - 80, 0, 150, (int) (141.0D * ((double) color / 100.0D)), 10);
            } else {
                drawCustomBar(true, this, var7 + 21, height - 80, 141, 10, (double) color / 100.0D * 100.0D, -16777216, -12763843, -11776948, text1[1], text1[0]);
            }
        }

        if (!this.mc.gameSettings.showDebugInfo) {
            String var1411 = var10 + "/" + var4;
            String var1511 = var12 + "/" + "20";
            String var1611 = "";
            if (isPlayerMounted) {
                mount = (EntityLivingBase) this.mc.thePlayer.ridingEntity;
                var1611 = (int) Math.ceil((double) mount.getHealth()) + "/" + (int) mount.getMaxHealth();
            }

            switch (RPGHud.rpg_settings.hudtype) {
                case 0:
                    if (RPGHud.rpg_settings.show_numbers_health) {
                        this.drawString(fontrenderer, var1411, 90, 15, -1);
                    }

                    if (RPGHud.rpg_settings.show_numbers_stamina) {
                        this.drawString(fontrenderer, var1511, 90, 28, -1);
                    }

                    if (RPGHud.rpg_settings.show_numbers_experience) {
                        this.drawString(fontrenderer, var9, var7 + 80, height - 9, -1);
                    }

                    GL11.glScaled(0.5D, 0.5D, 0.5D);
                    if (RPGHud.rpg_settings.show_numbers_health) {
                        this.drawString(fontrenderer, var1611, 183, 102, -1);
                    }

                    GL11.glScaled(2.0D, 2.0D, 2.0D);
                    break;
                case 1:
                case 2:
                    if (RPGHud.rpg_settings.show_numbers_health) {
                        this.drawString(fontrenderer, var1411, 90, 11, -1);
                    }

                    if (RPGHud.rpg_settings.show_numbers_stamina) {
                        this.drawString(fontrenderer, var1511, 90, 24, -1);
                    }

                    GL11.glScaled(0.5D, 0.5D, 0.5D);
                    if (RPGHud.rpg_settings.show_numbers_experience) {
                        this.drawString(fontrenderer, var9, 180, 74, -1);
                    }

                    if (RPGHud.rpg_settings.show_numbers_health) {
                        this.drawString(fontrenderer, var1611, 183, 112, -1);
                    }

                    GL11.glScaled(2.0D, 2.0D, 2.0D);
                    break;
                case 3:
                    if (RPGHud.rpg_settings.show_numbers_health && !isPlayerMounted) {
                        this.drawString(fontrenderer, var1411, 120, height - 32 - 30 + 7, -1);
                    }

                    if (RPGHud.rpg_settings.show_numbers_stamina) {
                        this.drawString(fontrenderer, var1511, 120, height - 32 + 7, -1);
                    }

                    if (RPGHud.rpg_settings.show_numbers_experience) {
                        this.drawString(fontrenderer, var9, var7 + 80, height - 9, -1);
                    }

                    if (RPGHud.rpg_settings.show_numbers_health && isPlayerMounted) {
                        this.drawString(fontrenderer, var1611, 120, height - 32 - 30 + 7, -1);
                    }
            }

            color = 8453920;
            String text11 = "" + this.mc.thePlayer.experienceLevel;
            int x = (width - fontrenderer.getStringWidth(text11)) / 2;
            int y = height - 31 - 4;
            if (RPGHud.rpg_settings.hudtype != 3) {
                fontrenderer.drawStringWithShadow(text11, 38 - fontrenderer.getStringWidth(text11) / 2, 38, color);
            } else {
                fontrenderer.drawStringWithShadow(text11, 5 - fontrenderer.getStringWidth(text11) / 2 + 20, height - 21, color);
            }
        }

        if (RPGHud.rpg_settings.enable_status) {
            this.renderStatusEffects();
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (RPGHud.rpg_settings.show_armor) {
            this.renderArmorHelper(width, height);
        }

        this.mc.getTextureManager().bindTexture(Gui.icons);
    }

    protected ResourceLocation func_110817_a(AbstractClientPlayer par1AbstractClientPlayer) {
        return par1AbstractClientPlayer.getLocationSkin();
    }

    protected void renderArmorHelper(int width, int height) {
        this.mc.mcProfiler.startSection("armorhelper");
        this.mc.getTextureManager().bindTexture(DAMAGE);
        if (RPGHud.rpg_settings.show_armor) {
            byte scale = 1;
            switch (RPGHud.rpg_settings.size_armor) {
                case 0:
                    GL11.glScaled(0.5D, 0.5D, 0.5D);
                    scale = 2;
                    break;
                case 1:
                    GL11.glScaled(1.0D, 1.0D, 1.0D);
                    scale = 1;
            }

            for (int i11 = 0; i11 < this.mc.thePlayer.inventory.armorInventory.length; ++i11) {
                if (this.mc.thePlayer.inventory.armorInventory[i11] != null && this.mc.thePlayer.inventory.armorInventory[i11].getItem() instanceof ItemArmor) {
                    int i12 = this.mc.thePlayer.inventory.armorInventory[i11].getMaxDamage();
                    if (this.mc.thePlayer.inventory.armorInventory[i11].getItemDamage() > i12 / 4 * 3) {
                        if (i11 == 0) {
                            this.drawTexturedModalRect(16 * scale, (height - (RPGHud.rpg_settings.hudtype == 3 ? 56 : 0) - (scale == 2 ? 8 : 0)) * scale - 41, 32, 96, 32, 32);
                        }

                        if (i11 == 1) {
                            this.drawTexturedModalRect(16 * scale, (height - (RPGHud.rpg_settings.hudtype == 3 ? 56 : 0) - (scale == 2 ? 8 : 0)) * scale - 63, 32, 64, 32, 32);
                        }

                        if (i11 == 2) {
                            this.drawTexturedModalRect(16 * scale, (height - (RPGHud.rpg_settings.hudtype == 3 ? 56 : 0) - (scale == 2 ? 8 : 0)) * scale - 87, 32, 32, 32, 32);
                        }

                        if (i11 == 3) {
                            this.drawTexturedModalRect(16 * scale, (height - (RPGHud.rpg_settings.hudtype == 3 ? 56 : 0) - (scale == 2 ? 8 : 0)) * scale - 107, 32, 0, 32, 32);
                        }
                    } else if (this.mc.thePlayer.inventory.armorInventory[i11].getItemDamage() > i12 / 2) {
                        if (i11 == 0) {
                            this.drawTexturedModalRect(16 * scale, (height - (RPGHud.rpg_settings.hudtype == 3 ? 56 : 0) - (scale == 2 ? 8 : 0)) * scale - 41, 0, 96, 32, 32);
                        }

                        if (i11 == 1) {
                            this.drawTexturedModalRect(16 * scale, (height - (RPGHud.rpg_settings.hudtype == 3 ? 56 : 0) - (scale == 2 ? 8 : 0)) * scale - 63, 0, 64, 32, 32);
                        }

                        if (i11 == 2) {
                            this.drawTexturedModalRect(16 * scale, (height - (RPGHud.rpg_settings.hudtype == 3 ? 56 : 0) - (scale == 2 ? 8 : 0)) * scale - 87, 0, 32, 32, 32);
                        }

                        if (i11 == 3) {
                            this.drawTexturedModalRect(16 * scale, (height - (RPGHud.rpg_settings.hudtype == 3 ? 56 : 0) - (scale == 2 ? 8 : 0)) * scale - 107, 0, 0, 32, 32);
                        }
                    }
                }
            }
        }

        switch (RPGHud.rpg_settings.size_armor) {
            case 0:
                GL11.glScaled(2.0D, 2.0D, 2.0D);
                break;
            case 1:
                GL11.glScaled(1.0D, 1.0D, 1.0D);
                break;
            case 2:
                GL11.glScaled(0.66D, 0.66D, 0.66D);
        }

        this.mc.getTextureManager().bindTexture(Gui.icons);
        this.mc.mcProfiler.endSection();
    }

    private void renderStatusEffects() {
        ScaledResolution var1 = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
        int width = var1.getScaledWidth();
        int height = var1.getScaledHeight();
        int i = width - 28;
        byte j = 0;
        Collection collection = this.mc.thePlayer.getActivePotionEffects();
        if (!collection.isEmpty()) {
            byte scale = 1;
            switch (RPGHud.rpg_settings.size_status) {
                case 0:
                    GL11.glScaled(0.5D, 0.5D, 0.5D);
                    i = width * 2 - 28;
                    scale = 2;
                    break;
                case 1:
                    GL11.glScaled(1.0D, 1.0D, 1.0D);
                    i = width - 28;
                    scale = 1;
                    break;
                case 2:
                    GL11.glScaled(1.5D, 1.5D, 1.5D);
                    i = width / 2 - 28;
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(2896);
            byte k = 24;

            for (Iterator iterator = this.mc.thePlayer.getActivePotionEffects().iterator(); iterator.hasNext(); i -= k) {
                PotionEffect potioneffect = (PotionEffect) iterator.next();
                Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                String[] potionDuration = Potion.getDurationString(potioneffect).split(":");
                boolean shouldRender = true;
                if (RPGHud.rpg_settings.enable_status_blink) {
                    try {
                        if (Integer.valueOf(potionDuration[0]) == 0 && (Integer.valueOf(potionDuration[1]) == 14 || Integer.valueOf(potionDuration[1]) == 12 || Integer.valueOf(potionDuration[1]) == 10 || Integer.valueOf(potionDuration[1]) == 8 || Integer.valueOf(potionDuration[1]) == 6 || Integer.valueOf(potionDuration[1]) == 4 || Integer.valueOf(potionDuration[1]) == 2)) {
                            shouldRender = false;
                        }
                    } catch (Exception var17) {
                        var17.printStackTrace();
                    }
                }

                this.mc.getTextureManager().bindTexture(ACHIEVEMENTS);
                if (shouldRender) {
                    this.drawTexturedModalRect(i, RPGHud.rpg_settings.hudtype == 3 ? (height - 12) * scale - 24 : j, 0, 202, 26, 26);
                }

                if (potion.hasStatusIcon()) {
                    int l = potion.getStatusIconIndex();
                    this.mc.getTextureManager().bindTexture(INVENTORY);
                    if (shouldRender) {
                        this.drawTexturedModalRect(i + 4, (RPGHud.rpg_settings.hudtype == 3 ? (height - 12) * scale - 24 : j) + 4, 0 + l % 8 * 18, 198 + l / 8 * 18, 18, 18);
                    }
                }
            }

            switch (RPGHud.rpg_settings.size_status) {
                case 0:
                    GL11.glScaled(2.0D, 2.0D, 2.0D);
                    break;
                case 1:
                    GL11.glScaled(1.0D, 1.0D, 1.0D);
                    break;
                case 2:
                    GL11.glScaled(0.66D, 0.66D, 0.66D);
            }
        }
    }
}

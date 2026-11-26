package gg.laydzr.auctions;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public final class LaydzrAuctionsClient implements ClientModInitializer {
    private static KeyBinding openSettings;
    private static boolean settingsOpen = false;
    private static boolean auctionHudEnabled = true;
    private static boolean profitHudEnabled = true;
    private static long profitCents = 0;

    @Override
    public void onInitializeClient() {
        openSettings = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.laydzr_auctions.settings",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_L,
                KeyBinding.Category.MISC
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openSettings.wasPressed()) {
                settingsOpen = !settingsOpen;
                if (settingsOpen) client.setScreen(new LaydzrSettingsScreen(
                        auctionHudEnabled, profitHudEnabled, profitCents,
                        (auction, profit, cents) -> {
                            auctionHudEnabled = auction;
                            profitHudEnabled = profit;
                            profitCents = cents;
                        }));
            }
        });

        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> renderHud(drawContext));
    }

    private static void renderHud(DrawContext ctx) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        int x = 14;
        int y = 18;
        if (auctionHudEnabled) {
            drawPanel(ctx, x, y, 184, 38);
            ctx.drawText(client.textRenderer, Text.literal("LAYDZR  /  AUCTIONS"), x + 10, y + 7, 0xFF55DFFF, false);
            ctx.drawText(client.textRenderer, Text.literal("Press L to configure"), x + 10, y + 21, 0xFFB7DCE8, false);
            y += 46;
        }
        if (profitHudEnabled) {
            drawPanel(ctx, x, y, 184, 34);
            String amount = String.format("$%,.2f", Math.abs(profitCents) / 100.0);
            boolean positive = profitCents >= 0;
            ctx.drawText(client.textRenderer, Text.literal("TOTAL " + (positive ? "PROFIT" : "LOSS")), x + 10, y + 5, 0xFF55DFFF, false);
            ctx.drawText(client.textRenderer, Text.literal((positive ? "+" : "-") + amount), x + 10, y + 18,
                    positive ? 0xFF55E39A : 0xFFFF626E, false);
        }
    }

    private static void drawPanel(DrawContext ctx, int x, int y, int w, int h) {
        ctx.fill(x, y, x + w, y + h, 0xD9101924);
        ctx.fill(x, y, x + w, y + 2, 0xFF28BFEF);
        ctx.fill(x, y + h - 2, x + w, y + h, 0xFF28BFEF);
        ctx.fill(x, y, x + 2, y + h, 0xFF28BFEF);
        ctx.fill(x + w - 2, y, x + w, y + h, 0xFF28BFEF);
    }

    interface SettingsConsumer {
        void accept(boolean auction, boolean profit, long cents);
    }
}

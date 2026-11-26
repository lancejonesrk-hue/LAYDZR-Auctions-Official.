
package gg.laydzr.auctions;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudElementRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public final class LaydzrAuctionsClient implements ClientModInitializer {
    private static KeyMapping openSettings;

    private static boolean auctionHudEnabled = true;
    private static boolean profitHudEnabled = true;
    private static long profitCents = 0;

    private static final int BLUE = 0xFF28BFEF;
    private static final int CYAN = 0xFF55DFFF;

    @Override
    public void onInitializeClient() {
        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath("laydzr_auctions", "main")
        );

        openSettings = KeyMappingHelper.registerKeyMapping(
                new KeyMapping(
                        "key.laydzr_auctions.settings",
                        InputConstants.Type.KEYSYM,
                        InputConstants.KEY_L,
                        category
                )
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openSettings.consumeClick()) {
                client.gui.setScreen(new LaydzrSettingsScreen(
                        auctionHudEnabled,
                        profitHudEnabled,
                        profitCents,
                        client.gui.screen(),
                        (auction, profit, cents) -> {
                            auctionHudEnabled = auction;
                            profitHudEnabled = profit;
                            profitCents = cents;
                        }
                ));
            }
        });

        HudElementRegistry.addLast(
                Identifier.fromNamespaceAndPath("laydzr_auctions", "main_hud"),
                (graphics, deltaTracker) -> renderHud(graphics)
        );
    }

    private static void renderHud(GuiGraphicsExtractor graphics) {
        Minecraft client = Minecraft.getInstance();

        if (client.player == null) return;

        int x = 14;
        int y = 18;

        if (auctionHudEnabled) {
            drawPanel(graphics, x, y, 184, 38);

            graphics.text(client.font, "LAYDZR / AUCTIONS",
                    x + 10, y + 7, CYAN, false);

            graphics.text(client.font, "Press L to configure",
                    x + 10, y + 21, 0xFFB7DCE8, false);

            y += 46;
        }

        if (profitHudEnabled) {
            drawPanel(graphics, x, y, 184, 34);

            String amount = String.format(
                    "$%,.2f", Math.abs(profitCents) / 100.0
            );

            boolean positive = profitCents >= 0;

            graphics.text(client.font,
                    "TOTAL " + (positive ? "PROFIT" : "LOSS"),
                    x + 10, y + 5, CYAN, false);

            graphics.text(client.font,
                    (positive ? "+" : "-") + amount,
                    x + 10, y + 18,
                    positive ? 0xFF55E39A : 0xFFFF626E,
                    false);
        }
    }

    private static void drawPanel(
            GuiGraphicsExtractor graphics,
            int x, int y, int width, int height
    ) {
        graphics.fill(x, y, x + width, y + height, 0xD9101924);
        graphics.fill(x, y, x + width, y + 2, BLUE);
        graphics.fill(x, y + height - 2, x + width, y + height, BLUE);
        graphics.fill(x, y, x + 2, y + height, BLUE);
        graphics.fill(x + width - 2, y, x + width, y + height, BLUE);
    }

    interface SettingsConsumer {
        void accept(boolean auction, boolean profit, long cents);
    }
}


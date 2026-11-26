
package gg.laydzr.auctions;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public final class LaydzrAuctionsClient implements ClientModInitializer {
    private static KeyBinding openSettings;

    private static boolean auctionHudEnabled = true;
    private static boolean profitHudEnabled = true;
    private static long profitCents = 0;

    private static final int BLUE = 0xFF28BFEF;
    private static final int CYAN = 0xFF55DFFF;

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
                client.setScreen(new LaydzrSettingsScreen(
                        auctionHudEnabled,
                        profitHudEnabled,
                        profitCents,
                        (auction, profit, cents) -> {
                            auctionHudEnabled = auction;
                            profitHudEnabled = profit;
                            profitCents = cents;
                        }
                ));
            }
        });

        HudElementRegistry.addLast(
                Identifier.of("laydzr_auctions", "main_hud"),
                (context, tickCounter) -> renderHud(context)
        );
    }

    private static void renderHud(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player == null) return;

        int x = 14;
        int y = 18;

        if (auctionHudEnabled) {
            drawPanel(context, x, y, 184, 38);

            context.drawText(client.textRenderer,
                    Text.literal("LAYDZR / AUCTIONS"),
                    x + 10, y + 7, CYAN, false);

            context.drawText(client.textRenderer,
                    Text.literal("Press L to configure"),
                    x + 10, y + 21, 0xFFB7DCE8, false);

            y += 46;
        }

        if (profitHudEnabled) {
            drawPanel(context, x, y, 184, 34);

            String amount = String.format(
                    "$%,.2f", Math.abs(profitCents) / 100.0
            );

            boolean positive = profitCents >= 0;

            context.drawText(client.textRenderer,
                    Text.literal("TOTAL " + (positive ? "PROFIT" : "LOSS")),
                    x + 10, y + 5, CYAN, false);

            context.drawText(client.textRenderer,
                    Text.literal((positive ? "+" : "-") + amount),
                    x + 10, y + 18,
                    positive ? 0xFF55E39A : 0xFFFF626E,
                    false);
        }
    }

    private static void drawPanel(
            DrawContext context,
            int x, int y, int width, int height
    ) {
        context.fill(x, y, x + width, y + height, 0xD9101924);
        context.fill(x, y, x + width, y + 2, BLUE);
        context.fill(x, y + height - 2, x + width, y + height, BLUE);
        context.fill(x, y, x + 2, y + height, BLUE);
        context.fill(x + width - 2, y, x + width, y + height, BLUE);
    }

    interface SettingsConsumer {
        void accept(boolean auction, boolean profit, long cents);
    }
}

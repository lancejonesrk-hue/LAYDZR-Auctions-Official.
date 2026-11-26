
package gg.laydzr.auctions;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class LaydzrSettingsScreen extends Screen {
    private boolean auctionEnabled;
    private boolean profitEnabled;
    private long profitCents;

    private final Screen parent;
    private final LaydzrAuctionsClient.SettingsConsumer consumer;

    public LaydzrSettingsScreen(
            boolean auction,
            boolean profit,
            long cents,
            Screen parent,
            LaydzrAuctionsClient.SettingsConsumer consumer
    ) {
        super(Component.literal("LAYDZR Auctions Settings"));

        this.auctionEnabled = auction;
        this.profitEnabled = profit;
        this.profitCents = cents;
        this.parent = parent;
        this.consumer = consumer;
    }

    @Override
    protected void init() {
        int center = width / 2;
        int top = height / 2 - 70;

        addRenderableWidget(Button.builder(
                toggleText("Auction HUD", auctionEnabled),
                button -> {
                    auctionEnabled = !auctionEnabled;
                    button.setMessage(toggleText("Auction HUD", auctionEnabled));
                    save();
                }
        ).bounds(center - 105, top, 210, 20).build());

        addRenderableWidget(Button.builder(
                toggleText("Profit/Loss HUD", profitEnabled),
                button -> {
                    profitEnabled = !profitEnabled;
                    button.setMessage(toggleText("Profit/Loss HUD", profitEnabled));
                    save();
                }
        ).bounds(center - 105, top + 27, 210, 20).build());

        addRenderableWidget(Button.builder(
                Component.literal("Add $1,000 profit"),
                button -> {
                    profitCents += 100000;
                    save();
                }
        ).bounds(center - 105, top + 54, 102, 20).build());

        addRenderableWidget(Button.builder(
                Component.literal("Record $1,000 loss"),
                button -> {
                    profitCents -= 100000;
                    save();
                }
        ).bounds(center + 3, top + 54, 102, 20).build());

        addRenderableWidget(Button.builder(
                Component.literal("Reset total"),
                button -> {
                    profitCents = 0;
                    save();
                }
        ).bounds(center - 105, top + 81, 210, 20).build());

        addRenderableWidget(Button.builder(
                Component.literal("Done"),
                button -> onClose()
        ).bounds(center - 105, top + 108, 210, 20).build());
    }

    private Component toggleText(String name, boolean enabled) {
        return Component.literal(
                name + ": " + (enabled ? "ON" : "OFF")
        );
    }

    private void save() {
        consumer.accept(auctionEnabled, profitEnabled, profitCents);
    }

    @Override
    public void extractRenderState(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float delta
    ) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);

        graphics.centeredText(
                font,
                title,
                width / 2,
                height / 2 - 95,
                0xFF55DFFF
        );

        graphics.centeredText(
                font,
                "Local demo controls - no server auction connection yet",
                width / 2,
                height / 2 + 85,
                0xFFB7DCE8
        );
    }

    @Override
    public void onClose() {
        minecraft.gui.setScreen(parent);
    }
}


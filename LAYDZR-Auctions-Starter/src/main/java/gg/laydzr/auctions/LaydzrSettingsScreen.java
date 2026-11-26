
package gg.laydzr.auctions;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public final class LaydzrSettingsScreen extends Screen {
    private boolean auctionEnabled;
    private boolean profitEnabled;
    private long profitCents;

    private final LaydzrAuctionsClient.SettingsConsumer consumer;

    public LaydzrSettingsScreen(
            boolean auction,
            boolean profit,
            long cents,
            LaydzrAuctionsClient.SettingsConsumer consumer
    ) {
        super(Text.literal("LAYDZR Auctions Settings"));

        this.auctionEnabled = auction;
        this.profitEnabled = profit;
        this.profitCents = cents;
        this.consumer = consumer;
    }

    @Override
    protected void init() {
        int center = width / 2;
        int top = height / 2 - 70;

        addDrawableChild(ButtonWidget.builder(
                toggleText("Auction HUD", auctionEnabled),
                button -> {
                    auctionEnabled = !auctionEnabled;
                    button.setMessage(toggleText("Auction HUD", auctionEnabled));
                    save();
                }
        ).dimensions(center - 105, top, 210, 20).build());

        addDrawableChild(ButtonWidget.builder(
                toggleText("Profit/Loss HUD", profitEnabled),
                button -> {
                    profitEnabled = !profitEnabled;
                    button.setMessage(toggleText("Profit/Loss HUD", profitEnabled));
                    save();
                }
        ).dimensions(center - 105, top + 27, 210, 20).build());

        addDrawableChild(ButtonWidget.builder(
                Text.literal("Add $1,000 profit"),
                button -> {
                    profitCents += 100000;
                    save();
                }
        ).dimensions(center - 105, top + 54, 102, 20).build());

        addDrawableChild(ButtonWidget.builder(
                Text.literal("Record $1,000 loss"),
                button -> {
                    profitCents -= 100000;
                    save();
                }
        ).dimensions(center + 3, top + 54, 102, 20).build());

        addDrawableChild(ButtonWidget.builder(
                Text.literal("Reset total"),
                button -> {
                    profitCents = 0;
                    save();
                }
        ).dimensions(center - 105, top + 81, 210, 20).build());

        addDrawableChild(ButtonWidget.builder(
                Text.literal("Done"),
                button -> close()
        ).dimensions(center - 105, top + 108, 210, 20).build());
    }

    private Text toggleText(String name, boolean enabled) {
        return Text.literal(name + ": " + (enabled ? "ON" : "OFF"));
    }

    private void save() {
        consumer.accept(auctionEnabled, profitEnabled, profitCents);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(
                textRenderer,
                title,
                width / 2,
                height / 2 - 95,
                0xFF55DFFF
        );

        context.drawCenteredTextWithShadow(
                textRenderer,
                Text.literal("Local demo controls - no server auction connection yet"),
                width / 2,
                height / 2 + 85,
                0xFFB7DCE8
        );

        super.render(context, mouseX, mouseY, delta);
    }
}

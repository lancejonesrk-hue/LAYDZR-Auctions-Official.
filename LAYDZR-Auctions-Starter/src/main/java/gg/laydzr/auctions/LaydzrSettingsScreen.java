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

    public LaydzrSettingsScreen(boolean auction, boolean profit, long cents,
                                LaydzrAuctionsClient.SettingsConsumer consumer) {
        super(Text.literal("LAYDZR Auctions Settings"));
        this.auctionEnabled = auction;
        this.profitEnabled = profit;
        this.profitCents = cents;
        this.consumer = consumer;
    }

    @Override
    protected void init() {
        int cx = width / 2;
        int top = height / 2 - 70;
        addDrawableChild(ButtonWidget.builder(toggleText("Auction HUD", auctionEnabled), b -> {
            auctionEnabled = !auctionEnabled;
            b.setMessage(toggleText("Auction HUD", auctionEnabled));
            save();
        }).dimensions(cx - 105, top, 210, 20).build());

        addDrawableChild(ButtonWidget.builder(toggleText("Profit/Loss HUD", profitEnabled), b -> {
            profitEnabled = !profitEnabled;
            b.setMessage(toggleText("Profit/Loss HUD", profitEnabled));
            save();
        }).dimensions(cx - 105, top + 27, 210, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Add $1,000 profit"), b -> {
            profitCents += 100000;
            save();
        }).dimensions(cx - 105, top + 54, 102, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Record $1,000 loss"), b -> {
            profitCents -= 100000;
            save();
        }).dimensions(cx + 3, top + 54, 102, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Reset total"), b -> {
            profitCents = 0;
            save();
        }).dimensions(cx - 105, top + 81, 210, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), b -> close())
                .dimensions(cx - 105, top + 108, 210, 20).build());
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
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, height / 2 - 95, 0xFF55DFFF);
        context.drawCenteredTextWithShadow(textRenderer,
                Text.literal("Local demo controls — no server auction connection yet"),
                width / 2, height / 2 + 85, 0xFFB7DCE8);
        super.render(context, mouseX, mouseY, delta);
    }
}

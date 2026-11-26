
package gg.laydzr.auctions;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public final class LaydzrSettingsScreen extends Screen {
    private boolean auctionEnabled;
    private boolean profitEnabled;
    private long profitCents;

    private final LaydzrAuctionsClient.SettingsConsumer consumer;

    private static final int BLUE = 0xFF28BFEF;
    private static final int CYAN = 0xFF55DFFF;
    private static final int WHITE = 0xFFFFFFFF;

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

    private int center() {
        return width / 2;
    }

    private int top() {
        return height / 2 - 100;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);

        int cx = center();
        int y = top();

        context.fill(cx - 155, y - 15, cx + 155, y + 215, 0xF0101928);
        context.fill(cx - 155, y - 15, cx + 155, y - 12, BLUE);
        context.fill(cx - 155, y - 15, cx - 152, y + 215, BLUE);
        context.fill(cx + 152, y - 15, cx + 155, y + 215, BLUE);
        context.fill(cx - 155, y + 212, cx + 155, y + 215, BLUE);

        context.drawCenteredTextWithShadow(
                textRenderer, Text.literal("LAYDZR AUCTIONS"),
                cx, y, CYAN
        );

        context.drawCenteredTextWithShadow(
                textRenderer, Text.literal("SETTINGS"),
                cx, y + 20, WHITE
        );

        drawButton(context, cx - 110, y + 48, 220, 24,
                "Auction HUD: " + (auctionEnabled ? "ON" : "OFF"), mouseX, mouseY);

        drawButton(context, cx - 110, y + 79, 220, 24,
                "Profit/Loss HUD: " + (profitEnabled ? "ON" : "OFF"), mouseX, mouseY);

        drawButton(context, cx - 110, y + 110, 105, 24,
                "+$1,000 PROFIT", mouseX, mouseY);

        drawButton(context, cx + 5, y + 110, 105, 24,
                "-$1,000 LOSS", mouseX, mouseY);

        drawButton(context, cx - 110, y + 141, 220, 24,
                "RESET TOTAL", mouseX, mouseY);

        drawButton(context, cx - 110, y + 172, 220, 24,
                "DONE", mouseX, mouseY);

        context.drawCenteredTextWithShadow(
                textRenderer,
                Text.literal("Total: $" + String.format("%,.2f", profitCents / 100.0)),
                cx, y + 204, CYAN
        );

        super.render(context, mouseX, mouseY, delta);
    }

    private void drawButton(
            DrawContext context,
            int x, int y, int w, int h,
            String label,
            int mouseX, int mouseY
    ) {
        boolean hovered = mouseX >= x && mouseX <= x + w
                && mouseY >= y && mouseY <= y + h;

        context.fill(x, y, x + w, y + h,
                hovered ? 0xFF174B68 : 0xFF142638);

        context.fill(x, y, x + w, y + 2, BLUE);
        context.fill(x, y + h - 2, x + w, y + h, BLUE);
        context.fill(x, y, x + 2, y + h, BLUE);
        context.fill(x + w - 2, y, x + w, y + h, BLUE);

        context.drawCenteredTextWithShadow(
                textRenderer,
                Text.literal(label),
                x + w / 2,
                y + 8,
                WHITE
        );
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (click.button() != 0) {
            return super.mouseClicked(click, doubled);
        }

        double mouseX = click.x();
        double mouseY = click.y();

        int cx = center();
        int y = top();

        if (inside(mouseX, mouseY, cx - 110, y + 48, 220, 24)) {
            auctionEnabled = !auctionEnabled;
            save();
            return true;
        }

        if (inside(mouseX, mouseY, cx - 110, y + 79, 220, 24)) {
            profitEnabled = !profitEnabled;
            save();
            return true;
        }

        if (inside(mouseX, mouseY, cx - 110, y + 110, 105, 24)) {
            profitCents += 100000;
            save();
            return true;
        }

        if (inside(mouseX, mouseY, cx + 5, y + 110, 105, 24)) {
            profitCents -= 100000;
            save();
            return true;
        }

        if (inside(mouseX, mouseY, cx - 110, y + 141, 220, 24)) {
            profitCents = 0;
            save();
            return true;
        }

        if (inside(mouseX, mouseY, cx - 110, y + 172, 220, 24)) {
            close();
            return true;
        }

        return true;
    }

    private boolean inside(
            double mouseX, double mouseY,
            int x, int y, int w, int h
    ) {
        return mouseX >= x && mouseX <= x + w
                && mouseY >= y && mouseY <= y + h;
    }

    private void save() {
        consumer.accept(auctionEnabled, profitEnabled, profitCents);
    }
}

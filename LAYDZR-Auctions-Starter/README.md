# LAYDZR Auctions

A client-side Fabric mod starter for Minecraft Java. Electric-blue LAYDZR styling.

## Build
Requires JDK 25 and internet access.

On Windows:
```powershell
.\gradlew.bat build
```
On macOS/Linux:
```bash
chmod +x gradlew
./gradlew build
```

The mod JAR will be in `build/libs/` (use the non-sources JAR).

## Current starter features
- L key opens/closes the LAYDZR settings screen.
- Auction HUD and Total Profit/Loss HUD toggles.
- Simple locally-recorded profit/loss controls from the settings screen.
- Client-side UI only: live server auction listings/bids require server-specific chat/API integration.

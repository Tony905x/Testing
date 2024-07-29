package com.pettonpc;

import com.google.inject.Provides;
import java.util.List;
import net.runelite.api.Client;
import net.runelite.api.FontID;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.RuneLiteObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.config.ConfigManager;

import javax.inject.Inject;
import java.awt.*;

public class TextOverlay extends Overlay {
	private final NpcFollowerPlugin plugin;
	private final Client client;

	@Inject
	private NpcFollowerConfig config;

	@Inject
	private ClientThread clientThread;

	@Provides
	NpcFollowerConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(NpcFollowerConfig.class);
	}

	@Inject
	public TextOverlay(NpcFollowerPlugin plugin, Client client) {
		this.plugin = plugin;
		this.client = client;
		setPosition(OverlayPosition.DYNAMIC);
	}

	@Override
	public Dimension render(Graphics2D graphics) {
		if (plugin.transmogInitialized) {

			String text = "Hi dad"; // Replace with the text you want to display
			List<RuneLiteObject> transmogObjects = plugin.transmogObjects;
			for (RuneLiteObject transmogObject : transmogObjects) {
				if (transmogObject != null) {
					LocalPoint localPoint = transmogObject.getLocation();
					if (localPoint != null) {
						Point textLocation = Perspective.getCanvasTextLocation(client, graphics, localPoint, text, config.textLocation());
						if (textLocation != null) {
							Font font = FontManager.getRunescapeBoldFont();
//							graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//							graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
//							graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
							graphics.setFont(font);
							Color textColor = Color.YELLOW;
							OverlayUtil.renderTextLocation(graphics, textLocation, text, textColor);
						}
					}
				}
			}
		}
		return null;
	}
}


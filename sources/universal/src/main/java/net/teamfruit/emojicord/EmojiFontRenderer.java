package net.teamfruit.emojicord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import io.netty.util.internal.StringUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.teamfruit.emojicord.compat.OpenGL;

public class EmojiFontRenderer extends FontRenderer {
	private final Map<Integer, Emoji> emojis = new HashMap<>();

	public EmojiFontRenderer(final Minecraft minecraft) {
		super(minecraft.gameSettings, new net.minecraft.util.ResourceLocation("textures/font/ascii.png"),
				minecraft.renderEngine, false);
		super.onResourceManagerReload(minecraft.getResourceManager());
	}

	@Override
	public int getStringWidth(String text) {
		text = getEmojiFormattedString(text);
		return super.getStringWidth(text);
	}

	private String getEmojiFormattedString(String text) {
		String fomattingText;
		if ((EmojicordConfig.renderEnabled) && (!StringUtil.isNullOrEmpty(text))) {
			final String unformattedText = net.minecraft.util.text.TextFormatting
					.getTextWithoutFormattingCodes(text);
			if (StringUtil.isNullOrEmpty(unformattedText))
				return text;
			final List<Pair<Emoji, String>> addedEmojis = new ArrayList<>();
			final List<Pair<EmojiId, String>> emojis = EmojiParser.parse(unformattedText);
			for (final Pair<EmojiId, String> word : emojis)
				if (word.getLeft() != null) {
					final Emoji wordEmoji = EmojiCache.instance.getEmoji(word.getLeft());

					if (wordEmoji != null)
						addedEmojis.add(Pair.of(wordEmoji, word.getRight()));
				}
			fomattingText = text;
			for (final Pair<Emoji, String> entry : addedEmojis) {
				final String emojiText = entry.getValue();
				final int index = fomattingText.indexOf(emojiText);
				this.emojis.put(index, entry.getKey());
				fomattingText = fomattingText.replaceFirst(Pattern.quote(emojiText), "?");
				text = text.replaceFirst("(?i)" + Pattern.quote(emojiText), "?");
			}
		}
		return text;
	}

	@Override
	public int getCharWidth(final char character) {
		if (character == '?')
			return 10;
		return super.getCharWidth(character);
	}

	@Override
	protected void renderStringAtPos(String text, final boolean hasShadow) {
		if (text.isEmpty())
			return;
		this.emojis.clear();
		text = getEmojiFormattedString(text);
		for (int charIndex = 0; charIndex < text.length(); charIndex++) {
			char character = text.charAt(charIndex);
			if ((character == '§') && (charIndex + 1 < text.length())) {
				int formatting = "0123456789abcdefklmnor"
						.indexOf(text.toLowerCase(Locale.ENGLISH).charAt(charIndex + 1));
				if (formatting < 16) {
					this.randomStyle = false;
					this.boldStyle = false;
					this.strikethroughStyle = false;
					this.underlineStyle = false;
					this.italicStyle = false;
					if ((formatting < 0) || (formatting > 15))
						formatting = 15;

					if (hasShadow)
						formatting += 16;

					final int colour = this.colorCode[formatting];
					this.textColor = colour;
					setColor((colour >> 16) / 255.0F, (colour >> 8 & 0xFF) / 255.0F, (colour & 0xFF) / 255.0F,
							this.alpha);
				} else if (formatting == 16)
					this.randomStyle = true;

				else if (formatting == 17)
					this.boldStyle = true;

				else if (formatting == 18)
					this.strikethroughStyle = true;

				else if (formatting == 19)
					this.underlineStyle = true;

				else if (formatting == 20)
					this.italicStyle = true;

				else if (formatting == 21) {
					this.randomStyle = false;
					this.boldStyle = false;
					this.strikethroughStyle = false;
					this.underlineStyle = false;
					this.italicStyle = false;
					setColor(this.red, this.blue, this.green, this.alpha);
				}
				charIndex++;
			} else {
				int c = "?????????????????????????\000\000\000\000\000\000\000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\000????????????????????????????￡?×???????????￢???≪≫???│┤??????????┐└┴┬├─┼???????????????????┘┌?????αβΓπΣσμτΦΘΩδ∞?∈∩≡±????÷?°?・√??■\000"
						.indexOf(character);
				if ((this.randomStyle) && (c != -1)) {
					final int width = getCharWidth(character);
					char newChar;
					for (;;) {
						c = this.fontRandom.nextInt(
								"?????????????????????????\000\000\000\000\000\000\000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\000????????????????????????????￡?×???????????￢???≪≫???│┤??????????┐└┴┬├─┼???????????????????┘┌?????αβΓπΣσμτΦΘΩδ∞?∈∩≡±????÷?°?・√??■\000"
										.length());
						newChar = "?????????????????????????\000\000\000\000\000\000\000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\000????????????????????????????￡?×???????????￢???≪≫???│┤??????????┐└┴┬├─┼???????????????????┘┌?????αβΓπΣσμτΦΘΩδ∞?∈∩≡±????÷?°?・√??■\000"
								.charAt(c);
						if (width == getCharWidth(newChar))
							break;
					}
					character = newChar;
				}
				final float size = (c == -1) || (this.unicodeFlag) ? 0.5F : 1.0F;
				final boolean shadow = ((character == 0) || (c == -1) || (this.unicodeFlag)) && (hasShadow);
				if (shadow) {
					this.posX -= size;
					this.posY -= size;
				}
				float offset = renderChar(character, this.italicStyle, charIndex, hasShadow);
				if (shadow) {
					this.posX += size;
					this.posY += size;
				}
				if (this.boldStyle) {
					this.posX += size;
					if (shadow) {
						this.posX -= size;
						this.posY -= size;
					}
					renderChar(character, this.italicStyle, charIndex, true);
					this.posX -= size;
					if (shadow) {
						this.posX += size;
						this.posY += size;
					}
					offset += 1.0F;
				}
				doDraw(offset);
			}
		}
	}

	private float renderChar(final char c, final boolean italic, final int index, final boolean hasShadow) {
		if (EmojicordConfig.renderEnabled) {
			final Emoji emoji = this.emojis.get(Integer.valueOf(index));
			if (emoji != null)
				if (hasShadow)
					return 10.0F;
				else {
					bindTexture(emoji.getResourceLocationForBinding());
					renderEmoji(emoji);
					return 10.0F;
				}
		}
		if (c == ' ')
			return 4.0F;

		final int charIndex = "?????????????????????????\000\000\000\000\000\000\000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\000????????????????????????????￡?×???????????￢???≪≫???│┤??????????┐└┴┬├─┼???????????????????┘┌?????αβΓπΣσμτΦΘΩδ∞?∈∩≡±????÷?°?・√??■\000"
				.indexOf(c);
		return (charIndex != -1) && (!this.unicodeFlag) ? renderDefaultChar(charIndex, italic)
				: renderUnicodeChar(c, italic);
	}

	private void renderEmoji(final Emoji emoji) {
		final float textureSize = 16.0F;
		final float textureX = 0.0F / textureSize;
		final float textureY = 0.0F / textureSize;
		final float textureOffset = 16.0F / textureSize;
		final float size = 10.0F;
		final float offsetY = 1.0F;
		final float offsetX = 0.0F;

		OpenGL.glPushAttrib();

		//OpenGL.glEnable(GL11.GL_BLEND);
		//OpenGL.glEnable(GL11.GL_ALPHA_TEST);

		OpenGL.glColor4f(1.0F, 1.0F, 1.0F, (OpenGL.glGetColorRGBA() >> 24 & 0xff) / 256f);
		OpenGL.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		//OpenGL.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		//OpenGL.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		//OpenGL.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		//OpenGL.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		OpenGL.glBegin(GL11.GL_QUADS);
		OpenGL.glTexCoord2f(textureX, textureY);
		OpenGL.glVertex3f(this.posX - offsetX, this.posY - offsetY, 0.0F);
		OpenGL.glTexCoord2f(textureX, textureY + textureOffset);
		OpenGL.glVertex3f(this.posX - offsetX, this.posY + size - offsetY, 0.0F);
		OpenGL.glTexCoord2f(textureX + textureOffset, textureY + textureOffset);
		OpenGL.glVertex3f(this.posX - offsetX + size, this.posY + size - offsetY, 0.0F);
		OpenGL.glTexCoord2f(textureX + textureOffset, textureY / textureSize);
		OpenGL.glVertex3f(this.posX - offsetX + size, this.posY - offsetY, 0.0F);
		OpenGL.glEnd();
		//OpenGL.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		//OpenGL.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);

		//OpenGL.glDisable(GL11.GL_ALPHA_TEST);
		//OpenGL.glDisable(GL11.GL_BLEND);

		setColor(this.red, this.green, this.blue, this.alpha);

		OpenGL.glPopAttrib();
	}
}

package net.teamfruit.emojicord;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

public class EmojiParser {
	static final @Nonnull Pattern pattern = Pattern.compile("<a?\\:(\\w+?)\\:([a-zA-Z0-9+/=]+?)>|\\:([\\w+-]+?)\\:(?:\\:skin-tone-(\\d)\\:)?"); // <a?\:(\w+?)\:([a-zA-Z0-9+/]+?)>|\:(\w+?)\:

	public static List<Pair<EmojiId, String>> parse(final String text) {
		final List<Pair<EmojiId, String>> emojis = Lists.newArrayList();
		final Matcher matcher = EmojiParser.pattern.matcher(text);
		while (matcher.find()) {
			final String matched = matcher.group(0);
			final String g2 = matcher.group(2);
			if (!StringUtils.isEmpty(g2))
				if (StringUtils.length(g2)>12)
					emojis.add(Pair.of(EmojiId.DiscordEmojiId.fromDecimalId(g2), matched));
				else
					emojis.add(Pair.of(EmojiId.DiscordEmojiId.fromEncodedId(g2), matched));
			final String g3 = matcher.group(3);
			final String g4 = matcher.group(4);
			if (!StringUtils.isEmpty(g3))
				if (!StringUtils.isEmpty(g4))
					emojis.add(Pair.of(EmojiId.StandardEmojiId.fromEndpoint(g3+":skin-tone-"+g4), matched));
				else {
					EmojiId emoji = EmojiId.StandardEmojiId.fromEndpoint(g3);
					if (emoji==null)
						emoji = EmojiDictionary.instance.get(g3);
					emojis.add(Pair.of(emoji, matched));
				}
		}
		for (final String splitted : StringUtils.split(text))
			if (EmojiId.StandardEmojiId.EMOJI_SHORT.get().contains(splitted)) {
				emojis.add(Pair.of(EmojiId.StandardEmojiId.fromEndpoint(splitted), splitted+" "));
				emojis.add(Pair.of(EmojiId.StandardEmojiId.fromEndpoint(splitted), " "+splitted));
			}
		return emojis;
	}

	public static String encode(final String text) {
		final StringBuffer sb = new StringBuffer();
		final Matcher matcher = EmojiParser.pattern.matcher(text);
		while (matcher.find()) {
			final String g3 = matcher.group(3);
			if (!StringUtils.isEmpty(g3))
				if (EmojiId.StandardEmojiId.fromEndpoint(g3)==null) {
					final EmojiId id = EmojiDictionary.instance.get(g3);
					if (id instanceof EmojiId.DiscordEmojiId)
						matcher.appendReplacement(sb,
								String.format("<:%s:%s>", g3, ((EmojiId.DiscordEmojiId) id).getEncodedId()));
				}
		}
		matcher.appendTail(sb);
		return sb.toString();
	}
}

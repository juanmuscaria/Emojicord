package net.teamfruit.emojicord;

import java.util.List;

public class EmojiModel {
	public static class EmojiGateway {
		public List<String> emojis;
		public List<String> api;
	}

	public static class EmojiStandard {
		public String location;
		public String name;
		public List<String> strings;
	}

	public static class EmojiStandardGroup {
		public List<EmojiStandard> emojis;
		public String location;
	}

	public static class EmojiStandardList {
		public List<EmojiStandardGroup> groups;
	}

	public static class EmojiDiscord {
		public String name;
		public String id;
	}

	public static class EmojiDiscordGroup {
		public List<EmojiDiscord> emojis;
		public String name;
		public String id;
	}

	public static class EmojiDiscordList {
		public List<EmojiDiscordGroup> groups;
	}

	public static class EmojiDiscordIndex {
		public String name;
		public String id;
	}

	public static class EmojiDiscordIndexList {
		public List<EmojiDiscordIndex> indexes;
	}
}

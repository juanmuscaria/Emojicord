package net.teamfruit.emojicord;

import java.io.File;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.teamfruit.emojicord.compat.CompatBaseProxy;
import net.teamfruit.emojicord.compat.CompatBaseProxy.CompatFMLInitializationEvent;
import net.teamfruit.emojicord.compat.CompatBaseProxy.CompatFMLPostInitializationEvent;
import net.teamfruit.emojicord.compat.CompatBaseProxy.CompatFMLPreInitializationEvent;

@Mod(modid = Reference.MODID, name = Reference.NAME, version = VersionReference.VERSION, guiFactory = Reference.GUI_FACTORY, updateJSON = Reference.UPDATE_JSON)
public class Emojicord {
	@Instance(Reference.MODID)
	public static @Nullable Emojicord instance;

	@SidedProxy(serverSide = Reference.PROXY_SERVER, clientSide = Reference.PROXY_CLIENT)
	public static @Nullable CompatBaseProxy proxy;

	@NetworkCheckHandler
	public boolean checkModList(final @Nonnull Map<String, String> versions, final @Nonnull Side side) {
		return true;
	}

	@EventHandler
	public void preInit(final @Nonnull FMLPreInitializationEvent event) {
		if (proxy!=null)
			proxy.preInit(new CompatFMLPreInitializationEventImpl(event));
	}

	@EventHandler
	public void init(final @Nonnull FMLInitializationEvent event) {
		if (proxy!=null)
			proxy.init(new CompatFMLInitializationEventImpl(event));
	}

	@EventHandler
	public void postInit(final @Nonnull FMLPostInitializationEvent event) {
		if (proxy!=null)
			proxy.postInit(new CompatFMLPostInitializationEventImpl(event));
	}

	private static class CompatFMLPreInitializationEventImpl implements CompatFMLPreInitializationEvent {
		private final @Nonnull FMLPreInitializationEvent event;

		public CompatFMLPreInitializationEventImpl(final FMLPreInitializationEvent event) {
			this.event = event;
		}

		@Override
		public File getSuggestedConfigurationFile() {
			return this.event.getSuggestedConfigurationFile();
		}
	}

	private static class CompatFMLInitializationEventImpl implements CompatFMLInitializationEvent {
		public CompatFMLInitializationEventImpl(final FMLInitializationEvent event) {
		}
	}

	private static class CompatFMLPostInitializationEventImpl implements CompatFMLPostInitializationEvent {
		public CompatFMLPostInitializationEventImpl(final FMLPostInitializationEvent event) {
		}
	}
}
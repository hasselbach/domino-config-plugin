package ch.hasselba.xpages.config.plugin;

import com.ibm.xsp.library.AbstractXspLibrary;

public class XspLibrary extends AbstractXspLibrary {
	public static final String LIBRARY_ID = XspLibrary.class.getName();

	public XspLibrary() {

		System.out.println("Loading Domino Config Plugin (" + getPluginId() + ").");

	}

	@Override
	public String getPluginId() {
		return Activator.PLUGIN_ID;

	}

	public String getLibraryId() {
		return LIBRARY_ID;
	}
}

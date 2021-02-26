package ch.hasselba.xpages.config.plugin;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import ch.hasselba.xpages.config.DominoCache;

public class Activator implements BundleActivator {

	public static final String PLUGIN_ID = Activator.class.getPackage().getName();
	
	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		DominoCache.getInstance().destroy();
		Activator.context = null;
	}

}
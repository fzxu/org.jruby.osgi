package org.jruby.osgi.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * Helper methods for the Ruby Runtime.
 * 
 * @author hmalphettes
 */
public class OSGiFileLocator {
	
	public static final String JRUBY_SYMBOLIC_NAME = "org.jruby.jruby";
	
	/**
	 * @return The home for gems and other files as provided by jruby.
	 */
	public static File getJRubyHomeFolder() throws IOException {
		//TODO: system property to override this?
	    //TODO: add some clutches to support jarred up jruby bundles?
	    return getFileInBundle(JRUBY_SYMBOLIC_NAME, "/META-INF/jruby.home");
	}
	
	public static File getFileInBundle(String symbolicName, String path) throws IOException {
		Bundle bundle = getBundle(symbolicName);
		if (bundle == null) {
			throw new IOException("Unable to find the bundle " + symbolicName);
		}
		return getFileInBundle(bundle, path);
	}
	public static File getFileInBundle(Bundle bundle, String path) throws IOException {
		URL url = null;
		try {
			url = getFileURL(bundle.getEntry(path));
			return new File(url.toURI());
		} catch (NullPointerException ne) {
			throw new IOException("Unable to find the " + path + " folder in the bundle '" 
					+ bundle.getSymbolicName() + "'; is the org.jruby.jruby bundle unzipped? ");
		} catch (Exception e) {
			throw new IOException("Unable to find the " + path + " folder in the bundle '" 
					+ bundle.getSymbolicName() + "'", e);
		}
	}

	/**
	 * @param symbolicName
	 * @return The bundle with this symbolic name
	 */
	public static Bundle getBundle(String symbolicName) {
	    BundleContext bc = FrameworkUtil.getBundle(OSGiFileLocator.class).getBundleContext();
	    if (bc == null) {
	        //this should not happen as this bundle is marked as Activation-Policy: lazy
	        throw new IllegalStateException("The bundle "
	                + FrameworkUtil.getBundle(OSGiFileLocator.class).getSymbolicName()
	                + " is not activated.");
	    }
		for (Bundle b : FrameworkUtil.getBundle(OSGiFileLocator.class).getBundleContext().getBundles()) {
			if (b.getSymbolicName().equals(symbolicName)) {
				return b;
			}
		}
		return null;
	}
	
	//introspection on equinox to invoke the getLocalURL method on BundleURLConnection
	private static Method BUNDLE_URL_CONNECTION_getLocalURL = null;
	private static Method BUNDLE_URL_CONNECTION_getFileURL = null;
	/**
	 * Only useful for equinox: on felix we get the file:// or jar:// url already.
	 * Other OSGi implementations have not been tested
	 * <p>
	 * Get a URL to the bundle entry that uses a common protocol (i.e. file:
	 * jar: or http: etc.).  
	 * </p>
	 * @return a URL to the bundle entry that uses a common protocol
	 */
	public static URL getLocalURL(URL url) {
		if ("bundleresource".equals(url.getProtocol()) || "bundleentry".equals(url.getProtocol())) {
			try {
				URLConnection conn = url.openConnection();
				if (BUNDLE_URL_CONNECTION_getLocalURL == null && 
						conn.getClass().getName().equals(
								"org.eclipse.osgi.framework.internal.core.BundleURLConnection")) {
					BUNDLE_URL_CONNECTION_getLocalURL = conn.getClass().getMethod("getLocalURL", null);
					BUNDLE_URL_CONNECTION_getLocalURL.setAccessible(true);
				}
				if (BUNDLE_URL_CONNECTION_getLocalURL != null) {
					return (URL)BUNDLE_URL_CONNECTION_getLocalURL.invoke(conn, null);
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		return url;
	}
	/**
	 * Only useful for equinox: on felix we get the file:// url already.
	 * Other OSGi implementations have not been tested
	 * <p>
	 * Get a URL to the content of the bundle entry that uses the file: protocol.
	 * The content of the bundle entry may be downloaded or extracted to the local
	 * file system in order to create a file: URL.
	 * @return a URL to the content of the bundle entry that uses the file: protocol
	 * </p>
	 */
	public static URL getFileURL(URL url)
	{
		if ("bundleresource".equals(url.getProtocol()) || "bundleentry".equals(url.getProtocol()))
		{
			try
			{
				URLConnection conn = url.openConnection();
				if (BUNDLE_URL_CONNECTION_getFileURL == null && 
						conn.getClass().getName().equals(
								"org.eclipse.osgi.framework.internal.core.BundleURLConnection"))
				{
					BUNDLE_URL_CONNECTION_getFileURL = conn.getClass().getMethod("getFileURL", null);
					BUNDLE_URL_CONNECTION_getFileURL.setAccessible(true);
				}
				if (BUNDLE_URL_CONNECTION_getFileURL != null)
				{
					return (URL)BUNDLE_URL_CONNECTION_getFileURL.invoke(conn, null);
				}
			}
			catch (Throwable t)
			{
				t.printStackTrace();
			}
		}
		return url;
	}
	
}

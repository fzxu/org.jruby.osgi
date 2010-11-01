package org.jruby.osgi.test;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;

import org.jruby.embed.ScriptingContainer;
import org.jruby.osgi.OSGiScriptingContainer;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * Test that jruby can access a class defined in this bundle.
 * 
 * @author hmalphettes
 *
 */
public class TestJRubyWithOSGiBundle {
	
	static OSGiScriptingContainer _container;
	static Bundle _bundle = FrameworkUtil.getBundle(TestJRubyWithOSGiBundle.class);
	
	static Object runScriptlet(Bundle bundle, String path) throws Exception {
		URL url = bundle.getEntry(path);
		InputStream istream = null;
		try {
			istream = new BufferedInputStream(url.openStream());
			return _container.runScriptlet(istream, "bundle:/" + bundle.getSymbolicName()
					+ (path.charAt(0) == '/' ? path : ("/" + path)));
		} finally {
			if (istream != null) istream.close();
		}
	}
	
	@Before public void setup() {
		if (_container == null) {
			_container = new OSGiScriptingContainer(_bundle);
		}
	}

	@Test public void testExtendClassDefinedInOSGiBundle() throws Exception {
		_container.runScriptlet(_bundle, "/ruby/extend_MyClass.rb");
	}
	
}

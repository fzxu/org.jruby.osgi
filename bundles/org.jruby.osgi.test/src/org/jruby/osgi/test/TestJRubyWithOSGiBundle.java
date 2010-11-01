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
		
	@Before public void setup() {
		if (_container == null) {
			_container = new OSGiScriptingContainer(_bundle);
		}
	}

	@Test public void testExtendClassDefinedInOSGiBundle() throws Exception {
		_container.runScriptlet(_bundle, "/ruby/extend_MyClass.rb");
	}
	
}

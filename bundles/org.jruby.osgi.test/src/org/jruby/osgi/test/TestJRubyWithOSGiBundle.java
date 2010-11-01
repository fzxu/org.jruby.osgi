package org.jruby.osgi.test;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.jruby.embed.EvalFailedException;
import org.jruby.embed.ScriptingContainer;
import org.jruby.osgi.OSGiScriptingContainer;
import org.jruby.osgi.utils.OSGiFileLocator;
import org.junit.After;
import org.junit.Assert;
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
	
	@After public void tearDown() {
		_container = null;
	}

	@Test public void testExtendClassDefinedInOSGiBundle() throws Exception {
		_container.runScriptlet(_bundle, "/ruby/extend_MyClass.rb");
	}
	
	@Test public void testRequireExtendClassDefinedInOSGiBundle() throws Exception {
		_container.runScriptlet(_bundle, "/ruby/require_extend_MyClass.rb");
	}
	
	@Test public void testExtendClassDefinedInOSGiBundleLibrary() throws Exception {
		String sampleBundleSymName = "org.jruby.osgi.test.samplebundle";
		String extendMyOtherClassPath = "/ruby/extend_MyOtherClass.rb";
		Bundle bundle = OSGiFileLocator.getBundle(sampleBundleSymName);
		if (bundle == null) {
			throw new IllegalStateException("The tests does not have access to the bundle "
					+ sampleBundleSymName);
		}
		
		//this is expected to fail because the bundle is not registered yet
		//as a library to the jruby runtime:
		EvalFailedException failedEval = null;
		try {
			runScriptletWithoutAddingBundleToJRubyClassPath(_container, bundle, extendMyOtherClassPath);
		} catch (EvalFailedException ev) {
			//ok.
			failedEval = ev;
		}
		Assert.assertNotNull("Should fail until the bundle is added to the jruby's ClassPath", failedEval);
		
		//should not fail.
		_container.runScriptlet(sampleBundleSymName, extendMyOtherClassPath);
		
		//todo: test say_hello_too on MyOtherClass
	}
	
	/**
	 * Same code than in OSGiScriptingContainer#runScriptlet(Bundle bundle, String path)
	 * except that we don't add the bundle to jruby's ClassPath.
	 * @param bundle
	 * @param path
	 * @return
	 */
	private Object runScriptletWithoutAddingBundleToJRubyClassPath(ScriptingContainer container,
			Bundle bundle, String path) {
        URL url = bundle.getEntry(path);
        if (url == null) {
            throw new IllegalArgumentException("Unable to find the entry '" + path
                    + "' in the bundle " + bundle.getSymbolicName());
        }
       // addToClassPath(bundle);
        InputStream istream = null;
        try {
            istream = new BufferedInputStream(url.openStream());
            return container.runScriptlet(istream,
            		"not-the-expected-bundle:/"+ bundle.getSymbolicName() + path);
        } catch (IOException ioe) {
            throw new EvalFailedException(ioe);
        } finally {
            if (istream != null) try { istream.close(); } catch (IOException ioe) {};
        }
		
	}
	
	/**
	 * Tests <code>require 'bundle:/#{bundle.symbolic.name}</code>
	 */
	@Test public void testRequireBundle() {
		_container.runScriptlet(_bundle, "/ruby/require_samplebundle.rb");
	}
	
}

/***** BEGIN LICENSE BLOCK *****
 * Version: CPL 1.0/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Common Public
 * License Version 1.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.eclipse.org/legal/cpl-v10.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Copyright (C) 2002-2011 JRuby Community
 * 
 * Alternatively, the contents of this file may be used under the terms of
 * either of the GNU General Public License Version 2 or later (the "GPL"),
 * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the CPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the CPL, the GPL or the LGPL.
 ***** END LICENSE BLOCK *****/
package org.jruby.embed.osgi.test;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.jruby.embed.EvalFailedException;
import org.jruby.embed.ScriptingContainer;
import org.jruby.embed.osgi.OSGiScriptingContainer;
import org.jruby.embed.osgi.utils.OSGiFileLocator;
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
		String sampleBundleSymName = "org.jruby.embed.osgi.test.samplebundle";
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
            		"not-the-expected-osgibundle:/"+ bundle.getSymbolicName() + path);
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

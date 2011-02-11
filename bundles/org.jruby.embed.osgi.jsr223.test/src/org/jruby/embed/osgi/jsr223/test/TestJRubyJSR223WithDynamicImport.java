/**
 * Public Domain.
 */
package org.jruby.embed.osgi.jsr223.test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.jruby.embed.EvalFailedException;
import org.jruby.embed.LocalContextScope;
import org.jruby.embed.ScriptingContainer;
import org.junit.Before;
import org.junit.Test;

/**
 * This test shows how to support "DynamicImport-Package: *" for a given jruby runtime:
 * <p>
 * <ol>
 * <li>Create an OSGi bundle that declares in its manifest. &quot;DynamicImport-Package: *&quot;
 * Say that the symbolic-name of this bundle is <code>'org.example.bundle.with.dynamic.import.package.star'<code></li>
 * <li>Create a new jruby runtime <code>engine</code> in an OSGi runtime using your favorite API (JSR223 or org.jruby.embed.osgi)</li>
 * <li>Evaluate the ruby code that will add the bundle to the jruby's classloader:
 * <code>engine.eval(&quot;require 'osgibundle:/org.jruby.embed.osgi.jsr223.test'&quot;)</code></li>
 * </ol>
 * This is it.
 * The long discussion is here: http://jira.codehaus.org/browse/JRUBY-5414
 * </p> 
 * 
 * The bundle org.jruby.embed.osgi.jsr223.test contains the 
 * unfamous "DynamicImport-Package: *" in its META-INF/MANIFEST.MF
 * 
 * <p>
 * By adding this bundle to the classpath of the JRuby runtime
 * this JRuby runtime is in fact able to import any packages. 
 * </p>
 * 
 * @author hmalphettes
 */
public class TestJRubyJSR223WithDynamicImport {

	@Before public void setup() {
		//make sure that the classloader for the embedded OSGiScriptingContainer
		//wont' be replaced by a naive classloader by the JRubyEngineFactory.
    	System.setProperty("org.jruby.embed.classloader", "none");
    	
    	//Use many instances of the jruby runtime: otherwise the tests are not decoupled.
    	System.setProperty("org.jruby.embed.localcontext.scope", "singlethread");
    	
	}
	
	/**
	 * Same test that above but without setting up the dynamic import:
	 * it should fail to import the class.
	 * @throws Exception
	 */
	@Test public void testWithoutDynamicallyImportedPackage() throws Exception {
    	//That engine manager has issues running in OSGi so far.
    	//we could register the engine directly on the manager.
    	//or pass it a class that belongs to the jruby bundle so that 
    	//it can locate the services's file.
    	//It defeats the purpose of JSR223 as now it is aware of jruby.
    	//(Confirmation that JSR223 needs a serious update to support OSGi)
    	
    	//note passing the org.jruby.jruby's classloader will enable
    	//the script engine manager to actually locate jruby's ScriptEngine
    	ScriptEngineManager manager = new ScriptEngineManager(
    			ScriptingContainer.class.getClassLoader());
    	ScriptEngine engine = manager.getEngineByName("jruby");
    	engine.eval("require 'java'");
    	try {
    		engine.eval("puts \"org.osgi.service.packageadmin.PackageAdmin.BUNDLE_TYPE_FRAGMENT = " +
        		"#{org.osgi.service.packageadmin.PackageAdmin.BUNDLE_TYPE_FRAGMENT}" + 
    			"\"");
    	} catch (Exception e) {
    		//we should get org.jruby.embed.EvalFailedException: (NameError) cannot load Java class org.osgi.service.packageadmin.PackageAdmin
    		//because there is no DynamicImport-Package to save the day.
    		if (e.getMessage().toLowerCase().indexOf("cannot load java class ") != -1) {
    			return;
    		}
    		throw e;
    	}
    }

	/**
	 * The bundle org.jruby.embed.osgi.jsr223.test contains the 
	 * unfamous "DynamicImport-Package: *" in its META-INF/MANIFEST.MF
	 * <p>
	 * By adding this bundle to the classpath of the JRuby runtime
	 * this JRuby runtime is in fact able to import any packages. 
	 * </p>
	 */
	@Test public void testDynamicallyImportedPackage() throws Exception {
    	//That engine manager has issues running in OSGi so far.
    	//we could register the engine directly on the manager.
    	//or pass it a class that belongs to the jruby bundle so that 
    	//it can locate the services's file.
    	//It defeats the purpose of JSR223 as now it is aware of
    	//
    	//(Confirmation that JSR223 needs a serious update)
    	
    	//note passing the org.jruby.jruby's classloader will enable
    	//the script engine manager to actually locate jruby's ScriptEngine
    	ScriptEngineManager manager = new ScriptEngineManager(
    			ScriptingContainer.class.getClassLoader());
    	System.setProperty("org.jruby.embed.classloader", "none");
    	ScriptEngine engine = manager.getEngineByName("jruby");
    	engine.eval("require 'osgibundle:/org.jruby.embed.osgi.jsr223.test'");
    	engine.eval("require 'java'");
    	//let's read a constant defined on the PackageAdmin object.
    	//we have not imported the PackageAdmin so this will in fact test the DynamicImport-Package: *
    	engine.eval("puts \"org.osgi.service.packageadmin.PackageAdmin.BUNDLE_TYPE_FRAGMENT = " +
        		"#{org.osgi.service.packageadmin.PackageAdmin.BUNDLE_TYPE_FRAGMENT}" + 
    		"\"");
    }


	
}

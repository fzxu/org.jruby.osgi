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

package org.jruby.embed.osgi.jsr223.test;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.jruby.embed.ScriptingContainer;
import org.junit.Test;

/**
 * @author hmalphettes
 * 
 * Simple test the bindings. No OSGi specifics involved here.
 */
public class TestJRubyJSR223 {

//	private static String puts_x="puts x";
        
    @Test public void testHelloWorldJSR223() throws Exception {
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
    	ScriptEngine engine = manager.getEngineByName("jruby");
    	
    	engine.eval("puts \"Hello World!\"");
    }
    @Test public void testWrappedException() throws Exception {
    	ScriptEngineManager manager = new ScriptEngineManager(
    			ScriptingContainer.class.getClassLoader());
    	ScriptEngine engine = manager.getEngineByName("jruby");
    	try {
    		engine.eval("a = nil\n" +
    				"a.hello");
    	} catch (ScriptException se) {
//    		Throwable t = se;
//    		while (t != null && t.getCause() != t) {
//    			System.err.println("Hierarchy of causes " + t.getMessage() + " " + t.getClass().getSimpleName());
//    			t = t.getCause();
//    		}
    		return;
    	}
    }
    
    @Test public void testWrappedException2() throws Exception {
    	ScriptEngineManager manager = new ScriptEngineManager(
    			ScriptingContainer.class.getClassLoader());
    	ScriptEngine engine = manager.getEngineByName("jruby");
    	try {
    		Invocable inv = (Invocable)engine;
    		inv.invokeFunction("thisMethodDoesNotExist", new Object());
    	} catch (ScriptException se) {
    		Throwable t = se;
    		while (t != null && t.getCause() != t) {
    			System.err.println("Hierarchy of causes " + t.getMessage() + " " + t.getClass().getSimpleName());
    			t = t.getCause();
    		}
    		throw se;
    	}
    }
    
}

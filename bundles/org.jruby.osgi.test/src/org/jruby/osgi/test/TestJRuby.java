/**
 * Copyright (C) 1999-2010, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with
 * the written permission of Intalio Inc. or in accordance with
 * the terms and conditions stipulated in the agreement/contract
 * under which the program(s) have been supplied.
 */
package org.jruby.osgi.test;

import java.util.ArrayList;
import java.util.List;

import org.jruby.embed.ScriptingContainer;
import org.junit.Test;

/**
 * @author hmalphettes
 * 
 * Simple test the bindings.
 * 
 */
public class TestJRuby {

    private static String puts_x="puts x";
        
    @Test public void testPuts_persist() {
        ScriptingContainer container = new ScriptingContainer();
        container.put("x", 12345);
        container.runScriptlet(puts_x);
        System.err.println("Persistent: And now try again");
        container.runScriptlet(puts_x);
    }
    @Test public void testEachArrayList() {
        List<Object> list = new ArrayList<Object>();
        list.add("one");
        list.add("two");
        ScriptingContainer container = new ScriptingContainer();
        container.put("list", list);
        String each = "list.each do |v|\n"
            + "  puts v\n"
            + "end";
        container.runScriptlet(each);
    }
    
}

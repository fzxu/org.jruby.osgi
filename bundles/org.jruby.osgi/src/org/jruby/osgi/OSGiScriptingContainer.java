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
 * Copyright (C) 2002-2010 JRuby Community
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

package org.jruby.osgi;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.jruby.embed.EmbedEvalUnit;
import org.jruby.embed.EvalFailedException;
import org.jruby.embed.LocalContextScope;
import org.jruby.embed.LocalVariableBehavior;
import org.jruby.embed.ScriptingContainer;
import org.jruby.osgi.internal.JRubyOSGiBundleClassLoader;
import org.jruby.osgi.utils.OSGiFileLocator;
import org.osgi.framework.Bundle;

/**
 * Helpers to create a ScriptingContainer and set it up so it lives as well
 * as possible in the OSGi world.
 * <p>
 * Currently:
 * <ol>
 * <li>Access to the java classes and resources provided by the osgi bundle.</li>
 * <li>Setup of jruby home pointing at the jruby bundle. Supporting unzipped jruby bundle for now.</li>
 * </ol>
 * </p>
 * <p>
 * TODO: look into using the LoadService of jruby.
 * Look if it would be possible to reuse the base runtime and minimize the cost of new
 * jruby runtimes. 
 * </p>
 * @author hmalphettes
 *
 */
public class OSGiScriptingContainer extends ScriptingContainer {

    /**
     * @return A scripting container where the classloader can find classes
     * in the osgi creator bundle and where the jruby home is set to point to
     * the one in the jruby's bundle home folder.
     * scope: LocalContextScope.SINGLETHREAD; behavior: LocalVariableBehavior.TRANSIENT
     */
    public OSGiScriptingContainer(Bundle creator) {
        this(creator, null, null);
    }
    /**
     * @param scope if null, LocalContextScope.SINGLETHREAD
     * @param behavior if null, LocalVariableBehavior.TRANSIENT
     * @return A scripting container where the classloader can find classes
     * in the osgi creator bundle and where the jruby home is set to point to
     * the one in the jruby's bundle home folder.
     */
    public OSGiScriptingContainer(Bundle creator,
            LocalContextScope scope, LocalVariableBehavior behavior) {
        if (scope == null) {
            scope = LocalContextScope.SINGLETHREAD;
        }
        if (behavior == null) {
            behavior = LocalVariableBehavior.TRANSIENT;
        }
        ScriptingContainer sc = new ScriptingContainer(scope, behavior);
        if (creator != null) {
            sc.setClassLoader(new JRubyOSGiBundleClassLoader(creator));
        } else {
            sc.setClassLoader(new JRubyOSGiBundleClassLoader());
        }
        try {
            sc.setHomeDirectory(OSGiFileLocator.getJRubyHomeFolder().getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * @param bundle The bundle where the script is located.
     * @param path The entry in the bundle
     * @return
     */
    public Object runScriptlet(Bundle bundle, String path) {
        URL url = bundle.getEntry(path);
        InputStream istream = null;
        try {
            istream = new BufferedInputStream(url.openStream());
            return this.runScriptlet(istream, "bundle:/" + bundle.getSymbolicName()
                    + (path.charAt(0) == '/' ? path : ("/" + path)));
        } catch (IOException ioe) {
            throw new EvalFailedException(ioe);
        } finally {
            if (istream != null) try { istream.close(); } catch (IOException ioe) {};
        }
    }

    /**
     * Parses a script given by a input stream and return an object which can be run().
     * This allows the script to be parsed once and evaluated many times.
     * 
     * @param bundle is where the script is located
     * @param path is the entry in the bundle.
     * @param lines are linenumbers to display for parse errors and backtraces.
     *        This field is optional. Only the first argument is used for parsing.
     *        When no line number is specified, 0 is applied to.
     * @return an object which can be run
     */
    public EmbedEvalUnit parse(Bundle bundle, String path, int... lines) throws IOException {
        URL url = bundle.getEntry(path);
        InputStream istream = null;
        try {
            istream = new BufferedInputStream(url.openStream());
            return super.parse(istream, "bundle:/" + bundle.getSymbolicName()
                    + (path.charAt(0) == '/' ? path : ("/" + path)));
        } catch (IOException ioe) {
            throw new EvalFailedException(ioe);
        } finally {
            if (istream != null) try { istream.close(); } catch (IOException ioe) {};
        }
    }
}
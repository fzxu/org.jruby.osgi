/**
 * Copyright (C) 1999-2010, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with
 * the written permission of Intalio Inc. or in accordance with
 * the terms and conditions stipulated in the agreement/contract
 * under which the program(s) have been supplied.
 */
package org.jruby.osgi.internal;

import org.jruby.runtime.load.LoadService.AlreadyLoaded;
import org.jruby.runtime.load.LoadService.LoadSearcher;
import org.jruby.runtime.load.LoadService.SearchState;

/**
 * @author hmalphettes
 * 
 * TODO: something nice with this?
 */
public class OSGiBundlesSearcher implements LoadSearcher {

    /* (non-Javadoc)
     * @see org.jruby.runtime.load.LoadService.LoadSearcher#shouldTrySearch(org.jruby.runtime.load.LoadService.SearchState)
     */
    public boolean shouldTrySearch(SearchState state) {
        return false;
    }

    /* (non-Javadoc)
     * @see org.jruby.runtime.load.LoadService.LoadSearcher#trySearch(org.jruby.runtime.load.LoadService.SearchState)
     */
    public void trySearch(SearchState state) throws AlreadyLoaded {
        
    }

}

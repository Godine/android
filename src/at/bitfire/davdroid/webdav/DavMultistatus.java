/*
 * Copyright © 2013 – 2015 Ricki Hirner (bitfire web engineering).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */
package at.bitfire.davdroid.webdav;

import at.bitfire.davdroid.webdav.*;
import at.bitfire.davdroid.webdav.DavResponse;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

import java.util.List;

@Namespace(reference="DAV:")
@Root(strict=false)
public class DavMultistatus {
	@ElementList(inline=true,entry="response",required=false)
	List<at.bitfire.davdroid.webdav.DavResponse> response;
}

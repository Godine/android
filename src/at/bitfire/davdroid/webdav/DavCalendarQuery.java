/*
 * Copyright © 2013 – 2015 Ricki Hirner (bitfire web engineering).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package at.bitfire.davdroid.webdav;

import at.bitfire.davdroid.webdav.DavFilter;
import at.bitfire.davdroid.webdav.DavProp;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;

import lombok.Getter;
import lombok.Setter;

@Root(name="calendar-query")
@NamespaceList({
		@Namespace(reference="DAV:"),
		@Namespace(prefix="C",reference="urn:ietf:params:xml:ns:caldav")
})
@Namespace(prefix="C",reference="urn:ietf:params:xml:ns:caldav")
public class DavCalendarQuery {
	@Element
	@Getter	@Setter
	at.bitfire.davdroid.webdav.DavProp prop;

	@Element
	@Getter @Setter
	at.bitfire.davdroid.webdav.DavFilter filter;
}

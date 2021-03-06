/*
 * Copyright © 2013 – 2015 Ricki Hirner (bitfire web engineering).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */
package at.bitfire.davdroid.webdav;

import at.bitfire.davdroid.webdav.HttpException;

import org.apache.http.HttpStatus;

public class PreconditionFailedException extends at.bitfire.davdroid.webdav.HttpException {
	private static final long serialVersionUID = 102282229174086113L;
	
	public PreconditionFailedException(String reason) {
		super(HttpStatus.SC_PRECONDITION_FAILED, reason);
	}
}

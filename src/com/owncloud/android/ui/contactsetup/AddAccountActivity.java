/*
 * Copyright © 2013 – 2015 Ricki Hirner (bitfire web engineering).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */
package com.owncloud.android.ui.contactsetup;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import at.bitfire.davdroid.Constants;
import com.owncloud.android.R;
import at.bitfire.davdroid.resource.ServerInfo;

public class AddAccountActivity extends Activity {

	protected ServerInfo serverInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.setup_add_account);

		if (savedInstanceState == null) {

			Bundle paramBundle = getIntent().getExtras();
			String urlParam = paramBundle.getString("urlParam");
			String usernameParam = paramBundle.getString("usernameParam");
			String passwordParam = paramBundle.getString("passwordParam");

			System.out.println("Got Parameters urlParam: " + urlParam
			+ " usernameParam: " + usernameParam
			+ " passwordParam: " + passwordParam);

			FragmentTransaction ft = getFragmentManager().beginTransaction();

			Bundle args = new Bundle();

			args.putString(QueryServerDialogFragment.EXTRA_BASE_URI, urlParam);
			args.putString(QueryServerDialogFragment.EXTRA_USER_NAME, usernameParam);
			args.putString(QueryServerDialogFragment.EXTRA_PASSWORD,passwordParam);
			args.putBoolean(QueryServerDialogFragment.EXTRA_AUTH_PREEMPTIVE, true);

			DialogFragment dialog = new QueryServerDialogFragment();
			dialog.setArguments(args);
			dialog.show(ft, QueryServerDialogFragment.class.getName());
		}

	}

}

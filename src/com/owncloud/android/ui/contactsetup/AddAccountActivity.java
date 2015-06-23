/*
 * Copyright © 2013 – 2015 Ricki Hirner (bitfire web engineering).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */
package com.owncloud.android.ui.contactsetup;

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
		
		if (savedInstanceState == null) {	// first call
			//getFragmentManager().beginTransaction()
			//	.add(R.id.right_pane, new LoginTypeFragment())
			//	.commit();

			FragmentTransaction ft = getFragmentManager().beginTransaction();

			Bundle args = new Bundle();
			//try {
			args.putString(QueryServerDialogFragment.EXTRA_BASE_URI, "https://moncloud.iam.ma");
			// } catch (URISyntaxException e) {
			//}
			args.putString(QueryServerDialogFragment.EXTRA_USER_NAME, "testib@ib-maroc.com");
			args.putString(QueryServerDialogFragment.EXTRA_PASSWORD,"IB@Cloud2015");
			args.putBoolean(QueryServerDialogFragment.EXTRA_AUTH_PREEMPTIVE, true);

			DialogFragment dialog = new QueryServerDialogFragment();
			dialog.setArguments(args);
			dialog.show(ft, QueryServerDialogFragment.class.getName());
		}
	}

	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.setup_add_account, menu);
		return true;
	}

	public void showHelp(MenuItem item) {
		startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.WEB_URL_HELP)), 0);
	}
	*/

}

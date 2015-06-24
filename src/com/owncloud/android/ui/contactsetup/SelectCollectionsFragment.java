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
import android.app.ListFragment;
import android.content.ContentResolver;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;


import com.owncloud.android.R;

import java.util.List;

import at.bitfire.davdroid.Constants;
import at.bitfire.davdroid.resource.LocalCalendar;
import at.bitfire.davdroid.resource.LocalStorageException;
import at.bitfire.davdroid.resource.ServerInfo;
import at.bitfire.davdroid.syncadapter.AccountSettings;

public class SelectCollectionsFragment extends ListFragment {
	public static final String TAG = "davdroid.AccountDetails";

	protected ServerInfo serverInfo;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		serverInfo = ((AddAccountActivity)getActivity()).serverInfo;

		View v = super.onCreateView(inflater, container, savedInstanceState);
		setHasOptionsMenu(true);

		return v;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		setListAdapter(null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		final ListView listView = getListView();
		listView.setPadding(20, 30, 20, 30);
		
		View header = getActivity().getLayoutInflater().inflate(R.layout.setup_select_collections_header, getListView(), false);
		listView.addHeaderView(header, getListView(), false);
		
		final SelectCollectionsAdapter adapter = new SelectCollectionsAdapter(view.getContext(), serverInfo);
		setListAdapter(adapter);
		
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int itemPosition = position - 1;    // one list header view at pos. 0
				if (adapter.getItemViewType(itemPosition) == SelectCollectionsAdapter.TYPE_ADDRESS_BOOKS_ROW) {
					// unselect all other address books
					for (int pos = 1; pos <= adapter.getNAddressBooks(); pos++)
						if (pos != itemPosition)
							listView.setItemChecked(pos + 1, false);
				}

				getActivity().invalidateOptionsMenu();
			}
		});
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		System.out.println("onCreateOptionsMenu");

		inflater.inflate(R.menu.setup_account_details, menu);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		boolean ok = false;
		try {
			ok = getListView().getCheckedItemCount() > 0;
		} catch(IllegalStateException e) {
		}
		MenuItem item = menu.findItem(R.id.add_account);
		item.setEnabled(ok);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add_account:
			// synchronize only selected collections
			for (ServerInfo.ResourceInfo addressBook : serverInfo.getAddressBooks())
				addressBook.setEnabled(false);
			for (ServerInfo.ResourceInfo calendar : serverInfo.getCalendars())
				calendar.setEnabled(false);
			for (ServerInfo.ResourceInfo todoList : serverInfo.getTodoLists())
				todoList.setEnabled(false);
			
			ListAdapter adapter = getListView().getAdapter();
			for (long id : getListView().getCheckedItemIds()) {
				int position = (int)id + 1;		// +1 because header view is inserted at pos. 0 
				ServerInfo.ResourceInfo info = (ServerInfo.ResourceInfo)adapter.getItem(position);
				info.setEnabled(true);
			}

			addAccount();

			break;
		default:
			return false;
		}
		return true;
	}

	void addAccount() {
		String accountName = serverInfo.getUserName();

		AccountManager accountManager = AccountManager.get(getActivity());
		Account account = new Account(accountName, Constants.ACCOUNT_TYPE);
		Bundle userData = AccountSettings.createBundle(serverInfo);

		if (accountManager.addAccountExplicitly(account, serverInfo.getPassword(), userData)) {
			addSync(account, ContactsContract.AUTHORITY, serverInfo.getAddressBooks(), null);

			addSync(account, CalendarContract.AUTHORITY, serverInfo.getCalendars(), new AddSyncCallback() {
				@Override
				public void createLocalCollection(Account account, ServerInfo.ResourceInfo calendar) throws LocalStorageException {
					LocalCalendar.create(account, getActivity().getContentResolver(), calendar);
				}
			});

			getActivity().finish();
		} else
			Toast.makeText(getActivity(), "Couldn't create account (account with this name already existing?)", Toast.LENGTH_LONG).show();
	}

	protected interface AddSyncCallback {
		void createLocalCollection(Account account, ServerInfo.ResourceInfo resource) throws LocalStorageException;
	}


	protected void addSync(Account account, String authority, List<ServerInfo.ResourceInfo> resourceList, AddSyncCallback callback) {
		boolean sync = false;
		for (ServerInfo.ResourceInfo resource : resourceList)
			if (resource.isEnabled()) {
				sync = true;
				if (callback != null)
					try {
						callback.createLocalCollection(account, resource);
					} catch(LocalStorageException e) {
						Log.e(TAG, "Couldn't add sync collection", e);
						Toast.makeText(getActivity(), "Couldn't set up synchronization for " + authority, Toast.LENGTH_LONG).show();
					}
			}
		if (sync) {
			ContentResolver.setIsSyncable(account, authority, 1);
			ContentResolver.setSyncAutomatically(account, authority, true);
		} else
			ContentResolver.setIsSyncable(account, authority, 0);
	}

}

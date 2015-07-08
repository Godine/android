package com.owncloud.android.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.owncloud.android.datamodel.contact.ContactAccount;
import com.owncloud.android.ui.activity.Preferences;
import com.owncloud.android.utils.ContactUtils;

public class ContactSynchronizationService extends IntentService {

    public static final String BUNDLE_ACCOUNT = "BUNDLE_ACCOUNT";
    public static final String BUNDLE_RESULT_RECEIVER = "BUNDLE_RESULT_RECEIVER";

    public ContactSynchronizationService() {
        super("ContactSynchronizationService");
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        final ContactAccount account = intent.getParcelableExtra(BUNDLE_ACCOUNT);
        final ResultReceiver resultReceiver = intent.getParcelableExtra(BUNDLE_RESULT_RECEIVER);

        if (account != null) {
            Log.i("Synchro", "Service start synchro for account " + account.getName());
            ContactUtils.synchronizeContactForAccount(getApplicationContext(), account);
            resultReceiver.send(0, new Bundle());
        } else {
            Log.e("Synchro", "Unable to synchronize account null");
            resultReceiver.send(1, new Bundle());
        }
    }
}

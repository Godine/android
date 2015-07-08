package com.owncloud.android.ui.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.owncloud.android.R;
import com.owncloud.android.datamodel.contact.ContactAccount;
import com.owncloud.android.ui.adapter.ContactAccountAdapter;

import java.util.ArrayList;

public class AccountsDialogFragment extends DialogFragment {

    private static final String BUNDLE_ACCOUNT = "BUNDLE_ACCOUNT";

    private ListView mListView;
    private ArrayList<ContactAccount> mAccounts;

    public static AccountsDialogFragment newInstance(ArrayList<ContactAccount> accounts) {
        AccountsDialogFragment fragment = new AccountsDialogFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(BUNDLE_ACCOUNT, accounts);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAccounts = getArguments().getParcelableArrayList(BUNDLE_ACCOUNT);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        getDialog().setTitle(getString(R.string.contact_to_synchronize));

        View view = inflater.inflate(R.layout.fragment_accounts, container, false);
        mListView = (ListView) view.findViewById(R.id.listview);
        mListView.setAdapter(new ContactAccountAdapter(getActivity(), mAccounts));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                final ContactAccount contactAccount = mAccounts.get(position);
                if (getActivity() != null && (getActivity() instanceof AccountChooseListener)) {
                    ((AccountChooseListener) getActivity()).onAccountChosen(contactAccount);
                }
                getDialog().dismiss();
            }
        });
        return view;
    }

    public interface AccountChooseListener {
        void onAccountChosen(ContactAccount account);
    }
}

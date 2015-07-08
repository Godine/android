package com.owncloud.android.ui.adapter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.owncloud.android.R;
import com.owncloud.android.datamodel.contact.ContactAccount;

import java.util.ArrayList;

public  class ContactAccountAdapter extends BaseAdapter {

    private final Context mContext;
    private ArrayList<ContactAccount> mAccounts;

    public ContactAccountAdapter(Context context, ArrayList<ContactAccount> accounts) {
        mContext = context;
        mAccounts = accounts;
    }

    @Override
    public int getCount() {
        return mAccounts.size();
    }

    @Override
    public ContactAccount getItem(final int position) {
        return mAccounts.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View v = convertView;
        ViewHolder holder;

        if (v == null) {
            v = LayoutInflater.from(mContext).inflate(R.layout.cellview_account, parent, false);
            holder = new ViewHolder();
            holder.accountNameTextView = (TextView) v.findViewById(R.id.textview_account_name);
            holder.accountSizeTextView = (TextView) v.findViewById(R.id.textview_account_size);
            holder.accountImageView = (ImageView) v.findViewById(R.id.imageview_account);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        final ContactAccount item = getItem(position);
        holder.accountNameTextView.setText(item.getName());
        holder.accountSizeTextView.setText(item.getCount() + " contacts");
        final Drawable iconForAccount = getIconForAccount(getAccountFromTypeAndName(item));
        holder.accountImageView.setImageDrawable(iconForAccount);
        if (iconForAccount != null) {
            holder.accountImageView.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
        } else {
            holder.accountImageView.setBackgroundColor(mContext.getResources().getColor(android.R.color.darker_gray));
        }
        return v;
    }

    private Account getAccountFromTypeAndName(ContactAccount contactAccount) {
        final Account[] accountsByType = AccountManager.get(mContext).getAccountsByType(contactAccount.getType());
        for (final Account account : accountsByType) {
            if (account.name.equals(contactAccount.getName())) {
                return account;
            }
        }
        return null;
    }

    private Drawable getIconForAccount(Account account) {
        if (account == null) {
            return null;
        }
        AuthenticatorDescription[] descriptions = AccountManager.get(mContext).getAuthenticatorTypes();
        for (AuthenticatorDescription description : descriptions) {
            if (description.type.equals(account.type)) {
                PackageManager pm = mContext.getPackageManager();
                return pm.getDrawable(description.packageName, description.iconId, null);
            }
        }
        return null;
    }

    private class ViewHolder {
        ImageView accountImageView;
        TextView accountNameTextView;
        TextView accountSizeTextView;
    }
}

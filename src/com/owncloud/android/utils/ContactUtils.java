package com.owncloud.android.utils;

import android.accounts.Account;
import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

import com.owncloud.android.authentication.AccountUtils;
import com.owncloud.android.datamodel.contact.Address;
import com.owncloud.android.datamodel.contact.Contact;
import com.owncloud.android.datamodel.contact.ContactAccount;
import com.owncloud.android.datamodel.contact.Email;
import com.owncloud.android.datamodel.contact.Im;
import com.owncloud.android.datamodel.contact.Phone;
import com.owncloud.android.datamodel.contact.Website;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.provider.ContactsContract.AUTHORITY;
import static android.provider.ContactsContract.CommonDataKinds;
import static android.provider.ContactsContract.CommonDataKinds.Event;
import static android.provider.ContactsContract.CommonDataKinds.Nickname;
import static android.provider.ContactsContract.CommonDataKinds.Note;
import static android.provider.ContactsContract.CommonDataKinds.Organization;
import static android.provider.ContactsContract.CommonDataKinds.Photo;
import static android.provider.ContactsContract.CommonDataKinds.StructuredName;
import static android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import static android.provider.ContactsContract.Contacts;
import static android.provider.ContactsContract.Data;
import static android.provider.ContactsContract.RawContacts;

public class ContactUtils {

    // ---------------------------------
    // CONSTANTS
    // ---------------------------------
    private static final String[] PROJECTION = new String[]{
            Contacts._ID,
            Contacts.DISPLAY_NAME
    };
    private static final String SELECTION_FOR_ACCOUNT = RawContacts.ACCOUNT_TYPE + "=? AND " + RawContacts.ACCOUNT_NAME + "=?";

    // ---------------------------------
    // PUBLIC
    // ---------------------------------
    public static void synchronizeContactForAccount(Context context, ContactAccount contactAccount) {
        final Cursor cursor = context.getContentResolver().query(RawContacts.CONTENT_URI,
                PROJECTION,
                SELECTION_FOR_ACCOUNT,
                new String[]{contactAccount.getType(), contactAccount.getName()},
                Contacts.DISPLAY_NAME);
        if (cursor != null) {
            List<Contact> contacts = new ArrayList<>();
            while (cursor.moveToNext()) {
                long contactId = cursor.getLong(cursor.getColumnIndex(Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(Contacts.DISPLAY_NAME));

                if (contactId != 0 && name != null) {
                    Contact contact = ContactUtils.getContactCommonDetails(context, contactId, name);

                    contact.setPhones(ContactUtils.getContactPhones(context, contactId));
                    contact.setEmails(ContactUtils.getContactEmails(context, contactId));
                    contact.setAddresses(ContactUtils.getContactAddresses(context, contactId));
                    contact.setIms(ContactUtils.getContactIm(context, contactId));
                    contact.setWebsites(ContactUtils.getContactWebsites(context, contactId));
                    contacts.add(contact);
                    Log.i("Synchro", contact.toString());
                }
            }
            cursor.close();

            if (contacts.size() > 0) {
                Account currentAccount = AccountUtils.getCurrentOwnCloudAccount(context);
                ContactUtils.duplicateContactsForAccount(context, contacts, currentAccount);
            }
        } else {
            Log.e("Synchro", "Unable to load contacts for account " + contactAccount.getName());
        }
    }

    public static void duplicateContactsForAccount(final Context context, final List<Contact> contacts, final Account currentAccount) {
        Log.i("Synchro", "Synchronizing " + contacts.size() + " contacts int account " + currentAccount.name);

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        for (final Contact contact : contacts) {
            Log.i("Synchro", "Synchronize " + contact.getName() + " start");
            ops.add(ContentProviderOperation.newInsert(
                    RawContacts.CONTENT_URI)
                    .withValue(RawContacts.ACCOUNT_TYPE, currentAccount.type)
                    .withValue(RawContacts.ACCOUNT_NAME, currentAccount.name)
                    .build());

            // name
            Log.d("Synchro", "Synchronize " + contact.getName() + " name");
            ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                    .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                    .withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(StructuredName.DISPLAY_NAME, contact.getName())
                    .build());

            // birthday
            Log.d("Synchro", "Synchronize " + contact.getName() + " birth");
            if (contact.getBirthDate() != null) {
                ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                        .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                        .withValue(Data.MIMETYPE, Event.CONTENT_ITEM_TYPE)
                        .withValue(Event.TYPE, Event.TYPE_BIRTHDAY)
                        .withValue(Event.START_DATE, contact.getBirthDate())
                        .build());
            }

            // phones
            Log.d("Synchro", "Synchronize " + contact.getName() + " phones");
            if (contact.getPhones() != null && contact.getPhones().size() > 0) {
                for (final Phone phone : contact.getPhones()) {
                    ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                            .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                            .withValue(Data.MIMETYPE, CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                            .withValue(CommonDataKinds.Phone.NUMBER, phone.getPhone())
                            .withValue(CommonDataKinds.Phone.TYPE, phone.getType())
                            .build());
                }
            }

            // mails
            Log.d("Synchro", "Synchronize " + contact.getName() + " mails");
            if (contact.getEmails() != null && contact.getEmails().size() > 0) {
                for (final Email email : contact.getEmails()) {
                    ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                            .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                            .withValue(Data.MIMETYPE, CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                            .withValue(CommonDataKinds.Email.DATA, email.getEmail())
                            .withValue(CommonDataKinds.Email.TYPE, email.getType())
                            .build());
                }
            }

            // addresses
            Log.d("Synchro", "Synchronize " + contact.getName() + " addresses");
            if (contact.getAddresses() != null && contact.getAddresses().size() > 0) {
                for (final Address address : contact.getAddresses()) {
                    ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                            .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                            .withValue(Data.MIMETYPE, StructuredPostal.CONTENT_ITEM_TYPE)
                            .withValue(StructuredPostal.CITY, address.getCity())
                            .withValue(StructuredPostal.COUNTRY, address.getCountry())
                            .withValue(StructuredPostal.POSTCODE, address.getPostalCode())
                            .withValue(StructuredPostal.REGION, address.getRegion())
                            .withValue(StructuredPostal.STREET, address.getStreet())
                            .withValue(StructuredPostal.TYPE, address.getType())
                            .build());
                }
            }

            // ims
            Log.d("Synchro", "Synchronize " + contact.getName() + " ims");
            if (contact.getIms() != null && contact.getIms().size() > 0) {
                for (final Im im : contact.getIms()) {
                    ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                            .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                            .withValue(Data.MIMETYPE, CommonDataKinds.Im.CONTENT_ITEM_TYPE)
                            .withValue(CommonDataKinds.Im.DATA, im.getIm())
                            .withValue(CommonDataKinds.Im.PROTOCOL, im.getProtocol())
                            .withValue(CommonDataKinds.Im.TYPE, im.getType())
                            .build());
                }
            }

            // websites
            Log.d("Synchro", "Synchronize " + contact.getName() + " websites");
            if (contact.getWebsites() != null && contact.getWebsites().size() > 0) {
                for (final Website website : contact.getWebsites()) {
                    ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                            .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                            .withValue(Data.MIMETYPE, CommonDataKinds.Website.CONTENT_ITEM_TYPE)
                            .withValue(CommonDataKinds.Website.URL, website.getUrl())
                            .withValue(CommonDataKinds.Im.TYPE, website.getType())
                            .build());
                }
            }

            // company & title
            Log.d("Synchro", "Synchronize " + contact.getName() + " organization");
            if (contact.getCompany() != null || contact.getTitle() != null) {
                ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                        .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                        .withValue(Data.MIMETYPE, Organization.CONTENT_ITEM_TYPE)
                        .withValue(Organization.COMPANY, contact.getCompany())
                        .withValue(Organization.TITLE, contact.getTitle())
                        .build());
            }

            // nickname
            Log.d("Synchro", "Synchronize " + contact.getName() + " nickname");
            if (contact.getNickName() != null) {
                ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                        .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                        .withValue(Data.MIMETYPE, Nickname.CONTENT_ITEM_TYPE)
                        .withValue(Nickname.NAME, contact.getNickName())
                        .build());
            }

            // photo
            Log.d("Synchro", "Synchronize " + contact.getName() + " photo");
            final InputStream contactPhoto = getContactPhoto(context, contact.getId());
            if (contactPhoto != null) {
                try {
                    ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                            .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                            .withValue(Data.IS_SUPER_PRIMARY, 1)
                            .withValue(Data.MIMETYPE, Photo.CONTENT_ITEM_TYPE)
                            .withValue(Photo.PHOTO, IOUtils.toByteArray(contactPhoto))
                            .build());
                } catch (IOException e) {
                    Log.e("Synchro", "Unable to retrieve photo for contact " + contact.getName(), e);
                } finally {
                    IOUtils.closeQuietly(contactPhoto);
                }
            }

            // note
            Log.d("Synchro", "Synchronize " + contact.getName() + " note");
            if (contact.getNote() != null) {
                ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                        .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                        .withValue(Data.MIMETYPE, Note.CONTENT_ITEM_TYPE)
                        .withValue(Note.NOTE, contact.getNote())
                        /* At the end of this insert, the batch operation's thread will yield priority to other threads. */
                        .withYieldAllowed(true)
                        .build());
            }

            try {
                context.getContentResolver().applyBatch(AUTHORITY, ops);
                Log.i("Synchro", "Synchronize " + contact.getName() + " done");
            } catch (RemoteException | OperationApplicationException e) {
                Log.e("Synchro", "Unable to duplicate contact " + contact.getName(), e);
            }
        }
    }

    // ---------------------------------
    // PRIVATE
    // ---------------------------------
    private static List<Phone> getContactPhones(final Context context, final long contactId) {
        Cursor phoneCursor = context.getContentResolver().query(CommonDataKinds.Phone.CONTENT_URI,
                null,
                CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{String.valueOf(contactId)}, null);

        List<Phone> phones = new ArrayList<>();
        if (phoneCursor != null && phoneCursor.getCount() > 0) {
            while (phoneCursor.moveToNext()) {
                String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(CommonDataKinds.Phone.NUMBER));
                int type = phoneCursor.getInt(phoneCursor.getColumnIndex(CommonDataKinds.Phone.TYPE));
                Phone phone = new Phone(phoneNumber, type);
                phones.add(phone);
            }
            phoneCursor.close();
        }
        return phones;
    }

    private static List<Email> getContactEmails(final Context context, final long contactId) {
        Cursor emailCursor = context.getContentResolver().query(CommonDataKinds.Email.CONTENT_URI,
                null,
                CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{String.valueOf(contactId)}, null);

        List<Email> emails = new ArrayList<>();

        if (emailCursor != null) {
            while (emailCursor.moveToNext()) {
                String mail = emailCursor.getString(emailCursor.getColumnIndex(CommonDataKinds.Email.DATA));
                int type = emailCursor.getInt(emailCursor.getColumnIndex(CommonDataKinds.Email.TYPE));
                Email email = new Email(mail, type);
                emails.add(email);
            }
            emailCursor.close();
        }

        return emails;
    }

    private static List<Address> getContactAddresses(final Context context, long contactId) {
        Cursor addrCur = context.getContentResolver().query(StructuredPostal.CONTENT_URI,
                null, StructuredPostal.CONTACT_ID + " = ?", new String[]{String.valueOf(contactId)}, null);

        List<Address> addresses = new ArrayList<>();

        if (addrCur != null) {
            while (addrCur.moveToNext()) {
                String street = addrCur.getString(
                        addrCur.getColumnIndex(StructuredPostal.STREET));
                String city = addrCur.getString(
                        addrCur.getColumnIndex(StructuredPostal.CITY));
                String state = addrCur.getString(
                        addrCur.getColumnIndex(StructuredPostal.REGION));
                String postalCode = addrCur.getString(
                        addrCur.getColumnIndex(StructuredPostal.POSTCODE));
                String country = addrCur.getString(
                        addrCur.getColumnIndex(StructuredPostal.COUNTRY));
                String type = addrCur.getString(
                        addrCur.getColumnIndex(StructuredPostal.TYPE));

                Address address = new Address(street, city, state, postalCode, country, type);
                addresses.add(address);
            }
            addrCur.close();
        }

        return addresses;
    }

    private static List<Im> getContactIm(final Context context, long contactId) {
        Cursor imCur = context.getContentResolver().query(Data.CONTENT_URI,
                null, Data.CONTACT_ID + " = ? AND " + Data.MIMETYPE + " = ?", new String[]{String.valueOf(contactId), CommonDataKinds.Im.CONTENT_ITEM_TYPE}, null);

        List<Im> ims = new ArrayList<>();

        if (imCur != null) {
            while (imCur.moveToNext()) {
                String id = imCur.getString(imCur.getColumnIndex(CommonDataKinds.Im.DATA));
                int type = imCur.getInt(imCur.getColumnIndex(CommonDataKinds.Im.TYPE));
                int protocol = imCur.getInt(imCur.getColumnIndex(CommonDataKinds.Im.PROTOCOL));
                Im im = new Im(id, type, protocol);
                ims.add(im);
            }
            imCur.close();
        }

        return ims;
    }

    private static List<Website> getContactWebsites(final Context context, long contactId) {
        Cursor webCursor = context.getContentResolver().query(Data.CONTENT_URI,
                null, Data.CONTACT_ID + " = ? AND " + Data.MIMETYPE + " = ?", new String[]{String.valueOf(contactId), CommonDataKinds.Website.CONTENT_ITEM_TYPE}, null);

        List<Website> websites = new ArrayList<>();

        if (webCursor != null) {
            while (webCursor.moveToNext()) {
                String id = webCursor.getString(webCursor.getColumnIndex(CommonDataKinds.Website.URL));
                int type = webCursor.getInt(webCursor.getColumnIndex(CommonDataKinds.Website.TYPE));
                Website im = new Website(id, type);
                websites.add(im);
            }
            webCursor.close();
        }

        return websites;
    }

    private static Contact getContactCommonDetails(final Context context, long contactId, String name) {
        Contact contact = new Contact();
        contact.setId(contactId);
        contact.setName(name);

        Cursor companyCursor = context.getContentResolver().query(Data.CONTENT_URI,
                null, Data.CONTACT_ID + " = ? AND " + Data.MIMETYPE + " = ?", new String[]{String.valueOf(contactId), Organization.CONTENT_ITEM_TYPE}, null);
        if (companyCursor != null) {
            if (companyCursor.moveToFirst()) {
                contact.setCompany(companyCursor.getString(companyCursor.getColumnIndex(Organization.COMPANY)));
                contact.setTitle(companyCursor.getString(companyCursor.getColumnIndex(Organization.TITLE)));
            }
            companyCursor.close();
        }

        Cursor nickCursor = context.getContentResolver().query(Data.CONTENT_URI,
                null, Data.CONTACT_ID + " = ? AND " + Data.MIMETYPE + " = ?", new String[]{String.valueOf(contactId), Nickname.CONTENT_ITEM_TYPE}, null);
        if (nickCursor != null) {
            if (nickCursor.moveToFirst()) {
                contact.setNickName(nickCursor.getString(nickCursor.getColumnIndex(Nickname.NAME)));
            }
            nickCursor.close();
        }

        Cursor noteCursor = context.getContentResolver().query(Data.CONTENT_URI,
                null, Data.CONTACT_ID + " = ? AND " + Data.MIMETYPE + " = ?", new String[]{String.valueOf(contactId), Note.CONTENT_ITEM_TYPE}, null);
        if (noteCursor != null) {
            if (noteCursor.moveToFirst()) {
                contact.setNote(noteCursor.getString(noteCursor.getColumnIndex(Note.NOTE)));
            }
            noteCursor.close();
        }

        Cursor birthCursor = context.getContentResolver().query(Data.CONTENT_URI,
                null,
                Data.CONTACT_ID + " = ? AND "
                        + Data.MIMETYPE + " = ? AND "
                        + Event.TYPE + " = " + Event.TYPE_BIRTHDAY,
                new String[]{String.valueOf(contactId), Event.CONTENT_ITEM_TYPE},
                null);
        if (birthCursor != null) {
            if (birthCursor.moveToFirst()) {
                contact.setBirthDate(birthCursor.getString(birthCursor.getColumnIndex(Event.START_DATE)));
            }
            birthCursor.close();
        }

        return contact;
    }

    private static InputStream getContactPhoto(Context context, long contactId) {
        Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[]{Contacts.Photo.PHOTO}, null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            if (cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(0);
                if (data != null) {
                    return new ByteArrayInputStream(data);
                }
            }
        } finally {
            cursor.close();
        }
        return null;
    }
}

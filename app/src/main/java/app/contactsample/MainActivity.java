package app.contactsample;

import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import app.contactsample.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private ContactAdapter adapter;
    private List<Contact> mContacts;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        binding.list.setLayoutManager(new LinearLayoutManager(this));
        mContacts = new ArrayList<>();
        adapter = new ContactAdapter(mContacts);
        binding.list.setAdapter(adapter);
        binding.stickyIndex.bindRecyclerView(binding.list);
        binding.fastScroller.bindRecyclerView(binding.list);
        LoaderManager.getInstance(this).initLoader(1, null, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        String[] projection = new String[]{ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
                ContactsContract.Contacts.LOOKUP_KEY,
                ContactsContract.Contacts.PHOTO_THUMBNAIL_URI};
        return new CursorLoader(this, ContactsContract.Contacts.CONTENT_URI, projection, ContactsContract.Contacts.IN_VISIBLE_GROUP + "= '1' AND " + ContactsContract.Contacts.HAS_PHONE_NUMBER, null, "LOWER (" + ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + ") ASC");
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        mContacts.clear();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                mContacts.add(map(cursor));
            } while (cursor.moveToNext());
        }
        adapter.notifyDataSetChanged();
        binding.stickyIndex.refresh(convertToIndexList(mContacts));
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    private Contact map(Cursor cursor) {
        return new Contact(cursor.getString(cursor.getColumnIndex(ContactsContract.Profile._ID)),
                cursor.getString(cursor.getColumnIndex(ContactsContract.Profile.DISPLAY_NAME_PRIMARY)),
                mapThumbnail(cursor)
        );
    }

    private Uri mapThumbnail(Cursor cursor) {
        int thumbnailIndex = cursor.getColumnIndex(ContactsContract.Profile.PHOTO_THUMBNAIL_URI);
        String thumbnail = cursor.getString(thumbnailIndex);
        if (thumbnail == null || thumbnail.isEmpty()) return null;
        else return Uri.parse(thumbnail);
    }

    private char[] convertToIndexList(List<Contact> contactsList) {
        List<Character> list = new ArrayList<>();
        for (Contact result : contactsList) {
            if (result == null)
                continue;
            if (!TextUtils.isEmpty(result.number))
                list.add(result.number.toUpperCase().charAt(0));
        }
//        Set<Character> set = new HashSet<>(list);
//        list.clear();
//        list.addAll(set);
        char[] chars = new char[list.size()];
        int i = 0;
        for (Character characters : list)
            chars[i++] = characters;
        return chars;
    }
}

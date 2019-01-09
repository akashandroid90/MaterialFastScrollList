package app.contactsample;

import android.net.Uri;

public class Contact {
    public String id, url, number;

    public Contact(String id, String number, Uri url) {
        this.id = id;
        if (url != null)
            this.url = url.toString();
        this.number = number;
    }

}

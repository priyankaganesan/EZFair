package edu.gatech.rts.ezfair.item;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by Priyanka on 11/2/2014.
 */
public class JoinedEntryItem implements Item {

    public final String company_name;
    public final String my_token;
    public final String count_ahead;

    public JoinedEntryItem(String c_name, String my_tkn, String count_ah) {
        this.company_name = c_name;
        this.my_token = "My Token: " +my_tkn;
        this.count_ahead = "People Ahead: " + count_ah;
    }

    @Override
    public boolean isSection() {
        return false;
    }

}
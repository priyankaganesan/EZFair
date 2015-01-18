package edu.gatech.rts.ezfair.item;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by Nithya on 11/29/2014.
 */
public class OtherEntryItem implements Item {
    public final String company_name;
    public final String count_ahead;
    public OtherEntryItem(String company_name, String subt) {
        this.company_name = company_name;
        this.count_ahead = "Queue Length: " + subt;
    }
    public boolean isSection() {
        return false;
    }


}

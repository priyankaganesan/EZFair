package edu.gatech.rts.ezfair.item;

/**
 * Created by Priyanka on 11/2/2014.
 */
public class SectionItem implements Item {

    private final String title;

    public SectionItem(String title) {
        this.title = title;
    }

    public String getTitle(){
        return title;
    }

    @Override
    public boolean isSection() {
        return true;
    }

}
package com.owncloud.android.ui.contactsetup;

/**
 * Created by Med-Amine on 6/25/2015.
 */
public class ImportListItem {

    private String itemTitle;

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public ImportListItem(String title){
        this.itemTitle = title;
    }
}

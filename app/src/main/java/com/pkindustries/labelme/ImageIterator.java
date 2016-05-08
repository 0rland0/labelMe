package com.pkindustries.labelme;

/**
 * Created by Orlando on 4/29/16.
 */
public class ImageIterator {

    private int[] imageResourceIds;
    private int counter = 0;

    public ImageIterator(int[] imageResourceIds) {
        this.imageResourceIds = imageResourceIds;
    }

    public int next() {
        if(counter < imageResourceIds.length - 1) {
            counter++;
            return imageResourceIds[counter];
        } else {
            return -1;
        }
    }

}

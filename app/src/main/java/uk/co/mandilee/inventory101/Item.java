package uk.co.mandilee.inventory101;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

/**
 * Created by manda on 2017-05-25.
 */

public class Item {

    private long mId;
    private String mName;
    private int mStock;
    private float mPrice;
    private String mDescription;
    private String mImagePath;

    public long getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }


    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }


    public int getStock() {
        return mStock;
    }

    public void setStock(int stock) {
        mStock = stock;
    }


    public float getPrice() {
        return mPrice;
    }

    public void setPrice(float price) {
        mPrice = price;
    }


    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }


    public boolean hasImage() {
        return getImagePath() != null && !getImagePath().isEmpty();
    }

    public String getImagePath() {
        return mImagePath;
    }

    public void setImagePath(String imagePath) {
        mImagePath = imagePath;
    }


    /**
     * Get a thumbnail of this profile's picture, or a default image if the profile doesn't have a
     * Image.
     *
     * @return Thumbnail of the profile.
     */
    public Drawable getThumbnail(Context context) {

        return getScaledImage(context, 128, 128);
    }

    /**
     * Get this profile's picture, or a default image if the profile doesn't have a Image.
     *
     * @return Image of the profile.
     */
    public Drawable getImage(Context context) {

        return getScaledImage(context, 512, 512);
    }

    /**
     * Get a scaled version of this profile's Image, or a default image if the profile doesn't have
     * a Image.
     *
     * @return Image of the profile.
     */
    private Drawable getScaledImage(Context context, int reqWidth, int reqHeight) {

        // If profile has a Image.
        if (hasImage()) {

            // Decode the input stream into a bitmap.
            Bitmap bitmap = FileUtils.getResizedBitmap(getImagePath(), reqWidth, reqHeight);

            // If was successfully created.
            if (bitmap != null) {

                // Return a drawable representation of the bitmap.
                return new BitmapDrawable(context.getResources(), bitmap);
            }
        }

        // Return the default image drawable.
        return ContextCompat.getDrawable(context, R.drawable.no_image);
    }

}
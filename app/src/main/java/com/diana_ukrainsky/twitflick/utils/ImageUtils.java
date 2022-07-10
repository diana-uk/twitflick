package com.diana_ukrainsky.twitflick.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class ImageUtils {

    /**
     * Using library picasso which takes url of image and setting an imageView
     * @param imageView To which ImageView
     * @param imageUrl From which url
     */
    public static void setImageUI(ImageView imageView, String imageUrl) {
        Picasso.get ()
                .load (imageUrl)
                .resize (130, 160)
                .into (imageView);
    }

    /**
     * Download directly from StorageReference using Glide
     *  (See MyAppGlideModule for Loader registration)
     * @param context Which context to set ImageView in.
     * @param downloadReference From which url from Firebase Storage
     * @param imageView To which ImageView we want to set in.
     */
    public static void setImageUI(Context context, Uri uri, ImageView imageView) {
        Glide.with(context /* context */)
                .load(uri)
                .into(imageView);
    }

    /**
     *  Set ImageBitmap from Uri.
     *   Useful when uploading a photo from internal storage of phone
     *   for example : from gallery.
     * @param context Needed to know the  activity to present the image.
     * @param selectedImageUri From which Uri of the selected image.
     * @return The selected Image Bitmap.
     */
    public static Bitmap setImageBitmapFromUriUI(Context context, Uri selectedImageUri) {
        Bitmap selectedImageBitmap = null;
        try {
                    selectedImageBitmap = MediaStore.Images.Media.getBitmap(
                    context.getContentResolver(),
                    selectedImageUri);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return  selectedImageBitmap;
    }

}

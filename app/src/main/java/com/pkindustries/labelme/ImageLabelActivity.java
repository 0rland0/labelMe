package com.pkindustries.labelme;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;
import java.io.IOException;

public class ImageLabelActivity extends AppCompatActivity {

    private static final String TAG = "ImageLabelActivity";
    private ImageIterator images = null;
    private ImageView imageView = null;
    private Activity mainView = null;
    private String filePath = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_label);

        mainView = this;
        imageView = (ImageView) findViewById(R.id.main_image);
        Button labelOneButton = (Button) findViewById(R.id.btn_label_one);
        Button labelTwoButton = (Button) findViewById(R.id.btn_label_two);

        images = createImageIterator();

        labelOneButton.setOnClickListener(new LabelClickListener(this));
        labelTwoButton.setOnClickListener(new LabelClickListener(this));

        final ImageLabelActivity imageLabelActivity = this;
        ImageDownloader imageDownloader = new ImageDownloader(this);
        imageDownloader.setDelegate(new ImageDownloaderDelegate() {
            @Override
            public void newImageDownloaded(String pathToImage) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(imageLabelActivity, "img downloaded", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        boolean isOnline = NetworkStateChecker.isOnline(this);
        if(isOnline == true) {
            imageDownloader.startDownloadAsync();
        } else {
            Toast.makeText(imageLabelActivity, R.string.no_internet_message, Toast.LENGTH_LONG).show();
            Log.e(TAG, "Image download could not be started because there is no internet connection");
        }


    }

    private ImageIterator createImageIterator() {
        int imageResourceIds[] = {R.drawable.img_10, R.drawable.img_11, R.drawable.img_12,
                R.drawable.img_13, R.drawable.img_14, R.drawable.img_15, R.drawable.img_16,
                R.drawable.img_17, R.drawable.img_18};
        return new ImageIterator(imageResourceIds);
    }


    public void changeImageFromAsyncTask() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Bitmap myBitmap = BitmapFactory.decodeFile(filePath);
                imageView.setImageBitmap(myBitmap);
                Toast.makeText(mainView, "Successfully changed to donwloaded image!", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void showNextImage() {

        int newResourceId = images.next()
                ;
        if (newResourceId != -1) {
            imageView.setImageResource(newResourceId);
        } else {
            Toast.makeText(mainView, "There are no more images left!", Toast.LENGTH_SHORT).show();
        }
    }



    class LabelClickListener implements View.OnClickListener {

        private ImageLabelActivity imageLabelActivity = null;

        public LabelClickListener(ImageLabelActivity imageLabelActivity) {
            this.imageLabelActivity = imageLabelActivity;
        }

        @Override
        public void onClick(View v) {
            imageLabelActivity.showNextImage();
        }
    }

}



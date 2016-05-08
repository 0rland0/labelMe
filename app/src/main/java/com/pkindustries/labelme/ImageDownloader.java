package com.pkindustries.labelme;


import android.content.Context;
import android.content.ContextWrapper;
import android.os.AsyncTask;
import android.util.Log;

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

interface  ImageDownloaderDelegate {
    void newImageDownloaded(String pathToImage);
}

/**
 * Created by Orlando on 5/5/16.
 */
public class ImageDownloader {
    private ImageDownloaderDelegate delegate = null;
    private Context applicationContext = null;
    private String pathToDownloadedImage = null;

    public ImageDownloader(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Sets a delegate which is notified if an image was downloaded
     * @param delegate
     */
    public void setDelegate(ImageDownloaderDelegate delegate) {
        this.delegate = delegate;
    }


    /**
     * Starts asynchronously an image download. If the download is finished
     * the ImageDownloader delegate (if set) will be notified.
     */
    public void startDownloadAsync() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                TransferUtility transferUtility = createAmazonTransferUtility();
                File imageFile = createEmptyImageFileWithName("downloadedFile.jpg");
                if(imageFile != null && imageFile.exists()) {
                    TransferObserver observer = transferUtility.download(
                            "dogsandcats",     /* The bucket to download from */
                            "134.jpg",    /* The key for the object to download */
                            imageFile        /* The file to download the object to */
                    );
                    observer.setTransferListener(new DownloadListener());
                    pathToDownloadedImage = imageFile.getAbsolutePath();
                }
                return null;
            }
        }.execute();
    }

    private void imageDownloadCompleted() {
        if(delegate != null) {
            delegate.newImageDownloaded(pathToDownloadedImage);
        }
    }

    /**
     * Creates and returns an amazon transfer utility object
     * which can be used to retrieve objects e.g. from the S3
     * storage
     * @return
     */
    private TransferUtility createAmazonTransferUtility() {
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                applicationContext,
                "us-east-1:70a550c4-7847-4233-9053-74bdbe469ee6", // Identity Pool ID
                Regions.US_EAST_1 // Region
        );

        AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
        TransferUtility transferUtility = new TransferUtility(s3, applicationContext);
        return transferUtility;
    }

    private File createEmptyImageFileWithName(String fileName) {
        ContextWrapper cw = new ContextWrapper(applicationContext);
        File directory = cw.getDir("themes", Context.MODE_WORLD_WRITEABLE);
        File imageFile = new File(directory.getAbsolutePath() + File.separator +  fileName);
        try
        {
            imageFile.createNewFile();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return imageFile;
    }

    /*
     * A TransferListener class that can listen to a download task and be
     * notified when the status changes.
     */
    private class DownloadListener implements TransferListener {

        String TAG = "DownloadListener";
        // Simply updates the list when notified.
        @Override
        public void onError(int id, Exception e) {
            Log.e(TAG, "onError: " + id, e);
            int i = 5;
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            Log.d(TAG, String.format("onProgressChanged: %d, total: %d, current: %d",
                    id, bytesTotal, bytesCurrent));
//            updateList();
        }

        @Override
        public void onStateChanged(int id, TransferState state) {
//            Log.d(TAG, "onStateChanged: " + id + ", " + state);
            if (state == TransferState.COMPLETED) {
                imageDownloadCompleted();
            } else if(state == TransferState.FAILED) {
                Log.e(TAG, "Dowloading image failed");
            } else {
                Log.d(TAG, "State changed to " + state.name());
            }
        }


    }
}

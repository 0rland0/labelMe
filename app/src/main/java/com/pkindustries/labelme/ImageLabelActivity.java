package com.pkindustries.labelme;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class ImageLabelActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_label);


        final ImageView imageView = (ImageView) findViewById(R.id.main_image);


        int imageResourceIds[] = { R.drawable.img_10, R.drawable.img_11, R.drawable.img_12,
                R.drawable.img_13, R.drawable.img_14, R.drawable.img_15, R.drawable.img_16,
                R.drawable.img_17, R.drawable.img_18 };
        final ImageIterator images = new ImageIterator(imageResourceIds);

        Button labelOneButton =  (Button) findViewById(R.id.btn_label_one);
        final Activity mainView = this;

        labelOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newResourceId = images.next();
                if(newResourceId != -1) {
                    imageView.setImageResource(newResourceId);
                } else {
                    Toast.makeText(mainView, "There are no more images left!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

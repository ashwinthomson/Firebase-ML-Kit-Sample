package com.app.objectdetection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;

import java.util.List;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runImageLabelingOnDevice(BitmapFactory.decodeResource(MainActivity.this.getResources(),
                        R.drawable.bg_log));
            }
        });
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runImageLabelingCloud(BitmapFactory.decodeResource(MainActivity.this.getResources(),
                        R.drawable.bg_log));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void runImageLabelingOnDevice(Bitmap bitmap){
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        Task<List<FirebaseVisionImageLabel>> labelDetector = FirebaseVision.getInstance().getOnDeviceImageLabeler()
                .processImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionImageLabel> firebaseVisionImageLabels) {
                        processImageLabelingFromDevice(firebaseVisionImageLabels);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("LABELFAILURE",  e.getMessage());
                    }
                });
    }

    private void processImageLabelingFromDevice(List<FirebaseVisionImageLabel> labels){
        StringBuilder labelsSb = new StringBuilder();
        for (FirebaseVisionImageLabel label : labels){
            Log.d("IMAGELABELING",label.getText());
            labelsSb.append(label.getText()+ ", ");
        }


        new AlertDialog.Builder(this)
                .setTitle("Labels from device")
                .setMessage(labelsSb.toString())
                .create()
                .show();
    }

    private void runImageLabelingCloud(Bitmap bitmap) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

        FirebaseVision.getInstance().getCloudImageLabeler().processImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
            @Override
            public void onSuccess(List<FirebaseVisionImageLabel> firebaseVisionImageLabels) {
                processImageLabelingFromCloud(firebaseVisionImageLabels);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("LABELFAILURE",e.getMessage());
            }
        });
    }

    private void processImageLabelingFromCloud(List<FirebaseVisionImageLabel> labels){
        StringBuilder labelsSb = new StringBuilder();
        for (FirebaseVisionImageLabel label : labels){
            Log.d("IMAGELABELING",label.getText());
            labelsSb.append(label.getText()+ ", ");
        }

        new AlertDialog.Builder(this)
                .setTitle("Labels from cloud")
                .setMessage(labelsSb.toString())
                .create()
                .show();
    }

}

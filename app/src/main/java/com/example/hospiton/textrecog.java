package com.example.hospiton;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

public class textrecog extends AppCompatActivity {

    private Button btncamera;
    private ImageView take;
    private Bitmap bitmap;
    private TextView recognize;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textrecog);

        btncamera=(Button)findViewById(R.id.btncamera);
        take=(ImageView)findViewById(R.id.picture);
        recognize=(TextView)findViewById(R.id.display_text);


        btncamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         bitmap=(Bitmap)data.getExtras().get("data");
         take.setImageBitmap(bitmap);
         extracttextfromimage();

    }

    private void extracttextfromimage() {
        TextRecognizer textRecognizer=new TextRecognizer.Builder(textrecog.this).build();

        if(!textRecognizer.isOperational())
        {
            Toast.makeText(this,"Could not get the text",Toast.LENGTH_SHORT).show();
        }
        else {
            Log.d("textrecog",bitmap.toString());
            Frame frame=new Frame.Builder().setBitmap(bitmap).build();

            SparseArray<TextBlock>items=textRecognizer.detect(frame);

            StringBuilder sb=new StringBuilder();

            for(int i=0;i<items.size();i++)
            {
                TextBlock myitems=items.valueAt(i);
                sb.append(myitems.getValue());
                sb.append("\n");
            }
            recognize.setText(sb.toString());
        }
    }
}

package com.sctcs.jacobin.moustachios;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.PermissionChecker;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity
{
    private final int REQUEST_ID=3321;
    private static  final int PHOTO_REQUEST_ID=331;


     private ImageView image_holder;
     private ImageButton button;
     private FloatingActionButton share_button;
     private FloatingActionButton save_button;
    private String currentPhotoPath;

    private Uri current_file_uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image_holder=(ImageView) findViewById(R.id.imageView);
        button=(ImageButton) findViewById(R.id.imageButton);
        share_button=(FloatingActionButton) findViewById(R.id.share_button);
        save_button=(FloatingActionButton) findViewById(R.id.save_button);

        Glide.with(this).load(R.drawable.moustache).into(image_holder);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_permission();
            }
        });

    }

    private void  check_permission()
    {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=PackageManager.PERMISSION_GRANTED )
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                   REQUEST_ID);
        }

        else
        {
            take_photo();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch(requestCode)
        {
            case REQUEST_ID:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {

                    take_photo();
                }
                else
                {
                    Toast.makeText(this,"Please enable the Permission",Toast.LENGTH_SHORT).show();

                }
                break;
        }

    }

    public void take_photo()
    {
        Intent photo_intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(photo_intent.resolveActivity(getPackageManager())!=null)
        {   File  temp_file=null;
            try {
                temp_file=create_image_file();
            } catch (IOException e) {
                e.printStackTrace();
            }
             if(temp_file!=null)
             {

                 Uri photo_uri= FileProvider.getUriForFile(this,"com.sctcs.jacobin.moustachios",temp_file);
                 current_file_uri=photo_uri;
                 photo_intent.putExtra(MediaStore.EXTRA_OUTPUT,photo_uri);
                 startActivityForResult(photo_intent, PHOTO_REQUEST_ID);
             }

        }

    }


    private File create_image_file() throws IOException
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String temp_file_name="JPEG"+timeStamp+"_";
        File StorageDir=getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image=File.createTempFile(temp_file_name,".jpg",StorageDir);
        currentPhotoPath=image.getAbsolutePath();
        return image;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
           if(requestCode==PHOTO_REQUEST_ID && resultCode==RESULT_OK)
           {
             LoadBitmap(currentPhotoPath);
           }
    }



  /*  private void scale_bitmap(String  path)
    {
        int target_width=image_holder.getMaxWidth();
        int target_height=image_holder.getMaxHeight();
        BitmapFactory.Options moptions= new BitmapFactory.Options();
        moptions.inJustDecodeBounds=true;
        BitmapFactory.decodeFile(path,moptions);

        int photo_width=moptions.outWidth;
        int photo_height=moptions.outHeight;


        int scaleFactor=Math.max(photo_width/target_width,photo_height/target_height);
        moptions.inJustDecodeBounds=false;
        moptions.inSampleSize=scaleFactor;

        Bitmap bitmap=BitmapFactory.decodeFile(path,moptions);
        image_holder.setImageBitmap(bitmap);

    }*/

  private void LoadBitmap(String Path)
  {
      int target_width=image_holder.getMaxWidth();
      int target_height=image_holder.getMaxHeight();
     Glide.with(this).load(current_file_uri).override(target_width,target_height).into(image_holder);
  }
}


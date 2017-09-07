package com.sctcs.jacobin.moustachios;

import android.Manifest;
import android.content.Context;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity
{
    private final int REQUEST_ID=3321;
    private static  final int PHOTO_REQUEST_ID=331;


     private ImageView image_holder;
     private ImageButton button;
     private String currentPhotoPath=null;
     private ProgressBar progressBar;

    private Uri current_file_uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image_holder=(ImageView) findViewById(R.id.imageView);
        button=(ImageButton) findViewById(R.id.imageButton);
        progressBar=(ProgressBar) findViewById(R.id.progressBar) ;

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

             LoadBitmap();
           }
    }



  private void LoadBitmap()
  {
      int target_width=image_holder.getMaxWidth();
      int target_height=image_holder.getMaxHeight();
      progressBar.setVisibility(View.VISIBLE);

      Glide.with(this).load(current_file_uri).listener(new RequestListener<Uri, GlideDrawable>() {
          @Override
          public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
              return false;
          }

          @Override
          public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
              progressBar.setVisibility(View.GONE);
              return false;
          }
      }).override(target_width,target_height).into(image_holder);
  }

    public void deleteFile(Context context, String Photopath)
    {   if(Photopath!=null) {
        File imageFile = new File(Photopath);
        boolean deleted = imageFile.delete();

       }

    }

    @Override
    protected void onStop() {
        super.onStop();
        deleteFile(this,currentPhotoPath);
    }
}



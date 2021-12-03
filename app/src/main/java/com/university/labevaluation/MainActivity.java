package com.university.labevaluation;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void pickImage(){
        Intent intent = new Intent();
        intent.setType("Image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Image"),101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 101 && resultCode == RESULT_OK && data!=null){
            Uri uri = data.getData();

            String path = getPath(getApplicationContext(),uri);
            String name = getFileName(uri);

           try {
               insertImage(name,path);
           }
           catch (Exception e){

           }




        }


    }

    private void insertImage(String name, String path) throws IOException {

        FileOutputStream fos = openFileOutput(name,MODE_APPEND);
        File file = new File(path);
        byte[] bytes = getBytesFromFile(file);
      fos.write(bytes);
      fos.close();

        Toast.makeText(getApplicationContext(),"File saved in :"+ getFilesDir() + "/"+name,Toast.LENGTH_SHORT).show();


    }

    private byte[] getBytesFromFile(File file) throws IOException {
    byte[] data = FileUtils.readFileToByteArray(file);
    return data;

    }

    private String getFileName(Uri uri) {
        String result = null;
        if(uri.getScheme().equals("content")){
            Cursor cursor = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                cursor = getContentResolver().query(uri,null,null,null);
            }

            try{
                if(cursor!=null && cursor.moveToFirst()){
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
            finally {
                cursor.close();
            }
        }
        if(result== null){
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if(cut!=-1){
                result = result.substring(cut + 1);

            }
        }
        return result;
    }

    private String getPath(Context context, Uri uri){
        String [] media= {MediaStore.Images.Media.DATA};

        Cursor cursor = context.getContentResolver().query(uri,media,null,null,null);
        if(cursor !=null){
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return null;

    }
}
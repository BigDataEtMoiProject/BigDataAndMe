package ca.uqac.bigdataetmoi.menu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import ca.uqac.bigdataetmoi.R;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final SharedPreferences mPrefs = getSharedPreferences("profile", 0);
        final ImageView avatar = findViewById(R.id.avatar);
        final EditText name = findViewById(R.id.name);
        final EditText lastname = findViewById(R.id.lastname);
        final EditText email = findViewById(R.id.email);
        final EditText password = findViewById(R.id.password);
        Button validate = findViewById(R.id.validate);

        //RETRIEVE PROFILE VALUES
        String avatarStr = mPrefs.getString("avatar", "");
        String nameStr = mPrefs.getString("name", "");
        String lastnameStr = mPrefs.getString("lastname", "");
        String emailStr = mPrefs.getString("email", "");
        String passwordStr = mPrefs.getString("password", "");

        //IF VALUE EXISTS, PUT IT IN TEXTFIELD
        if(avatarStr != "" && avatarStr != null){
            byte[] b = Base64.decode(avatarStr, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            avatar.setImageBitmap(getCircularBitmap(bitmap));
        }
        if(nameStr != "" && nameStr != null){
            name.setText(nameStr);
        }
        if(lastnameStr != "" && lastnameStr != null){
            lastname.setText(lastnameStr);
        }
        if(emailStr != "" && emailStr != null){
            email.setText(emailStr);
        }
        if(passwordStr != "" && passwordStr != null){
            password.setText(passwordStr);
        }

        avatar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1).setCropShape(CropImageView.CropShape.OVAL).setRequestedSize(300, 300).start(ProfileActivity.this);
            }
        });

        validate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //STORE PROFILE VALUES IF THEY EXIST
                SharedPreferences.Editor mEditor = mPrefs.edit();

                String nameStr2 = name.getText().toString();
                String lastnameStr2 = lastname.getText().toString();
                String emailStr2 = email.getText().toString();
                String passwordStr2 = password.getText().toString();

                if(nameStr2 != "" && nameStr2 != null){
                    mEditor.putString("name", nameStr2).commit();
                }
                if(lastnameStr2 != "" && lastnameStr2 != null){
                    mEditor.putString("lastname", lastnameStr2).commit();
                }
                if(emailStr2 != "" && emailStr2 != null){
                    mEditor.putString("email", emailStr2).commit();
                }
                if(passwordStr2 != "" && passwordStr2 != null){
                    mEditor.putString("password",passwordStr2).commit();
                }

                onBackPressed();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Back arrow clicked
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri selectedImage = result.getUri();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

                    //CONVERT IMAGE TO STRING TO BE ABLE TO STOCK IT
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] b = baos.toByteArray();
                    String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

                    //STOCK THE STRING IMAGE
                    final SharedPreferences mPrefs = getSharedPreferences("profile", 0);
                    SharedPreferences.Editor mEditor = mPrefs.edit();
                    mEditor.putString("avatar", encodedImage).commit();

                    //SET IMAGE TO AVATAR
                    ImageView avatar = findViewById(R.id.avatar);
                    avatar.setImageBitmap(getCircularBitmap(bitmap));
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public static Bitmap getCircularBitmap(Bitmap bitmap) {
        // MID SQUARE
        if (bitmap.getWidth() >= bitmap.getHeight()){
            bitmap = Bitmap.createBitmap(bitmap, bitmap.getWidth()/2 - bitmap.getHeight()/2, 0, bitmap.getHeight(), bitmap.getHeight());
        }
        else{
            bitmap = Bitmap.createBitmap(bitmap, 0, bitmap.getHeight()/2 - bitmap.getWidth()/2, bitmap.getWidth(), bitmap.getWidth());
        }

        //ROUND THE MID SQUARE
        Bitmap output;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        }
        else {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        float r;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            r = bitmap.getHeight() / 2;
        }
        else {
            r = bitmap.getWidth() / 2;
        }

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }
}

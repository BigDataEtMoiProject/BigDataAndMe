package ca.uqac.bigdataetmoi.workers;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Base64;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import ca.uqac.bigdataetmoi.events.OnPhotoUploadedEvent;
import ca.uqac.bigdataetmoi.services.HttpClient;
import ca.uqac.bigdataetmoi.services.UserService;
import ca.uqac.bigdataetmoi.dto.PhotoDto;
import ca.uqac.bigdataetmoi.models.Photo;
import ca.uqac.bigdataetmoi.models.User;
import ca.uqac.bigdataetmoi.repositories.UserRepository;
import retrofit2.Call;
import retrofit2.Response;

public class PhotoWorker extends Worker {

    private Context appContext;

    public PhotoWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.appContext = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        ArrayList<String> photosPathList = getGalleryPhotosPath();
        ArrayList<String> userPhotoNameList = getUserPhotoNameList();

        for (String photoPath : photosPathList) {
            int lastSlashIndex = photoPath.lastIndexOf("/");
            String photoName = photoPath.substring(lastSlashIndex + 1);

            if (!userPhotoNameList.contains(photoName)) {
                String base64photo = convertPhotoPathToBase64(photoPath);
                String currentTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new java.util.Date());

                PhotoDto photoDto = new PhotoDto(base64photo, photoName, currentTime, "undefined");
                Call<User> photoCall = new HttpClient<UserService>(appContext).create(UserService.class).sendPhoto(photoDto);

                try {
                    Response<User> res = photoCall.execute();
                    if (res.isSuccessful() && res.body() != null) {
                        EventBus.getDefault().post(new OnPhotoUploadedEvent(res.body()));
                    } else if (res.code() == 401) {
                        return Result.failure();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return Result.success();
    }

    private ArrayList<String> getGalleryPhotosPath() {
        Uri uri;
        Cursor cursor;
        int column_index_data;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        String absolutePathOfImage;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        if (appContext != null) {
            cursor = appContext.getContentResolver().query(uri, projection, null,
                    null, null);

            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

            while (cursor.moveToNext()) {
                absolutePathOfImage = cursor.getString(column_index_data);

                listOfAllImages.add(absolutePathOfImage);
            }

            cursor.close();
        }

        return listOfAllImages;
    }

    private ArrayList<String> getUserPhotoNameList() {
        try {
            Response<User> response = UserRepository.getUserFromApi(appContext).execute();

            if (response.isSuccessful() && response.body() != null) {
                ArrayList<String> photoNameList = new ArrayList<>();

                for (Photo photo : response.body().photoList) {
                    if (photo.name != null) {
                        photoNameList.add(photo.name);
                    }
                }

                return photoNameList;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    private String convertPhotoPathToBase64(String imagePath) {
        Bitmap bm = BitmapFactory.decodeFile(imagePath);
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 30, bOut);
        return Base64.encodeToString(bOut.toByteArray(), Base64.DEFAULT);
    }
}

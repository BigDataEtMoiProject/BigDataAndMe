package ca.uqac.bigdataetmoi.service;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ca.uqac.bigdataetmoi.database.DatabaseManager;

/**
 * This class is a thread of the Mic
 */

public class MicroThread  extends Thread implements Runnable, MediaRecorder.OnInfoListener {

    DatabaseManager dbManager;

    Context mContext;

    private MediaRecorder mRecorder;

    private File mOutputFile;

    boolean continueRecording;

    private long mStartTime;

    private int[] amplitudes = new int[100];

    // Requires a little of noise by the user to trigger, background noise may trigger it
    public static final int AMPLITUDE_DIFF_LOW = 10000;

    public static final int AMPLITUDE_DIFF_MED = 18000;

    // Requires a lot of noise by the user to trigger. background noise isn't likely to be this loud
    public static final int AMPLITUDE_DIFF_HIGH = 25000;

    private static final int DEFAULT_AMPLITUDE_DIFF = AMPLITUDE_DIFF_MED;

    // Setting maximum file size to be recorded
    private long Audio_MAX_FILE_SIZE = 500000;//500ko


    public MicroThread(Context context)
    {
        mContext = context;

        dbManager = DatabaseManager.getInstance();

        Log.v("MICThread", "MIC thread has been created");
    }


    @Override
    public void run()
    {
        boolean RECORD_AUDIO_PERMISSION = (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED);
        boolean WRITE_EXTERNAL_STORAGE_PERMISSION = (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);

        if(RECORD_AUDIO_PERMISSION && WRITE_EXTERNAL_STORAGE_PERMISSION){
            Log.w("MICService", "MIC service has started");
            startRecording();
        }
      //  else
         //   Toast.makeText(mContext, "Permission to use MIC or STORAGE WRITING : denied", Toast.LENGTH_SHORT).show();

    }


    private File getOutputFile() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.US);

        return new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath().toString()
                + "/Voice Recorder/RECORDING_"
                + dateFormat.format(new Date())
                + ".3gpp");
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setOnInfoListener(this);
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setMaxFileSize(Audio_MAX_FILE_SIZE);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mRecorder.setAudioEncodingBitRate(64000);
        mRecorder.setAudioSamplingRate(16000);
        mOutputFile = getOutputFile();
        mOutputFile.getParentFile().mkdirs();
        mRecorder.setOutputFile(mOutputFile.getAbsolutePath());
        continueRecording=true;

        try {
            mRecorder.prepare();
            mRecorder.start();
            Log.w("MICService", "MIC started recording");
            Log.w("MICService","MaxAmplitude " +mRecorder.getMaxAmplitude());

            int startAmplitude = mRecorder.getMaxAmplitude();
            Log.d("MICService", "starting amplitude: " + startAmplitude);
            mStartTime = SystemClock.elapsedRealtime();
            /*do
            {
                Log.d("MICService", "waiting while recording...");

                int finishAmplitude = mRecorder.getMaxAmplitude();

                int ampDifference = finishAmplitude - startAmplitude;
                if (ampDifference >= amplitudeThreshold)
                {
                    Log.d("MICService", "heard sound !");

                }
                Log.d("MICService", "finishing amplitude: " + finishAmplitude + " diff: "
                        + ampDifference);

            } while (mOutputFile.length()<Audio_MAX_FILE_SIZE-20);*/



        } catch (IOException e) {
        }
    }

    @Override
    public void onInfo(MediaRecorder mr, int what, int extra)
    {
        //check whether file size has reached to 1MB to stop recording
        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED) {
            Log.w("MICService", "New audio stored");
            SaveAudio(true);
        }
    }


    protected void SaveAudio(boolean saveFile) {
        if(mRecorder!=null) {
            mRecorder.stop();
            //upload();
            mRecorder.reset();
            mStartTime = 0;

            startRecording();
            Log.w("MICService", "Recording again..");
        }
        if (!saveFile && mOutputFile != null) {
            mOutputFile.delete();
        }


    }


    /*public void upload() {


        //progress.setMessage("Uploading Audio ...");
        //progress.show();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.US);
        StorageReference filePath = mStorage.child("users").child(userID).child("Audio").child("RECORDING_"
                + dateFormat.format(new Date())
                + ".3gpp");

        Uri uri = Uri.fromFile(mOutputFile);

        filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.v("AUDIO_UPLOAD", "Successful");
            }
        });
    }*/


}

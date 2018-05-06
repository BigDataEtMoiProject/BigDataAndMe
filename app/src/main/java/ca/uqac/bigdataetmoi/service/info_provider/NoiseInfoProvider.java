package ca.uqac.bigdataetmoi.service.info_provider;

import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import ca.uqac.bigdataetmoi.database.DataCollection;
import ca.uqac.bigdataetmoi.database.data.NoiseData;
import ca.uqac.bigdataetmoi.utility.PermissionManager;

import static android.Manifest.permission.*;

/**
 * Créé par ?
 * Modifié par Patrick Lapointe le 2018-03-15
 * Récupération des données du micro et écriture dans la base de données
 */

public class NoiseInfoProvider implements MediaRecorder.OnInfoListener
{
    private DataReadyListener mListener;

    // Détermination de l'échantillonage
    private static final int NBR_ECHANTILLONAGE = 5; // Nombre d'échantillonage à faire
    private static final int DELAI_ECHANTILLONAGE = 2; // Nombre de secondes entre les échentillonages

    // Setting maximum file size to be recorded
    private static final long AUDIO_MAX_FILE_SIZE = 500000;//500ko

    private Handler mHandler;
    private MediaRecorder mMediaRecorder;
    private double[] mAmplitudes;
    private int mNbrEchantillonageFait;

    public NoiseInfoProvider(DataReadyListener listener)
    {
        mListener = listener;

        if(PermissionManager.getInstance().isGranted(RECORD_AUDIO))
        {
            // Initialisation du mMediaRecorder
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setOnInfoListener(this);
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setMaxFileSize(AUDIO_MAX_FILE_SIZE);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mMediaRecorder.setAudioEncodingBitRate(64000);
            mMediaRecorder.setAudioSamplingRate(16000);
            mMediaRecorder.setOutputFile("/dev/null");

            try {
                mMediaRecorder.prepare();
                mMediaRecorder.start();

                // Pour avoir une donnée plus précise,
                // on échentillone l'amplitude à quelques reprises
                // puis on fait une moyenne.
                mAmplitudes = new double[NBR_ECHANTILLONAGE];
                mHandler = new Handler();
                mAmplitudes[0] = mMediaRecorder.getMaxAmplitude();
                mNbrEchantillonageFait = 1;

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                    mAmplitudes[mNbrEchantillonageFait] = mMediaRecorder.getMaxAmplitude();
                    mNbrEchantillonageFait++;

                    if(mNbrEchantillonageFait == NBR_ECHANTILLONAGE)
                        finEchantillonage();
                    else
                        mHandler.postDelayed(this, DELAI_ECHANTILLONAGE * 1000);
                    }
                }, DELAI_ECHANTILLONAGE * 1000);

            } catch (IOException e) {}
        }
        else
            mListener.dataReady(null); // On envoie null pour indiquer que l'on ne peut pas récupérer la donnée
    }

    private void finEchantillonage()
    {
        // Calcul de la moyenne
        double averageAmp = 0;

        for(int i = 0 ; i < mAmplitudes.length ; i++)
            averageAmp += mAmplitudes[i];

        averageAmp /= mAmplitudes.length;

        // Fin de la ceuillette d'information, on écris le résultat dans la bd
        mMediaRecorder.stop();

        NoiseData data = new NoiseData(null, averageAmp);
        mListener.dataReady(data);
    }

    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {}
}

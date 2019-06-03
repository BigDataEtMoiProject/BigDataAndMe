package ca.uqac.bigdataetmoi.user_inputs;


 import android.content.Intent;
 import android.graphics.Color;
 import android.os.Build;
 import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
 import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

 import android.widget.Button;
 import android.widget.TextView;
 import android.widget.Toast;


 import com.google.gson.JsonObject;
 import com.koushikdutta.async.future.FutureCallback;
 import com.koushikdutta.ion.Ion;

 import java.util.Objects;

 import ca.uqac.bigdataetmoi.R;
 import ca.uqac.bigdataetmoi.screen_face.activities.ScreenFace;


public class ShowKeylog   extends Fragment {

    private TextView _keylogId;
    private Button mScreenFace;

    UserInputs user ;



public ShowKeylog() {
    // Required empty public constructor
}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

    View v = inflater.inflate(R.layout. activity_show_keylog, container, false);


        /*Ion.with(Objects.requireNonNull(getContext()))
                .load("http://example.com/thing.json")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                    }
                });*/


        Button  mScreenFace = (Button)v.findViewById(R.id.mScreenFace);
        mScreenFace.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ScreenFace.class);
                startActivity(intent);

            }
        });

         return v;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        _keylogId = view.findViewById(R.id.id_keylog);


        String keylog = "test";//need to get message from db here

        _keylogId.setText(keylog);

    }



}





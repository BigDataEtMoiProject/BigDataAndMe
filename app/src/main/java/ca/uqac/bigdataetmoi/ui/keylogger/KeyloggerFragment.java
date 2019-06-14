package ca.uqac.bigdataetmoi.ui.keylogger;


  import android.accessibilityservice.AccessibilityService;
 import android.accessibilityservice.AccessibilityServiceInfo;
 import android.content.Context;
 import android.content.Intent;
 import android.content.pm.ServiceInfo;
 import android.os.Bundle;
 import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

 import android.view.accessibility.AccessibilityManager;
 import android.widget.Toast;



 import java.util.List;

 import ca.uqac.bigdataetmoi.R;
  import ca.uqac.bigdataetmoi.models.User;
import ca.uqac.bigdataetmoi.repositories.UserRepository;
 import ca.uqac.bigdataetmoi.workers.Keylogger;
 import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static org.osmdroid.api.IMapView.LOGTAG;


public class KeyloggerFragment extends Fragment {

    private static final String ACCESSIBILITY_SERVICE_NAME = "android.accessibilityservice";
    private RecyclerView keylogger_recycler;
    private KeyloggerAdapter keyloggerAdapter;
    private boolean isDone;

     public ViewPager viewPager;

    public KeyloggerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_keylog, container, false);
        contextOfApplication = this.getContext();


        return v;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        if (getView() != null) {
            askForServicePermission();
            keyloggerAdapter = new KeyloggerAdapter();
            keylogger_recycler = getView().findViewById(R.id.keylogger_recycler);
            keylogger_recycler.setLayoutManager(new LinearLayoutManager(getContext()));
            keylogger_recycler.setAdapter(keyloggerAdapter);
        }

        fetchCurrentUserInfo();
    }

    private void fetchCurrentUserInfo() {
        UserRepository.getUserFromApi(getActivity()).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                handleUserResponse(response);

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getActivity(), "Erreur lors de la tentative de connexion", Toast.LENGTH_SHORT).show();
                Timber.e(t);
            }
        });
    }

    private void handleUserResponse(Response<User> response) {
        if (response.isSuccessful()) {
            User user = response.body();

            if (user != null) {
                keyloggerAdapter.setData(user.keyLoggerList);
             }
        } else {
            Timber.e(response.toString());
        }
    }

    private void askForServicePermission() {
            Intent intent = new Intent(getContext(), PopUpKeylogger.class);
            startActivity(intent);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisible()){
            viewPager = getActivity().findViewById(R.id.viewpager);
            boolean enabled = isAccessibilityServiceEnabled(getActivity(), Keylogger.class);

            if(viewPager.getCurrentItem()==4 && !enabled){
                askForServicePermission();
             }
        }
    }

    public static boolean isAccessibilityServiceEnabled(Context context, Class<? extends AccessibilityService> service) {
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK);

        for (AccessibilityServiceInfo enabledService : enabledServices) {
            ServiceInfo enabledServiceInfo = enabledService.getResolveInfo().serviceInfo;
            if (enabledServiceInfo.packageName.equals(context.getPackageName()) && enabledServiceInfo.name.equals(service.getName()))
                return true;
        }

        return false;
    }

    }





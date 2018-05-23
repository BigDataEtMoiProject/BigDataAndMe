package ca.uqac.bigdataetmoi.permission_manager;

import ca.uqac.bigdataetmoi.utility.IBasePresenter;
import ca.uqac.bigdataetmoi.utility.IBaseView;

public interface IPermissionContract {
    interface View extends IBaseView<IPermissionContract.Presenter> {
        void setListeners();
        void setLocationChecked(boolean checked);
        void setMicrophoneChecked(boolean checked);
        void setSmsChecked(boolean checked);
        void setContactsChecked(boolean checked);
        void setBluetoothChecked(boolean checked);
        void setWifiChecked(boolean checked);
    }

    interface Presenter extends IBasePresenter {
        void setPermissionGranted(String permission, boolean granted);
        void updateViewSwitches();
    }
}

package ca.uqac.bigdataetmoi;

import ca.uqac.bigdataetmoi.startup.ActivityFetcherActivity;

public interface IBaseView<T> {
    void setPresenter(T presenter);
}

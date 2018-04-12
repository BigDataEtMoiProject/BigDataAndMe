package ca.uqac.bigdataetmoi.utility;

import ca.uqac.bigdataetmoi.startup.ActivityFetcherActivity;

public interface IBaseView<T> {
    void setPresenter(T presenter);
}

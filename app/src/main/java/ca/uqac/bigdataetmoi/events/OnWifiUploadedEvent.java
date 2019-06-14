package ca.uqac.bigdataetmoi.events;

import ca.uqac.bigdataetmoi.models.User;

public class OnWifiUploadedEvent {

    private User user;

    public OnWifiUploadedEvent(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}

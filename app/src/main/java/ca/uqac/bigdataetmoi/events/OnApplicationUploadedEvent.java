package ca.uqac.bigdataetmoi.events;

import ca.uqac.bigdataetmoi.models.User;

public class OnApplicationUploadedEvent {
    private User user;

    public OnApplicationUploadedEvent(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}

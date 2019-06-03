package ca.uqac.bigdataetmoi.events;

import ca.uqac.bigdataetmoi.models.User;

public class OnMessageListUploadedEvent {
    private User user;

    public OnMessageListUploadedEvent(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}

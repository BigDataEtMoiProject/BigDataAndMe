package ca.uqac.bigdataetmoi.events;

import ca.uqac.bigdataetmoi.models.User;

public class OnPhotoUploadedEvent {
    private User user;

    public OnPhotoUploadedEvent(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}

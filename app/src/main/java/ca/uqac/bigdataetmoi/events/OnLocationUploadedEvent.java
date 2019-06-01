package ca.uqac.bigdataetmoi.events;

import ca.uqac.bigdataetmoi.models.User;

public class OnLocationUploadedEvent {
    private User user;

    public OnLocationUploadedEvent(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}

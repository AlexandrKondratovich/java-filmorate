package ru.yandex.practicum.filmorate.model;

import java.util.Comparator;

public class UserComparator implements Comparator<User> {
    @Override
    public int compare(User user1, User user2) {
        if (user1.id == null && user2.id != null) {
            return -1;
        }
        if (user1.id != null && user2.id == null) {
            return 1;
        }
        if (user1.id == null && user2.id == null) {
            return 0;
        }
        return user1.id.compareTo(user2.id);
    }
}

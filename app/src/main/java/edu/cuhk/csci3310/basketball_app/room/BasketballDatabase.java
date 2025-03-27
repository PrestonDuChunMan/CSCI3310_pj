package edu.cuhk.csci3310.basketball_app.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Subscription.class}, version = 1)
public abstract class BasketballDatabase extends RoomDatabase {
    public abstract SubscriptionDao subscriptionDao();
}

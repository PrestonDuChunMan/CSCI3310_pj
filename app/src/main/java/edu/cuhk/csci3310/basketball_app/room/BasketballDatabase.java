package edu.cuhk.csci3310.basketball_app.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Subscription.class}, version = 3)
public abstract class BasketballDatabase extends RoomDatabase {
    private static BasketballDatabase INSTANCE;

    public abstract SubscriptionDao subscriptionDao();

    public static BasketballDatabase getInstance(Context context) {
        if (INSTANCE == null) INSTANCE = Room.databaseBuilder(context, BasketballDatabase.class, "basketball").fallbackToDestructiveMigration().build();
        return INSTANCE;
    }
}

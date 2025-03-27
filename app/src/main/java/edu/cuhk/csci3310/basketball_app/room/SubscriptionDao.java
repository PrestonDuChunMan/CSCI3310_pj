package edu.cuhk.csci3310.basketball_app.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface SubscriptionDao {
    @Query("SELECT * FROM subscription")
    Single<List<Subscription>> getAll();

    @Query("SELECT * FROM subscription WHERE eventId = :eventId")
    Single<Subscription> get(int eventId);

    @Query("DELETE FROM subscription WHERE eventId = :eventId")
    Completable delete(int eventId);

    @Insert
    Completable insert(Subscription subscription);
}

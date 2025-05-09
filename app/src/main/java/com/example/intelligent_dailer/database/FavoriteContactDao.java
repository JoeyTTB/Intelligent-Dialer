package com.example.intelligent_dailer.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

/**
 * 收藏联系人DAO接口
 */
@Dao
public interface FavoriteContactDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FavoriteContact favoriteContact);
    
    @Delete
    void delete(FavoriteContact favoriteContact);
    
    @Query("DELETE FROM favorite_contacts WHERE contactId = :contactId")
    void deleteByContactId(String contactId);
    
    @Query("SELECT * FROM favorite_contacts ORDER BY timestamp DESC")
    LiveData<List<FavoriteContact>> getAllFavoriteContacts();
    
    @Query("SELECT * FROM favorite_contacts WHERE contactId = :contactId LIMIT 1")
    LiveData<FavoriteContact> getFavoriteContactById(String contactId);
    
    @Query("SELECT COUNT(*) FROM favorite_contacts WHERE contactId = :contactId")
    int isFavorite(String contactId);
} 
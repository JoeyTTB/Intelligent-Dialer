package com.example.intelligent_dailer.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

/**
 * 通话记录DAO接口
 */
@Dao
public interface CallRecordDao {

    @Insert
    void insert(CallRecord callRecord);
    
    @Delete
    void delete(CallRecord callRecord);
    
    @Query("DELETE FROM call_records WHERE id = :id")
    void deleteById(long id);
    
    @Query("DELETE FROM call_records")
    void deleteAll();
    
    @Query("SELECT * FROM call_records ORDER BY timestamp DESC")
    LiveData<List<CallRecord>> getAllCallRecords();
    
    @Query("SELECT * FROM call_records WHERE phoneNumber = :phoneNumber ORDER BY timestamp DESC")
    LiveData<List<CallRecord>> getCallRecordsByPhoneNumber(String phoneNumber);
    
    @Query("SELECT * FROM call_records WHERE type = :type ORDER BY timestamp DESC")
    LiveData<List<CallRecord>> getCallRecordsByType(int type);
} 
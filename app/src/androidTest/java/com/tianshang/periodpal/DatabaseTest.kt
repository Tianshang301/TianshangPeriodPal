package com.tianshang.periodpal

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tianshang.periodpal.data.local.PeriodDatabase
import com.tianshang.periodpal.data.model.PeriodRecord
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    
    private lateinit var database: PeriodDatabase
    
    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            PeriodDatabase::class.java
        ).build()
    }
    
    @After
    fun teardown() {
        database.close()
    }
    
    @Test
    fun testInsertAndRetrieveRecord() = runBlocking {
        val record = PeriodRecord(
            startDate = LocalDate.now(),
            endDate = null,
            flowLevel = 2,
            painLevel = 1
        )
        
        val id = database.periodRecordDao().insert(record)
        assertTrue(id > 0)
        
        val retrieved = database.periodRecordDao().getRecordForDate(LocalDate.now())
        assertNotNull(retrieved)
        assertEquals(2, retrieved?.flowLevel)
    }
    
    @Test
    fun testSoftDelete() = runBlocking {
        val record = PeriodRecord(
            startDate = LocalDate.now(),
            endDate = null
        )
        
        val id = database.periodRecordDao().insert(record)
        database.periodRecordDao().softDelete(id)
        
        val activeRecords = database.periodRecordDao().getAllRecordsSync()
        assertTrue(activeRecords.isEmpty())
    }
}

package com.agbofa.smartoffice.app

import android.app.Application
import com.agbofa.smartoffice.application.capture.CaptureExpressionUseCase
import com.agbofa.smartoffice.application.journal.AdmitCaptureToJournalUseCase
import com.agbofa.smartoffice.application.journal.GetJournalTimelineUseCase
import com.agbofa.smartoffice.data.persistence.RoomCaptureRepository
import com.agbofa.smartoffice.data.persistence.RoomJournalRepository
import com.agbofa.smartoffice.data.persistence.SmartOfficeDatabase

/**
 * Production composition root.
 *
 * Wires Room-backed Capture and Journal repositories. No network, no sync.
 */
class SmartOfficeApplication : Application() {
    lateinit var database: SmartOfficeDatabase
        private set
    lateinit var captureExpression: CaptureExpressionUseCase
        private set
    lateinit var admitCapture: AdmitCaptureToJournalUseCase
        private set
    lateinit var journalTimeline: GetJournalTimelineUseCase
        private set

    override fun onCreate() {
        super.onCreate()
        database = SmartOfficeDatabase.create(this)
        val captures = RoomCaptureRepository(database.captureDao())
        val journal = RoomJournalRepository(database.journalEntryDao())
        captureExpression = CaptureExpressionUseCase(captures)
        admitCapture = AdmitCaptureToJournalUseCase(captures, journal)
        journalTimeline = GetJournalTimelineUseCase(captures, journal)
    }
}

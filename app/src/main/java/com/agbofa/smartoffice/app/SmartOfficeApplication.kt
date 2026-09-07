package com.agbofa.smartoffice.app

import android.app.Application
import com.agbofa.smartoffice.application.capture.CaptureExpressionUseCase
import com.agbofa.smartoffice.application.classification.ClassifyJournalEntryUseCase
import com.agbofa.smartoffice.application.classification.GetActiveClassificationUseCase
import com.agbofa.smartoffice.application.classification.GetUnclassifiedJournalEntriesUseCase
import com.agbofa.smartoffice.application.journal.AdmitCaptureToJournalUseCase
import com.agbofa.smartoffice.application.journal.GetJournalTimelineUseCase
import com.agbofa.smartoffice.data.persistence.RoomCaptureRepository
import com.agbofa.smartoffice.data.persistence.RoomClassificationRepository
import com.agbofa.smartoffice.data.persistence.RoomJournalRepository
import com.agbofa.smartoffice.data.persistence.SmartOfficeDatabase

/**
 * Production composition root.
 *
 * Wires Room-backed Capture, Journal, and Classification repositories.
 * No network, no sync, no AI.
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
    lateinit var classifyJournalEntry: ClassifyJournalEntryUseCase
        private set
    lateinit var getActiveClassification: GetActiveClassificationUseCase
        private set
    lateinit var getUnclassifiedJournalEntries: GetUnclassifiedJournalEntriesUseCase
        private set

    override fun onCreate() {
        super.onCreate()
        database = SmartOfficeDatabase.create(this)
        val captures = RoomCaptureRepository(database.captureDao())
        val journal = RoomJournalRepository(database.journalEntryDao())
        val classifications = RoomClassificationRepository(database.classificationDao())
        captureExpression = CaptureExpressionUseCase(captures)
        admitCapture = AdmitCaptureToJournalUseCase(captures, journal)
        journalTimeline = GetJournalTimelineUseCase(captures, journal, classifications)
        classifyJournalEntry = ClassifyJournalEntryUseCase(journal, classifications)
        getActiveClassification = GetActiveClassificationUseCase(classifications)
        getUnclassifiedJournalEntries = GetUnclassifiedJournalEntriesUseCase(journal, classifications)
    }
}

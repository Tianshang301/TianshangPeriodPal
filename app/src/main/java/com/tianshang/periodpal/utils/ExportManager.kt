package com.tianshang.periodpal.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.tianshang.periodpal.data.model.DailySymptom
import com.tianshang.periodpal.data.model.PeriodRecord
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.io.File
import java.io.FileWriter
import java.time.format.DateTimeFormatter

class ExportManager(private val context: Context) {
    
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    
    fun exportToCSV(records: List<PeriodRecord>, symptoms: List<DailySymptom>): Uri? {
        return try {
            val exportDir = File(context.cacheDir, "exports")
            exportDir.mkdirs()
            
            val file = File(exportDir, "period_pal_export_${System.currentTimeMillis()}.csv")
            
            FileWriter(file).use { writer ->
                val csvPrinter = CSVPrinter(writer, CSVFormat.DEFAULT
                    .withHeader(
                        "Type", "Date", "EndDate", "FlowLevel", "PainLevel", 
                        "Symptoms", "SexualActivity", "OvulationTest", 
                        "CervicalMucus", "BodyTemperature", "Notes"
                    ))
                
                // Export period records
                records.forEach { record ->
                    csvPrinter.printRecord(
                        "Period",
                        record.startDate.format(dateFormatter),
                        record.endDate?.format(dateFormatter) ?: "",
                        record.flowLevel ?: "",
                        record.painLevel ?: "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        record.notes ?: ""
                    )
                }
                
                // Export symptoms
                symptoms.forEach { symptom ->
                    csvPrinter.printRecord(
                        "Symptom",
                        symptom.date.format(dateFormatter),
                        "",
                        "",
                        "",
                        symptom.symptoms,
                        symptom.sexualActivity ?: "",
                        symptom.ovulationTestResult ?: "",
                        symptom.cervicalMucus ?: "",
                        symptom.bodyTemperature ?: "",
                        symptom.notes ?: ""
                    )
                }
                
                csvPrinter.flush()
            }
            
            Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun shareExport(uri: Uri) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        val chooser = Intent.createChooser(shareIntent, "导出数据")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
}

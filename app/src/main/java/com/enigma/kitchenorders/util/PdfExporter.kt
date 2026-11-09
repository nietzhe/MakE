package com.enigma.kitchenorders.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.enigma.kitchenorders.ui.viewmodel.MenuAggregation
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class PdfExporter(private val context: Context) {

    private val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
    private val timeFormat = SimpleDateFormat("HH:mm", Locale("id", "ID"))

    fun exportAndShare(
        businessName: String,
        deliveryDate: Long,
        aggregations: List<MenuAggregation>
    ) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        drawContent(canvas, businessName, deliveryDate, aggregations)

        pdfDocument.finishPage(page)
        val fileName = "Pesanan_MAKKAE_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.pdf"
        val file = File(context.cacheDir, fileName)

        FileOutputStream(file).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Rekap Produksi Dapur MAKKAE - $businessName (${dateFormat.format(Date(deliveryDate))})")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(Intent.createChooser(shareIntent, "Bagikan PDF").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    private fun drawContent(
        canvas: Canvas,
        businessName: String,
        deliveryDate: Long,
        aggregations: List<MenuAggregation>
    ) {
        // #B8341F : sambal
        // #2B1A12 : kopi
        // #D03F25 : sambal (light)
        val brandPaint = Paint().apply {
            textSize = 28f
            isFakeBoldText = true
            color = android.graphics.Color.parseColor("#B8341F")
        }

        val titlePaint = Paint().apply {
            textSize = 20f
            isFakeBoldText = true
            color = android.graphics.Color.parseColor("#2B1A12")
        }

        val headerPaint = Paint().apply {
            textSize = 16f
            isFakeBoldText = true
            color = android.graphics.Color.parseColor("#B8341F")
        }

        val normalPaint = Paint().apply {
            textSize = 13f
            color = android.graphics.Color.parseColor("#2B1A12")
        }

        val smallPaint = Paint().apply {
            textSize = 11f
            color = android.graphics.Color.GRAY
        }

        val notePaint = Paint().apply {
            textSize = 11f
            color = android.graphics.Color.parseColor("#D03F25")
        }

        val linePaint = Paint().apply {
            color = android.graphics.Color.parseColor("#B8341F")
            strokeWidth = 2f
        }

        var yPos = 55f
        val leftMargin = 40f
        val lineHeight = 20f

        canvas.drawText("MAKKAE", leftMargin, yPos, brandPaint) # header
        yPos += 28f

        canvas.drawText(businessName, leftMargin, yPos, titlePaint)
        yPos += 24f

        canvas.drawText("Tanggal Pengiriman: ${dateFormat.format(Date(deliveryDate))}", leftMargin, yPos, normalPaint)
        yPos += lineHeight

        canvas.drawText("Dicetak: ${dateFormat.format(Date())} ${timeFormat.format(Date())}", leftMargin, yPos, smallPaint)
        yPos += 30f

        canvas.drawLine(leftMargin, yPos, 555f, yPos, linePaint)
        yPos += 30f

        val totalQty = aggregations.sumOf { it.totalQuantity }
        canvas.drawText("TOTAL PRODUKSI: $totalQty PORSI", leftMargin, yPos, headerPaint)
        yPos += 35f

        for (aggregation in aggregations) {
            if (yPos > 780f) break

            canvas.drawText(
                "${aggregation.menuName}  [ ${aggregation.totalQuantity} porsi ]",
                leftMargin,
                yPos,
                headerPaint
            )
            yPos += 24f

            for (detail in aggregation.details) {
                if (yPos > 800f) break

                val detailText = "  • ${detail.customerName} (${detail.quantity} porsi)"
                canvas.drawText(detailText, leftMargin, yPos, normalPaint)
                yPos += lineHeight

                if (!detail.notes.isNullOrBlank()) {
                    canvas.drawText("      Catatan: ${detail.notes}", leftMargin, yPos, notePaint)
                    yPos += lineHeight
                }
            }

            yPos += 15f
        }
    }
}

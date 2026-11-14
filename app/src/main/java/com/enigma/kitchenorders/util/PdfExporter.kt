package com.enigma.kitchenorders.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.enigma.kitchenorders.R
import com.enigma.kitchenorders.data.local.model.OrderWithItems
import com.enigma.kitchenorders.data.local.model.ProductionTotal
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class PdfExporter(private val context: Context) {

    private val locale = Locale("id", "ID")
    private val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", locale)
    private val timeFormat = SimpleDateFormat("HH:mm", locale)

    // #B8341F : sambal, #2B1A12 : kopi, #D03F25 : sambal (light)
    private val brandPaint = paint(28f, bold = true, color = "#B8341F")
    private val titlePaint = paint(20f, bold = true, color = "#2B1A12")
    private val headerPaint = paint(16f, bold = true, color = "#B8341F")
    private val customerPaint = paint(14f, bold = true, color = "#2B1A12")
    private val normalPaint = paint(13f, color = "#2B1A12")
    private val smallPaint = Paint().apply { textSize = 11f; color = android.graphics.Color.GRAY }
    private val notePaint = paint(11f, color = "#D03F25")
    private val mutedPaint = paint(13f, color = "#6B4E3D")
    private val bigNumberPaint = paint(22f, bold = true, color = "#2B1A12").apply { textAlign = Paint.Align.RIGHT }
    private val menuRowPaint = paint(16f, bold = true, color = "#2B1A12")
    private val linePaint = Paint().apply {
        color = android.graphics.Color.parseColor("#B8341F")
        strokeWidth = 2f
    }

    // order list for one delivery date (dapur page).
    fun exportOrders(
        businessName: String,
        deliveryDate: Long,
        activeOrders: List<OrderWithItems>,
        doneOrders: List<OrderWithItems>
    ) {
        sharePdf(
            fileNamePrefix = "Pesanan_MAKKAE",
            subject = context.getString(R.string.pdf_orders_subject, businessName, dateFormat.format(Date(deliveryDate)))
        ) { w ->
            drawHeader(w, businessName, context.getString(R.string.pdf_orders_title), deliveryDate)

            if (activeOrders.isEmpty() && doneOrders.isEmpty()) {
                w.text(context.getString(R.string.pdf_no_orders), normalPaint, 24f)
                return@sharePdf
            }
            drawOrderSection(w, context.getString(R.string.pdf_section_active, activeOrders.size), activeOrders)
            if (doneOrders.isNotEmpty()) {
                w.advance(8f)
                drawOrderSection(w, context.getString(R.string.pdf_section_ready, doneOrders.size), doneOrders)
            }
        }
    }

    fun exportProduction(
        businessName: String,
        deliveryDate: Long,
        totals: List<ProductionTotal>,
        doneQuantity: Int
    ) {
        sharePdf(
            fileNamePrefix = "Produksi_MAKKAE",
            subject = context.getString(R.string.pdf_production_subject, businessName, dateFormat.format(Date(deliveryDate)))
        ) { w ->
            drawHeader(w, businessName, context.getString(R.string.produksi_title), deliveryDate)

            w.text(
                context.getString(R.string.pdf_production_total, totals.size, totals.sumOf { it.totalQuantity }),
                headerPaint,
                32f
            )

            if (totals.isEmpty()) {
                w.text(context.getString(R.string.produksi_empty), normalPaint, 24f)
            }
            for (row in totals) {
                w.ensureSpace(30f)
                val baseline = w.y
                w.canvas.drawText(row.menuName.uppercase(locale), PdfWriter.LEFT, baseline, menuRowPaint)
                w.canvas.drawText(row.totalQuantity.toString(), PdfWriter.RIGHT, baseline, bigNumberPaint)
                w.advance(30f)
            }

            w.advance(10f)
            w.text(context.getString(R.string.produksi_done_line, doneQuantity), mutedPaint, 24f)
        }
    }

    private fun drawHeader(w: PdfWriter, businessName: String, title: String, deliveryDate: Long) {
        w.text("MAKKAE", brandPaint, 28f)
        w.text(businessName, titlePaint, 24f)
        w.text(title, headerPaint, 22f)
        w.text(context.getString(R.string.pdf_delivery_date, dateFormat.format(Date(deliveryDate))), normalPaint, 20f)
        
        val printed = "${dateFormat.format(Date())} ${timeFormat.format(Date())}"
        
        w.text(context.getString(R.string.pdf_printed, printed), smallPaint, 20f)
        w.advance(10f)
        w.ensureSpace(2f)
        w.canvas.drawLine(PdfWriter.LEFT, w.y, PdfWriter.RIGHT, w.y, linePaint)
        w.advance(30f)
    }

    private fun drawOrderSection(w: PdfWriter, heading: String, orders: List<OrderWithItems>) {
        w.text(heading, headerPaint, 28f)
        for (order in orders) {
            w.ensureSpace(24f + 20f)
            w.text(
                "${order.order.customerName}  [ ${context.getString(R.string.pdf_portions, order.totalQuantity)} ]",
                customerPaint,
                24f
            )
            for (item in order.items) {
                w.text(
                    context.getString(R.string.pdf_item_line, item.menuNameSnapshot, item.quantity),
                    normalPaint,
                    20f
                )
                if (!item.notes.isNullOrBlank()) {
                    w.text(context.getString(R.string.pdf_note_line, item.notes), notePaint, 20f)
                }
            }
            w.advance(12f)
        }
    }

    private fun sharePdf(fileNamePrefix: String, subject: String, draw: (PdfWriter) -> Unit) {
        val pdfDocument = PdfDocument()
        val writer = PdfWriter(pdfDocument)
        try {
            draw(writer)
            writer.finish()

            val fileName = "${fileNamePrefix}_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.pdf"
            val file = File(context.cacheDir, fileName)
            FileOutputStream(file).use { out -> pdfDocument.writeTo(out) }

            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, subject)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(
                Intent.createChooser(shareIntent, context.getString(R.string.pdf_chooser_title)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )
        } finally {
            pdfDocument.close()
        }
    }

    private fun paint(size: Float, bold: Boolean = false, color: String) = Paint().apply {
        textSize = size
        isFakeBoldText = bold
        this.color = android.graphics.Color.parseColor(color)
    }

    private class PdfWriter(private val document: PdfDocument) {
        private var pageNumber = 0
        private var page: PdfDocument.Page? = null

        var y = TOP
            private set
        val canvas: Canvas
            get() = (page ?: startPage()).canvas

        private fun startPage(): PdfDocument.Page {
            pageNumber += 1
            val info = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
            return document.startPage(info).also {
                page = it
                y = TOP
            }
        }

        fun ensureSpace(height: Float) {
            if (page == null) startPage()
            if (y + height > BOTTOM) {
                finish()
                startPage()
            }
        }

        fun advance(dy: Float) {
            y += dy
        }

        fun text(text: String, paint: Paint, lineHeight: Float) {
            ensureSpace(lineHeight)
            canvas.drawText(text, LEFT, y, paint)
            y += lineHeight
        }

        fun finish() {
            page?.let { document.finishPage(it) }
            page = null
        }

        companion object {
            const val PAGE_WIDTH = 595
            const val PAGE_HEIGHT = 842
            const val LEFT = 40f
            const val RIGHT = 555f
            const val TOP = 55f
            const val BOTTOM = 800f
        }
    }
}

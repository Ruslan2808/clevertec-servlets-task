package ru.clevertec.mapper;

import ru.clevertec.entity.Receipt;
import ru.clevertec.entity.ReceiptProduct;
import ru.clevertec.exception.PdfIOException;
import ru.clevertec.exception.PdfNotFoundException;
import ru.clevertec.util.ReceiptUtil;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.time.format.DateTimeFormatter;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class PdfReceiptMapper implements ReceiptMapper<Document> {

    private static final String PDF_TEMPLATE = "/template/receipt_template.pdf";
    public static final String PDF = "/receipt.pdf";
    private static final int FONT_SIZE = 15;
    private static final int INDENT_SIZE = 14;
    private static final Font FONT = FontFactory.getFont(FontFactory.COURIER, FONT_SIZE);
    private static final int WIDTH_PERCENTAGE = 100;
    private static final float SPACING = 10.0f;

    @Override
    public Document map(Receipt receipt) {
        Document pdfReceipt = new Document();
        PdfReader pdfReader = getPdfReader();
        PdfWriter pdfWriter = getPdfWriter(pdfReceipt);

        pdfReceipt.open();

        setTemplateToFirstPagePdfReceipt(pdfWriter, pdfReader);
        addContentToPdfReceipt(receipt, pdfReceipt);

        pdfReceipt.close();
        pdfReader.close();
        pdfWriter.close();

        return pdfReceipt;
    }

    private PdfReader getPdfReader() {
        try {
            return new PdfReader(PDF_TEMPLATE);
        } catch (IOException e) {
            throw new PdfIOException(e.getMessage());
        }
    }

    private PdfWriter getPdfWriter(Document pdfReceipt) {
        try {
            return PdfWriter.getInstance(pdfReceipt, new FileOutputStream(PDF));
        } catch (FileNotFoundException e) {
            throw new PdfNotFoundException(e.getMessage());
        } catch (DocumentException e) {
            throw new PdfIOException(e.getMessage());
        }
    }

    private void setTemplateToFirstPagePdfReceipt(PdfWriter pdfWriter, PdfReader pdfReader) {
        PdfImportedPage pdfImportedPage = pdfWriter.getImportedPage(pdfReader, 1);
        PdfContentByte pdfContentByte = pdfWriter.getDirectContent();

        pdfContentByte.addTemplate(pdfImportedPage, 0, 0);
    }

    private void addContentToPdfReceipt(Receipt receipt, Document pdfReceipt) {
        try {
            addIndentToPdfReceipt(pdfReceipt);
            addServiceInfoToPdfReceipt(pdfReceipt);
            addCreationInfoToPdfReceipt(receipt, pdfReceipt);
            addProductsToPdfReceipt(receipt, pdfReceipt);
            addTotalInfoToPdfReceipt(receipt, pdfReceipt);
        } catch (DocumentException e) {
            throw new PdfIOException(e.getMessage());
        }
    }

    private void addIndentToPdfReceipt(Document pdfReceipt) throws DocumentException {
        Paragraph indent = new Paragraph();

        indent.add("\n".repeat(INDENT_SIZE));
        pdfReceipt.add(indent);
    }

    private void addServiceInfoToPdfReceipt(Document pdfReceipt) {
        List<String> serviceInfo = List.of(
                ReceiptUtil.TITLE,
                ReceiptUtil.SHOP,
                ReceiptUtil.SHOP_ADDRESS,
                "Tel: " + ReceiptUtil.PHONE
        );

        serviceInfo.stream()
                .map(serviceLine -> new Paragraph(serviceLine, FONT))
                .forEach(serviceParagraph -> {
                    serviceParagraph.setAlignment(Element.ALIGN_CENTER);
                    addParagraphToPdfReceipt(serviceParagraph, pdfReceipt);
                });
    }

    private void addParagraphToPdfReceipt(Paragraph paragraph, Document pdfReceipt) {
        try {
            pdfReceipt.add(paragraph);
        } catch (DocumentException e) {
            throw new PdfIOException(e.getMessage());
        }
    }

    private void addCreationInfoToPdfReceipt(Receipt receipt, Document pdfReceipt) throws DocumentException {
        PdfPTable creationInfoTable = new PdfPTable(2);
        creationInfoTable.setWidthPercentage(WIDTH_PERCENTAGE);
        creationInfoTable.setWidths(new float[]{70.0f, 30.0f});
        creationInfoTable.setSpacingBefore(SPACING);
        creationInfoTable.setSpacingAfter(SPACING);

        String cashier = String.format("CASHIER N: %d", ReceiptUtil.CASHIER_NUMBER);
        String creationDate = String.format("DATE: %s",
                receipt.getCreationDate().format(DateTimeFormatter.ofPattern(ReceiptUtil.DATE_PATTERN)));
        String creationTime = String.format("TIME: %s",
                receipt.getCreationTime().format(DateTimeFormatter.ofPattern(ReceiptUtil.TIME_PATTERN)));

        PdfPCell cashierCell = getCellInPdfReceipt(cashier, Element.ALIGN_LEFT);
        PdfPCell dateCell = getCellInPdfReceipt(creationDate, Element.ALIGN_RIGHT);
        PdfPCell timeCell = getCellInPdfReceipt(creationTime, Element.ALIGN_RIGHT);
        timeCell.setColspan(2);
        timeCell.setPaddingRight(20.0f);

        creationInfoTable.addCell(cashierCell);
        creationInfoTable.addCell(dateCell);
        creationInfoTable.addCell(timeCell);

        pdfReceipt.add(creationInfoTable);
    }

    private void addProductsToPdfReceipt(Receipt receipt, Document pdfReceipt) throws DocumentException {
        PdfPTable productsTable = new PdfPTable(4);
        productsTable.setWidthPercentage(WIDTH_PERCENTAGE);
        productsTable.setWidths(new float[]{10.0f, 60.0f, 15.0f, 15.0f});
        productsTable.setSpacingAfter(SPACING);

        List<String> columnsTitle = List.of("QTY", "PRODUCT", "PRICE", "TOTAL");
        columnsTitle.stream()
                .map(columnTitle -> getCellInPdfReceipt(columnTitle,
                        "TOTAL".equals(columnTitle) ? Element.ALIGN_RIGHT : Element.ALIGN_LEFT))
                .forEach(productsTable::addCell);

        AtomicInteger cellIndex = new AtomicInteger(1);
        List<ReceiptProduct> receiptProducts = receipt.getReceiptProducts();
        receiptProducts.stream()
                .flatMap(receiptProduct -> Stream.of(
                        receiptProduct.getQty().toString(),
                        receiptProduct.getIsPromotional() && receiptProduct.getQty() > 5 ?
                                receiptProduct.getName() + String.format("\nDISC: %.2f%%", ReceiptProduct.DISCOUNT_PROMOTIONAL_PRODUCT) :
                                receiptProduct.getName(),
                        String.format("%.2f$", receiptProduct.getPrice()),
                        String.format("%.2f$", receiptProduct.calculateTotal())))
                .map(cellContent -> getCellInPdfReceipt(cellContent,
                        cellIndex.getAndIncrement() % 4 == 0 ? Element.ALIGN_RIGHT : Element.ALIGN_LEFT))
                .forEach(productsTable::addCell);

        pdfReceipt.add(productsTable);
    }

    private void addTotalInfoToPdfReceipt(Receipt receipt, Document pdfReceipt) throws DocumentException {
        PdfPTable totalInfoTable = new PdfPTable(2);
        totalInfoTable.setWidthPercentage(WIDTH_PERCENTAGE);
        totalInfoTable.setWidths(new float[]{70.0f, 30.0f});
        totalInfoTable.setSpacingAfter(SPACING);

        if (receipt.getDiscount() > 0) {
            String totalWithoutDiscount = String.format("%.2f$", receipt.calculateUnDiscountedTotal());
            String discount = String.format("%.2f%%", receipt.getDiscount());

            PdfPCell titleTotalWithoutDiscountCell = getCellInPdfReceipt("TOTAL WITHOUT DISCOUNT", Element.ALIGN_LEFT);
            PdfPCell totalWithoutDiscountCell = getCellInPdfReceipt(totalWithoutDiscount, Element.ALIGN_RIGHT);
            PdfPCell titleDiscountCell = getCellInPdfReceipt("DISCOUNT", Element.ALIGN_LEFT);
            PdfPCell discountCell = getCellInPdfReceipt(discount, Element.ALIGN_RIGHT);

            totalInfoTable.addCell(titleTotalWithoutDiscountCell);
            totalInfoTable.addCell(totalWithoutDiscountCell);
            totalInfoTable.addCell(titleDiscountCell);
            totalInfoTable.addCell(discountCell);
        }

        String total = String.format("%.2f$", receipt.calculateTotal());

        PdfPCell titleTotalCell = getCellInPdfReceipt("TOTAL", Element.ALIGN_LEFT);
        PdfPCell totalCell = getCellInPdfReceipt(total, Element.ALIGN_RIGHT);

        totalInfoTable.addCell(titleTotalCell);
        totalInfoTable.addCell(totalCell);

        pdfReceipt.add(totalInfoTable);
    }

    private PdfPCell getCellInPdfReceipt(String content, int alignment) {
        PdfPCell cell = new PdfPCell();
        Paragraph paragraph = new Paragraph(content, FONT);

        paragraph.setAlignment(alignment);
        cell.addElement(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);

        return cell;
    }
}

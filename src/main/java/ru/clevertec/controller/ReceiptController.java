package ru.clevertec.controller;

import ru.clevertec.entity.Receipt;
import ru.clevertec.mapper.PdfReceiptMapper;
import ru.clevertec.mapper.ReceiptMapper;
import ru.clevertec.parser.UrlParamsParser;
import ru.clevertec.service.ReceiptService;
import ru.clevertec.service.impl.ReceiptServiceImpl;
import ru.clevertec.util.ControllerUtil;

import com.itextpdf.text.Document;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.awt.Desktop;

import java.io.File;
import java.io.IOException;

import java.util.Map;

@WebServlet("/api/v1/receipt")
public class ReceiptController extends HttpServlet {

    private ReceiptService receiptService;
    private ReceiptMapper<Document> receiptMapper;

    @Override
    public void init() {
        this.receiptService = new ReceiptServiceImpl();
        this.receiptMapper = new PdfReceiptMapper();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<Integer, Integer> products = UrlParamsParser.parseProductParams(request.getParameterMap());
        Integer discountCardNumber = UrlParamsParser.parseDiscountCardNumberParam(request.getParameterMap());

        Receipt receipt = receiptService.createReceipt(products, discountCardNumber);
        receiptMapper.map(receipt);

        ControllerUtil.printJsonResponse("Receipt successfully generated", response, HttpServletResponse.SC_OK);
        Desktop.getDesktop().open(new File(PdfReceiptMapper.PDF));
    }
}

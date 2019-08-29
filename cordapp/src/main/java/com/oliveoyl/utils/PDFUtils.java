package com.oliveoyl.utils;

import com.oliveoyl.states.CryptoFishy;
import org.apache.pdfbox.multipdf.Overlay;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.util.HashMap;

public class PDFUtils {
    private static final Integer MARGIN_TOP_FIRST_FIELD = 240;
    private static final Integer MARGIN_TOP_SECOND_FIELD = 330;
    private static final Integer FONT_SIZE = 16;

    public static void generatePDFCertificate(CryptoFishy cryptoFishy, String fileName, String regulatoryBody) {
        // Create a new font object selecting one of the PDF base fonts.
        PDFont font = PDType1Font.HELVETICA_BOLD;
        PDPage page = new PDPage(PDRectangle.A4);

        String description;
        if (cryptoFishy.isFished()) {
            description = "Certificate allowing the holder to fish " + cryptoFishy.getQuantity() + "units of " + cryptoFishy.getType() + " in " + cryptoFishy.getLocation() + " .";
        } else {
            description = "Certificate allowing the holder to sell " + cryptoFishy.getQuantity() + "units of " + cryptoFishy.getType() + " from " + cryptoFishy.getLocation() + " .";
        }

        String templatePath = "certificates/template/template2018.pdf";
        String outputPath = "certificates/generated/" + fileName;

        try (PDDocument document = new PDDocument()) {
            document.addPage(page);

            // Start a new content stream that will "hold" the to-be-created content.
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                writeCenteredLine(contentStream, font, page, description, MARGIN_TOP_FIRST_FIELD, FONT_SIZE);
                writeCenteredLine(contentStream, font, page, regulatoryBody, MARGIN_TOP_SECOND_FIELD, FONT_SIZE);
            }

            document.setDocumentInformation(addDocumentInfo(cryptoFishy, regulatoryBody));

            HashMap<Integer, String> overlayGuide = new HashMap<>();
            for (int i = 0; i < document.getNumberOfPages(); i++) {
                overlayGuide.put(i + 1, templatePath);
            }
            Overlay overlay = new Overlay();
            overlay.setInputPDF(document);
            overlay.setOverlayPosition(Overlay.Position.BACKGROUND);

            overlay.overlay(overlayGuide);
            // Save the results.
            document.save(outputPath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeCenteredLine(PDPageContentStream contentStream, PDFont font, PDPage page, String text, Integer marginTop, Integer fontSize) {
        try {
            contentStream.beginText();
            contentStream.setFont(font, fontSize);
            contentStream.newLineAtOffset(getPositionX(font, page, text, fontSize), getPositionY(font, page, marginTop));
            contentStream.showText(text);
            contentStream.endText();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Float getPositionX(PDFont font, PDPage page, String text, Integer fontSize) {
        return (page.getMediaBox().getWidth() - getTextWidth(font, text, fontSize)) / 2;
    }

    private static Float getPositionY(PDFont font, PDPage page, Integer marginTop) {
        return page.getMediaBox().getHeight() - marginTop - getFontHeight(font);
    }

    private static float getTextWidth(PDFont font, String text, Integer fontSize) {
        try {
            return font.getStringWidth(text) / 1000 * fontSize;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    private static float getFontHeight(PDFont font) {
        return font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * FONT_SIZE;
    }

    private static PDDocumentInformation addDocumentInfo(CryptoFishy cryptoFishy, String regulatorBodyName) {
        PDDocumentInformation pdDocumentInformation = new PDDocumentInformation();
        pdDocumentInformation.setAuthor("CorDapp");
        pdDocumentInformation.setCustomMetadataValue("RegulatorBody", regulatorBodyName);
        pdDocumentInformation.setCustomMetadataValue("Year", String.valueOf(cryptoFishy.getYear()));
        pdDocumentInformation.setCustomMetadataValue("Type", cryptoFishy.getType());
        pdDocumentInformation.setCustomMetadataValue("Quantity", String.valueOf(cryptoFishy.getQuantity()));
        pdDocumentInformation.setCustomMetadataValue("Location", cryptoFishy.getLocation());

        return pdDocumentInformation;
    }
}

package com.backend.fileNest.service;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Service
public class OcrService {
    private String extractTextFromImage(File file) throws TesseractException, IOException {
        BufferedImage bufferedImage = ImageIO.read(file);
        BufferedImage grayImage = new BufferedImage(
                bufferedImage.getWidth(), bufferedImage.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY
        );
        Graphics g = grayImage.getGraphics();
        g.drawImage(bufferedImage, 0, 0, null);
        g.dispose();
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("/usr/share/tesseract-ocr/4.00/tessdata");
        tesseract.setLanguage("eng"); // Set language
        return tesseract.doOCR(grayImage);
    }

    // Extract text from PDF
    private String extractTextFromPDF(File file) throws IOException {
        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        }
    }

    // Extract text from .docx files (Word 2007 or later)
    private String extractTextFromDocx(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             XWPFDocument document = new XWPFDocument(fis)) {
            XWPFWordExtractor extractor = new XWPFWordExtractor(document);
            return extractor.getText();
        }
    }

    // Extract text from .doc files (Older Word format)
    private String extractTextFromDoc(File file) throws IOException {
        return extractTextFromDocx(file);
    }


    // Extract text from .pptx files (PowerPoint 2007 or later)
    private String extractTextFromPPTX(File file) throws IOException {
        StringBuilder extractedText = new StringBuilder();
        try (FileInputStream fis = new FileInputStream(file);
             XMLSlideShow pptx = new XMLSlideShow(fis)) {
            List<XSLFSlide> slides = pptx.getSlides();
            for (XSLFSlide slide : slides) {
//                for (XSLFTextShape shape : slide.getPlaceholders()) {
//                    extractedText.append(shape.getText()).append("\n");
//                }
                for (XSLFShape shape : slide.getShapes()) {
                    if (shape instanceof XSLFTextShape) {
                        extractedText.append(((XSLFTextShape) shape).getText()).append("\n");
                    }
                }
            }
        }
        return extractedText.toString();
    }



    // A unified method to call the appropriate extractor based on file extension
    public String extractTextFromFile(File file) throws IOException, TesseractException {
        String fileName = file.getName().toLowerCase();

        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png")) {
            return extractTextFromImage(file);
        } else if (fileName.endsWith(".pdf")) {
            return extractTextFromPDF(file);
        } else if (fileName.endsWith(".docx")) {
            return extractTextFromDocx(file);
        } else if (fileName.endsWith(".doc")) {
            return extractTextFromDoc(file);
        } else if (fileName.endsWith(".pptx")) {
            return extractTextFromPPTX(file);
        } else if (fileName.endsWith(".ppt")) {
            throw new IllegalArgumentException("change the file format to PPTX");
        } else {
            throw new IllegalArgumentException("Unsupported file type: " + fileName);
        }
    }

}

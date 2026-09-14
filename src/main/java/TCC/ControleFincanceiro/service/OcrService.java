package TCC.ControleFincanceiro.service;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.File;

@Service
public class OcrService {

    public String extrairTexto(String caminhoArquivo) {

        try {

            Tesseract tesseract = new Tesseract();


            tesseract.setDatapath("C:/Program Files/Tesseract-OCR/tessdata");


            tesseract.setLanguage("por");

            File file = new File(caminhoArquivo);


            if (!file.exists()) {
                throw new RuntimeException("Arquivo não encontrado: " + caminhoArquivo);
            }

            if (caminhoArquivo.toLowerCase().endsWith(".pdf")) {

                PDDocument document = PDDocument.load(file);
                PDFRenderer renderer = new PDFRenderer(document);

                StringBuilder textoFinal = new StringBuilder();

                for (int i = 0; i < document.getNumberOfPages(); i++) {

                    BufferedImage image = renderer.renderImageWithDPI(i, 300);

                    String textoPagina = tesseract.doOCR(image);
                    textoFinal.append(textoPagina).append("\n");
                }

                document.close();

                System.out.println("=== TEXTO OCR PDF ===");
                System.out.println(textoFinal);

                return textoFinal.toString();
            }


            String texto = tesseract.doOCR(file);

            System.out.println("=== TEXTO OCR IMAGEM ===");
            System.out.println(texto);

            return texto;

        } catch (TesseractException e) {
            throw new RuntimeException("Erro no OCR (Tesseract): " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar arquivo: " + e.getMessage());
        }
    }
}
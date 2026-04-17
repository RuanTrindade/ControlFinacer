package TCC.ControleFincanceiro.service;

import net.sourceforge.tess4j.Tesseract;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class OcrService {

    public String extrairTexto(String caminhoImagem) {

        try {

            Tesseract tesseract = new Tesseract();

            // 🔥 MUITO IMPORTANTE (confere esse caminho no seu PC)
            tesseract.setDatapath("C:/Program Files/Tesseract-OCR/tessdata");

            // idioma português
            tesseract.setLanguage("por");

            String texto = tesseract.doOCR(new File(caminhoImagem));

            System.out.println("=== TEXTO OCR ===");
            System.out.println(texto);

            return texto;

        } catch (Exception e) {
            throw new RuntimeException("Erro no OCR: " + e.getMessage());
        }
    }
}
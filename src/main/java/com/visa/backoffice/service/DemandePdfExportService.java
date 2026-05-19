package com.visa.backoffice.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.visa.backoffice.model.Demande;
import com.visa.backoffice.model.DemandePieceJustificative;
import com.visa.backoffice.model.Demandeur;
import com.visa.backoffice.model.PieceJustificative;
import com.visa.backoffice.model.StatusDemande;
import com.visa.backoffice.repository.DemandePieceJustificativeRepository;
import com.visa.backoffice.repository.DemandeRepository;
import com.visa.backoffice.repository.DemandeStatusHistoryRepository;
import com.visa.backoffice.repository.DemandeurRepository;
import com.visa.backoffice.repository.PieceJustificativeRepository;
import com.visa.backoffice.repository.StatusDemandeRepository;
import com.visa.backoffice.support.SystemPieceLabels;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DemandePdfExportService {

    private final DemandeRepository demandeRepository;
    private final DemandeurRepository demandeurRepository;
    private final DemandePieceJustificativeRepository demandePieceJustificativeRepository;
    private final PieceJustificativeRepository pieceJustificativeRepository;
    private final DemandeStatusHistoryRepository demandeStatusHistoryRepository;
    private final StatusDemandeRepository statusDemandeRepository;
    private final Path uploadRoot = Paths.get("uploads", "demandes");
    private final String frontendBaseUrl;

    public DemandePdfExportService(
            DemandeRepository demandeRepository,
            DemandeurRepository demandeurRepository,
            DemandePieceJustificativeRepository demandePieceJustificativeRepository,
            PieceJustificativeRepository pieceJustificativeRepository,
            DemandeStatusHistoryRepository demandeStatusHistoryRepository,
            StatusDemandeRepository statusDemandeRepository,
            @Value("${app.frontend-base-url:http://localhost:4200}") String frontendBaseUrl) {
        this.demandeRepository = demandeRepository;
        this.demandeurRepository = demandeurRepository;
        this.demandePieceJustificativeRepository = demandePieceJustificativeRepository;
        this.pieceJustificativeRepository = pieceJustificativeRepository;
        this.demandeStatusHistoryRepository = demandeStatusHistoryRepository;
        this.statusDemandeRepository = statusDemandeRepository;
        this.frontendBaseUrl = frontendBaseUrl == null ? "http://localhost:4200" : frontendBaseUrl.trim();
    }

    public byte[] buildPiecesPreviewPdf(long demandeId) throws IOException {
        Demande demande = demandeRepository.findById((int) demandeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable"));

        List<DemandePieceJustificative> ordered =
                demandePieceJustificativeRepository.findByIdDemandeOrderByIdDemandePieceJustificativeAsc(demande.getIdDemande().longValue());

        List<Path> mediaFiles = new ArrayList<>();
        for (DemandePieceJustificative dpj : ordered) {
            Long pieceId = dpj.getIdPieceJustificative();
            if (pieceId == null) {
                continue;
            }
            for (String name : splitFiles(dpj.getPhotoPieceJustificative())) {
                Path resolved = resolvePieceFile(demandeId, pieceId, name);
                if (resolved != null && Files.isRegularFile(resolved)) {
                    mediaFiles.add(resolved);
                }
            }
        }

        if (mediaFiles.isEmpty()) {
            return singlePageTextPdf("Aucune pièce justificative à afficher.");
        }

        List<byte[]> singlePagePdfs = new ArrayList<>();
        for (Path path : mediaFiles) {
            String lower = path.getFileName().toString().toLowerCase(Locale.ROOT);
            if (lower.endsWith(".pdf")) {
                singlePagePdfs.add(Files.readAllBytes(path));
            } else if (isImageExtension(lower)) {
                byte[] imgBytes = Files.readAllBytes(path);
                singlePagePdfs.add(imageToPdfBytes(imgBytes, lower));
            }
        }

        if (singlePagePdfs.isEmpty()) {
            return singlePageTextPdf("Aucun format pris en charge pour l'aperçu.");
        }

        return mergePdfBytes(singlePagePdfs);
    }

    public byte[] buildLettreRecuPdf(long demandeId) throws IOException {
        Demande demande = demandeRepository.findById((int) demandeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable"));

        if (!isLatestStatusScanTermine(demande)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Lettre disponible uniquement après scan terminé.");
        }

        Demandeur demandeur = demandeurRepository.findById(demande.getIdDemandeur().longValue())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demandeur introuvable"));

        String qrUrl = buildFrontendQrUrl(demandeId);
        byte[] qrPng = renderQrPng(qrUrl, 220);

        BufferedImage photoImage = loadPhotoIdentiteImage(demandeId);

        try (PDDocument doc = new PDDocument()) {
            PDType1Font fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font fontBody = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            PDType1Font fontCaption = new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE);

            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            float margin = 48;
            float y = page.getMediaBox().getHeight() - margin;
            float width = page.getMediaBox().getWidth() - 2 * margin;

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                cs.beginText();
                cs.setFont(fontBold, 14);
                cs.newLineAtOffset(margin, y);
                cs.showText("Lettre de réception de dossier");
                cs.endText();

                y -= 28;
                cs.beginText();
                cs.setFont(fontBody, 11);
                cs.newLineAtOffset(margin, y);
                String ref = demande.getNumeroDemande() != null ? demande.getNumeroDemande() : "DEM-" + demande.getIdDemande();
                cs.showText("Référence demande : " + ref);
                cs.endText();

                y -= 18;
                cs.beginText();
                cs.newLineAtOffset(margin, y);
                cs.showText("Date : " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
                cs.endText();

                y -= 28;
                float phReserve = 120;
                float pwReserve = phReserve * 0.72f;
                float bodyColWidth = photoImage != null ? width - pwReserve - 24 : width;
                String body =
                        "Madame, Monsieur,\n\n"
                                + "Nous accusons réception du dossier de demande de visa déposé par "
                                + safe(demandeur.getPrenom()) + " " + safe(demandeur.getNom()) + ".\n"
                                + "Votre demande est enregistrée sous la référence ci-dessus et fera l'objet d'un traitement administratif.\n\n"
                                + "Vous pouvez suivre l'état d'avancement en scannant le QR code ci-dessous.\n\n"
                                + "Cordialement,\nLe service des visas.";
                y = drawMultiline(cs, fontBody, margin, y, bodyColWidth, body, 12);

                float pageW = page.getMediaBox().getWidth();
                float ph = phReserve;
                float pw = pwReserve;
                float qrSize = 96;

                if (qrPng.length > 0) {
                    y -= 20;
                    cs.beginText();
                    cs.setFont(fontCaption, 9);
                    cs.newLineAtOffset(margin, y);
                    cs.showText("QR code de suivi de la demande :");
                    cs.endText();
                    y -= 14;
                    float qrBottom = y - qrSize;
                    PDImageXObject qrImg = PDImageXObject.createFromByteArray(doc, qrPng, "qr");
                    cs.drawImage(qrImg, margin, qrBottom, qrSize, qrSize);
                    y = qrBottom - 8;
                }

                if (photoImage != null) {
                    PDImageXObject photoImg = LosslessFactory.createFromImage(doc, photoImage);
                    float photoX = pageW - margin - pw;
                    float photoBottom = margin + 8;
                    cs.drawImage(photoImg, photoX, photoBottom, pw, ph);
                    cs.beginText();
                    cs.setFont(fontCaption, 9);
                    cs.newLineAtOffset(photoX, photoBottom + ph + 4);
                    cs.showText("Photo d'identité");
                    cs.endText();
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.save(baos);
            return baos.toByteArray();
        }
    }

    private BufferedImage loadPhotoIdentiteImage(long demandeId) throws IOException {
        PieceJustificative photoPiece = pieceJustificativeRepository
                .findFirstByPieceJustificativeIgnoreCase(SystemPieceLabels.PHOTO_IDENTITE)
                .orElse(null);
        if (photoPiece == null) {
            return null;
        }
        DemandePieceJustificative dpj = demandePieceJustificativeRepository
                .findFirstByIdDemandeAndIdPieceJustificative(demandeId, photoPiece.getIdPieceJustificative())
                .orElse(null);
        if (dpj == null) {
            return null;
        }
        List<String> files = splitFiles(dpj.getPhotoPieceJustificative());
        if (files.isEmpty()) {
            return null;
        }
        Path path = resolvePieceFile(demandeId, photoPiece.getIdPieceJustificative(), files.get(0));
        if (path == null || !Files.isRegularFile(path)) {
            return null;
        }
        String lower = path.getFileName().toString().toLowerCase(Locale.ROOT);
        if (lower.endsWith(".pdf")) {
            return null;
        }
        if (!isImageExtension(lower)) {
            return null;
        }
        return ImageIO.read(path.toFile());
    }

    private float drawMultiline(PDPageContentStream cs, PDType1Font font, float x, float y, float maxWidth, String text, float fontSize)
            throws IOException {
        cs.setFont(font, fontSize);
        float lineHeight = fontSize + 4;
        for (String line : text.split("\n")) {
            for (String wrapped : wrapLine(line, maxWidth, fontSize)) {
                cs.beginText();
                cs.newLineAtOffset(x, y);
                cs.showText(wrapped.isEmpty() ? " " : wrapped);
                cs.endText();
                y -= lineHeight;
            }
        }
        return y;
    }

    private List<String> wrapLine(String line, float maxWidth, float fontSize) {
        List<String> out = new ArrayList<>();
        if (line.isEmpty()) {
            out.add("");
            return out;
        }
        float charW = fontSize * 0.5f;
        int maxChars = Math.max(10, (int) (maxWidth / charW));
        int i = 0;
        while (i < line.length()) {
            int end = Math.min(line.length(), i + maxChars);
            out.add(line.substring(i, end));
            i = end;
        }
        return out;
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    private boolean isLatestStatusScanTermine(Demande demande) {
        Map<Integer, StatusDemande> statuts = statusDemandeRepository.findAll().stream()
                .collect(Collectors.toMap(StatusDemande::getIdStatus, Function.identity()));
        StatusDemande latest = demandeStatusHistoryRepository
                .findFirstByIdDemandeOrderByDateChangementStatusDesc(demande.getIdDemande().longValue())
                .map(h -> statuts.get(h.getIdStatus()))
                .orElse(null);
        if (latest == null || latest.getStatus() == null) {
            return false;
        }
        String n = normalize(latest.getStatus());
        return n.equals("scan termine");
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        return java.text.Normalizer.normalize(value, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .trim()
                .toLowerCase(Locale.ROOT);
    }

    private String buildFrontendQrUrl(long demandeId) {
        String base = frontendBaseUrl.endsWith("/") ? frontendBaseUrl.substring(0, frontendBaseUrl.length() - 1) : frontendBaseUrl;
        return base + "/qr/" + demandeId;
    }

    private byte[] renderQrPng(String data, int size) {
        try {
            BitMatrix matrix = new MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, size, size);
            BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", baos);
            return baos.toByteArray();
        } catch (Exception e) {
            return new byte[0];
        }
    }

    private byte[] singlePageTextPdf(String message) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                cs.beginText();
                cs.setFont(font, 12);
                cs.newLineAtOffset(72, page.getMediaBox().getHeight() - 100);
                cs.showText(message.length() > 120 ? message.substring(0, 120) : message);
                cs.endText();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.save(baos);
            return baos.toByteArray();
        }
    }

    private byte[] mergePdfBytes(List<byte[]> parts) throws IOException {
        PDFMergerUtility merger = new PDFMergerUtility();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        merger.setDestinationStream(out);
        for (byte[] part : parts) {
            merger.addSource(new RandomAccessReadBuffer(part));
        }
        merger.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly().streamCache);
        return out.toByteArray();
    }

    private byte[] imageToPdfBytes(byte[] imageBytes, String lowerName) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            BufferedImage bim = ImageIO.read(new ByteArrayInputStream(imageBytes));
            if (bim == null) {
                return singlePageTextPdf("Image illisible.");
            }
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            PDImageXObject pdImage = lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg")
                    ? JPEGFactory.createFromImage(doc, bim)
                    : LosslessFactory.createFromImage(doc, bim);

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                PDRectangle box = page.getMediaBox();
                float maxW = box.getWidth() - 72;
                float maxH = box.getHeight() - 72;
                float iw = pdImage.getWidth();
                float ih = pdImage.getHeight();
                float scale = Math.min(maxW / iw, maxH / ih);
                float w = iw * scale;
                float h = ih * scale;
                float x = (box.getWidth() - w) / 2;
                float y = (box.getHeight() - h) / 2;
                cs.drawImage(pdImage, x, y, w, h);
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.save(baos);
            return baos.toByteArray();
        }
    }

    private boolean isImageExtension(String lower) {
        return lower.endsWith(".png")
                || lower.endsWith(".jpg")
                || lower.endsWith(".jpeg")
                || lower.endsWith(".gif")
                || lower.endsWith(".webp")
                || lower.endsWith(".bmp");
    }

    private List<String> splitFiles(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        List<String> list = new ArrayList<>();
        for (String s : value.split(";")) {
            String t = s.trim();
            if (!t.isEmpty()) {
                list.add(t);
            }
        }
        return list;
    }

    /**
     * Fichier dans le dossier pièce, ou à l'emplacement legacy {@code uploads/demandes/{id}/demandeur/}
     * pour les anciennes données migrées.
     */
    public Path resolvePieceFile(long demandeId, long pieceId, String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return null;
        }
        String safeName = Paths.get(fileName).getFileName().toString();
        Path pieceDir = uploadRoot.resolve(String.valueOf(demandeId)).resolve(String.valueOf(pieceId)).normalize();
        Path inPiece = pieceDir.resolve(safeName).normalize();
        if (inPiece.startsWith(pieceDir) && Files.isRegularFile(inPiece)) {
            return inPiece;
        }
        PieceJustificative pj = pieceJustificativeRepository.findById(pieceId).orElse(null);
        if (pj != null && SystemPieceLabels.isSystemPiece(pj.getPieceJustificative())) {
            Path legacyDir = uploadRoot.resolve(String.valueOf(demandeId)).resolve("demandeur").normalize();
            Path legacy = legacyDir.resolve(safeName).normalize();
            if (legacy.startsWith(legacyDir) && Files.isRegularFile(legacy)) {
                return legacy;
            }
        }
        return inPiece.startsWith(pieceDir) ? inPiece : null;
    }
}

package com.visa.backoffice.support;

/**
 * Libellés des pièces système (catalogue {@code piece_justificative}), utilisés pour la photo
 * d'identité et la signature stockées dans {@code demande_piece_justificative}.
 */
public final class SystemPieceLabels {

    public static final String PHOTO_IDENTITE = "Photo d'identité";
    public static final String SIGNATURE_DIGITALE = "Signature digitale";

    private SystemPieceLabels() {
    }

    public static boolean isPhotoIdentite(String pieceLabel) {
        return PHOTO_IDENTITE.equalsIgnoreCase(trim(pieceLabel));
    }

    public static boolean isSignatureDigitale(String pieceLabel) {
        return SIGNATURE_DIGITALE.equalsIgnoreCase(trim(pieceLabel));
    }

    public static boolean isSystemPiece(String pieceLabel) {
        return isPhotoIdentite(pieceLabel) || isSignatureDigitale(pieceLabel);
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }
}

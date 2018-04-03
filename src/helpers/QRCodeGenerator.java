package helpers;

import application.MainApp;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

/**
 * This class contains the methods responsible for creating a QR code from an input String
 *
 * It is used to create the QR code that is displayed on a customer's ticket and can be scanned
 * with a QR code scanner to verify the authenticity of the ticket.
 *
 * Source: http://crunchify.com/java-simple-qr-code-generator-example/
 */
public class QRCodeGenerator {

	/**
	 * This method sets up the creation of the QR code with createQRImage
	 *
	 * Source: http://crunchify.com/java-simple-qr-code-generator-example/
	 *
	 * @param QRString the String that will be used to generate the code
	 * @throws WriterException
	 * @throws IOException
	 */
        public static void createQRDetails(String QRString) throws WriterException, IOException {
        	// Defines the file path where the QR code image will be saved to
            String filePath = new File("").getAbsolutePath().concat("/img/qrcode.png");
            // Sets the size of the QR code
            int size = 125;
            // Sets the file type of the QR code image
            String fileType = "png";
            File qrFile = new File(filePath);
            // Creates the QR code
            createQRImage(qrFile, QRString, size, fileType);
	        MainApp.LOGGER.fine("Created QR Details");
        }


	/**
	 * Creates a QR code image from the supplied parameters
	 *
	 * Source: http://crunchify.com/java-simple-qr-code-generator-example/
	 *
	 * @param qrFile The File the QR code will be saved into
	 * @param qrCodeText the text that is encoded in the QR code
	 * @param size the pixel size of the created QR code
	 * @param fileType the file type of the created image
	 * @throws WriterException
	 * @throws IOException
	 */
	private static void createQRImage(File qrFile, String qrCodeText, int size,
                                          String fileType) throws WriterException, IOException {
        // Creates the ByteMatrix for the QR-Code that encodes the given String
        Hashtable hintMap = new Hashtable();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix byteMatrix = qrCodeWriter.encode(qrCodeText,
                BarcodeFormat.QR_CODE, size, size, hintMap);
        // Makes the BufferedImage that are to hold the QRCode
        int matrixWidth = byteMatrix.getWidth();
        BufferedImage image = new BufferedImage(matrixWidth, matrixWidth,
                BufferedImage.TYPE_INT_RGB);
        image.createGraphics();

        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, matrixWidth, matrixWidth);
        // Paints and saves the image using the ByteMatrix
        graphics.setColor(Color.BLACK);

        for (int i = 0; i < matrixWidth; i++) {
            for (int j = 0; j < matrixWidth; j++) {
                if (byteMatrix.get(i, j)) {
                    graphics.fillRect(i, j, 1, 1);
                }
            }
        }
        ImageIO.write(image, fileType, qrFile);
        MainApp.LOGGER.fine("Created QR Image");
	}
}

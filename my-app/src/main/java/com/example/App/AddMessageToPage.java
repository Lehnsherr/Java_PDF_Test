package com.example.App;

import java.io.File;
import java.io.IOException;

//Verwendetes PDF Package:
//https://pdfbox.apache.org/ 
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.util.Matrix;

/**
 * Beispiel stammt von :
 * https://svn.apache.org/viewvc/pdfbox/trunk/examples/src/main/java/org/apache/pdfbox/examples/pdmodel/AddMessageToEachPage.java?revision=1792647&view=markup
 */
public class AddMessageToPage {
    /**
     * Constructor.
     */
    public AddMessageToPage() {
        super();
    }

    /**
     * Main Function
     */
    public static void main(String[] args) throws IOException {
        System.out.println("Function AddMessageToPage \n Add text:" + args[1] +" on page:" + args[2]+ " to file:"+ args[0] +" and create file:"+ args[3] );
        
        AddMessageToPage app = new AddMessageToPage();

        if (args.length != 4) {
            app.usage();
        } else {
            app.addTextToPage(args[0], args[1], Integer.parseInt(args[2]), args[3]);
        }
    }

    /**
     * Funktion zum hinzufügen von Text in einem PDF (Erstellt ein neues PDF)
     * String file: Name der Input-PDF
     * String message: text der hinzugefügt werden soll
     * int pageNum: Seite der Input-PDF die erweitert werden soll
     * String outfile: Name der Output-PDF 
     */
    public void addTextToPage(String file, String message, int pageNum, String outfile) throws IOException {
        try (PDDocument doc = PDDocument.load(new File(file))) {
            PDFont font = PDType1Font.HELVETICA_BOLD;
            float fontSize = 36.0f;

            // Test ob Seite in PDF vorhanden
            if(pageNum > doc.getPages().getCount()){
                System.err.println("page not found");
            }else{
                //System.out.println(doc.getPages().getCount());
                //System.out.println(pageNum);

                //System.out.println(doc.getPages().get(pageNum-1));
                PDPage page = doc.getPages().get(pageNum-1);
                //System.out.println(page);
                PDRectangle pageSize = page.getMediaBox();
                float stringWidth = font.getStringWidth(message) * fontSize / 1000f;
                // calculate to center of the page
                int rotation = page.getRotation();
                boolean rotate = rotation == 90 || rotation == 270;
                float pageWidth = rotate ? pageSize.getHeight() : pageSize.getWidth();
                float pageHeight = rotate ? pageSize.getWidth() : pageSize.getHeight();
                float centerX = rotate ? pageHeight / 2f : (pageWidth - stringWidth*2) / 2f;
                float centerY = rotate ? (pageWidth - stringWidth*2) / 2f : pageHeight / pageHeight / 2f;
                // append the content to the existing stream
                try (PDPageContentStream contentStream = new PDPageContentStream(doc, page, AppendMode.APPEND, true, true)) {
                    contentStream.beginText();
                    // set font and font size
                    contentStream.setFont(font, fontSize);
                    // set text color to red
                    contentStream.setNonStrokingColor(255, 0, 0);
                    if (rotate) {
                        // rotate the text according to the page rotation
                        contentStream.setTextMatrix(Matrix.getRotateInstance(Math.PI / 2, centerX, centerY));
                    } else {
                        contentStream.setTextMatrix(Matrix.getTranslateInstance(centerX, centerY));
                    }
                    contentStream.showText(message);
                    contentStream.endText();
                }
                doc.save(outfile);
            }
        }
    }

    /**
     * This will print out a message telling how to use this example.
     */
    private void usage() {
        System.err.println("usage: " + this.getClass().getName() + " <input-file> <message> <page> <output-file>");
    }

    
}
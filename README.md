# LibPdfStamper
   PDF Sepecification: >= V1.4

   This is an useful and simple library(Java/jar) for users to add png(s) into PDF pages as visible watermark/stamps.
   It can also be used in Android apps.
   If you want to add text to image, you can try to convert text to png file, then try this way.
   Anyway, text to png is some easy.
   
   Because it is based on buffered io stream operation, so you don't worry about the OOM issue.
   * Such as the PdfiumAndroid lib, which will load total pdf files into memory in some cases, 
   * And cause OOM issue due to large size pdf file. see https://github.com/mshockwave/PdfiumAndroid 
   
## Image stamp/watermark mechanism
   Based on Adobe PDF reference: https://www.adobe.com/content/dam/acom/en/devnet/acrobat/pdfs/pdf_reference_1-7.pdf
   * plz see: No 3.4 File Structure
   * plz see: No 4.8 Images, about the image Xobject.
   * plz see: No 4.8 No 3.4.5 Incremental Updates
   And the most important is No 3.4.5 Incremental Updates, which told you how to append an XObject at the end of the PDF fils.
   This is an new feature since PDFv1.4.
   
 
# Simple Usage
```
private static void addMultiWM() {
		String srcPath = "resource\\source.pdf";
		String outPath = "resource\\resultm.pdf";
		PDFReader reader = null;
		PDFMultiWatermark watermarker = null;
		try {
			reader = new PDFReader(srcPath);

			// image 1 
			PDFImage image = PNGImage.getImage("resource\\p.png");
			WMarkImage wm1 = new WMarkImage();
			wm1.image = image;
			wm1.pageMode = PageMode.ALL; // All page
			wm1.posMode = PositionMode.MID;
			wm1.rotateDegree = 300;
			
			// image 2
			PDFImage image2 = PNGImage.getImage("resource\\q.png");
			WMarkImage wm2 = new WMarkImage();
			wm2.image = image2;
			wm2.pageMode = PageMode.FIRST; // just into first page
			wm2.posMode = PositionMode.MID;
			wm2.rotateDegree = 45;

			watermarker = new PDFMultiWatermark(reader, new File(outPath), wm1, wm2);
			watermarker.appendWatermark();
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (watermarker != null) {
				watermarker.close();
			}
		}
		
	}
```   


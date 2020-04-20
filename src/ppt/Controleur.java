package ppt;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.poi.util.IOUtils;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFConnectorShape;
import org.apache.poi.xslf.usermodel.XSLFPictureData;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTable;
import org.apache.poi.xslf.usermodel.XSLFTableCell;
import org.apache.poi.xslf.usermodel.XSLFTableRow;
import org.apache.poi.xslf.usermodel.XSLFTextBox;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;

public class Controleur {

	public static List<Slide> initSlides(String source) throws IOException {
		// TODO : nom du fichier a lire a prendre en lignes de commande et verifier que
		// c'est un pptx
		List<Slide> resSlides = new ArrayList<Slide>();
		XMLSlideShow ppt = new XMLSlideShow(new FileInputStream(source));
		Dimension pgsize = ppt.getPageSize();

		List<XSLFSlide> slides = ppt.getSlides();
		for (XSLFSlide s : slides) {
			List<String> notes;
			List<Element> elements;
			byte[] background;
			elements = getElements(s);
			notes = getNotes(s);
			background = getBackground(s, pgsize);
			resSlides.add(new Slide(s.getSlideNumber(), pgsize, notes, elements, background));
		}
		ppt.close();
		return resSlides;
	}

	// notes de bas de slide
	public static List<String> getNotes(XSLFSlide slide) throws IOException {
		List<String> res = new ArrayList<>();
		try {
			List<List<XSLFTextParagraph>> txt = slide.getNotes().getTextParagraphs();
			for (List<XSLFTextParagraph> t : txt) {
				for (XSLFTextParagraph tt : t) {
					res.add(tt.getText());
				}
			}
			res.remove(res.size() - 1); // enleve dernier elements qui est nb de page
		} catch (NullPointerException e) {
			// pas de notes dans la diapo

		}
		return res;
	}

	// elements de slide
	public static List<Element> getElements(XSLFSlide slide) throws IOException {
		List<Element> res = new ArrayList<>();
		List<XSLFShape> elements = slide.getShapes();
		for (XSLFShape e : elements) {
			double posX = e.getAnchor().getX();
			double posY = e.getAnchor().getY();
			double width = e.getAnchor().getWidth();
			double height = e.getAnchor().getHeight();
			Dimension size = new Dimension();
			size.setSize(width, height);

			if (e instanceof XSLFConnectorShape) {
				XSLFConnectorShape cs = (XSLFConnectorShape) e;
				Color fc = cs.getLineColor();
				if (fc == null)
					fc = new Color(-1);
				res.add(new Connecteur(posX, posY, String.format("#%06x", Integer.valueOf(fc.getRGB() & 0x00FFFFFF)),
						cs.getFlipHorizontal(), cs.getFlipVertical(), size, cs.getLineWidth()));
			}

			// si element est une image
			if (e instanceof XSLFPictureShape) {
				String checkIfPic = null;
				XSLFPictureShape p = (XSLFPictureShape) e;
				// check xml pour voir si media est video ou audio
				checkIfPic = checkXML4Media(p.getXmlObject());
				if (checkIfPic == null) {
					XSLFPictureData data = p.getPictureData();
					String ext = data.suggestFileExtension();
					byte[] data1 = p.getPictureData().getData();
					res.add(new Image(posX, posY, data1, ext, size));
				} else
					res.add(getVideoOrAudio(checkIfPic, slide.getSlideNumber(), posX, posY, size));
			}
			if (e instanceof XSLFTable) {
				res.add(makeTable((XSLFTable) e, posX, posY, size));
			}
			// si element est du texte
			if (e instanceof XSLFTextShape) {
				XSLFTextShape t = (XSLFTextShape) e;
				// pour les paragraphes
				List<XSLFTextParagraph> tp = t.getTextParagraphs();
				Color fc = t.getFillColor();
				if (fc == null)
					fc = new Color(-1);
				if (e instanceof XSLFTextBox) {
					XSLFTextBox tb = (XSLFTextBox) e;
					Color bc = tb.getLineColor();
					if (bc == null)
						bc = new Color(-1);
					res.add(new TexteBox(posX, posY, tp,
							String.format("#%06x", Integer.valueOf(fc.getRGB() & 0x00FFFFFF)), t.getVerticalAlignment(),
							size, String.format("#%06x", Integer.valueOf(bc.getRGB() & 0x00FFFFFF))));
				} else
					res.add(new Texte(posX, posY, tp, String.format("#%06x", Integer.valueOf(fc.getRGB() & 0x00FFFFFF)),
							t.getVerticalAlignment(), size));
			}
		}
		return res;
	}

	private static Table makeTable(XSLFTable tab, double tabposX, double tabposY, Dimension tabsize) {
		Table resTable = new Table(tabposX, tabposY, tabsize);
		List<XSLFTableRow> tablerows = tab.getRows();
		for (XSLFTableRow row : tablerows) {
			List<XSLFTableCell> tablecells = row.getCells();
			TableRow resRow = new TableRow(row.getHeight());
			double posX = 0, posY = 0, width = 0, height = 0;
			Dimension size = new Dimension();

			for (XSLFTableCell cell : tablecells) {
				try {
					posX = cell.getAnchor().getX();
					posY = cell.getAnchor().getY();
					width = cell.getAnchor().getWidth();
					height = cell.getAnchor().getHeight();
					size.setSize(width, height);
				}

				catch (Exception e) {
				}
				List<XSLFTextParagraph> tp = cell.getTextParagraphs();
				Color fc = cell.getFillColor();
				if (fc == null)
					fc = new Color(-1);
				Color bc = cell.getLineColor();
				if (bc == null)
					bc = new Color(-1);
				resRow.cells.add(new TexteBox(posX, posY, tp,
						String.format("#%06x", Integer.valueOf(fc.getRGB() & 0x00FFFFFF)), cell.getVerticalAlignment(),
						size, String.format("#%06x", Integer.valueOf(fc.getRGB() & 0x00FFFFFF))));
			}
			resTable.tablerows.add(resRow);
		}
		return resTable;

	}

	// renvoit un element video ou audio a partir de la lecture directe de xml
	private static Element getVideoOrAudio(String ref, int slideNb, double posX, double posY, Dimension size)
			throws IOException {
		try {
			XmlObject xml = XmlObject.Factory.parse(new File("temp/ppt/slides/_rels/slide" + slideNb + ".xml.rels"));
			XmlCursor cursor = xml.newCursor();
			cursor.toFirstChild();
			cursor.push();
			cursor.toFirstChild();
			boolean end = false;
			while (!end) {
				XmlCursor att = cursor.newCursor();
				att.toFirstAttribute();
				if (att.getTextValue().equals(ref)) {
					att.toNextAttribute();
					att.toNextAttribute();
					int index = att.getTextValue().lastIndexOf('/');
					String res = att.getTextValue().substring(index);
					return getMediaObject(res, posX, posY, size);
				}
				if (!cursor.toNextSibling())
					end = true;
			}

			cursor.dispose();
		} catch (XmlException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static Element getMediaObject(String nomFichier, double posX, double posY, Dimension size)
			throws FileNotFoundException, IOException {
		String mime = getMimeType(nomFichier);
		File fichierMedia = new File("temp/ppt/media" + nomFichier);
		FileInputStream fileInputStream = new FileInputStream(fichierMedia);
		byte[] data = IOUtils.toByteArray(fileInputStream);
		int index = mime.lastIndexOf('/');
		String type = mime.substring(0, index);
		String extension = mime.substring(index + 1);
		fileInputStream.close();
		if (type.equalsIgnoreCase("video"))
			return new Video(posX, posY, data, extension, size);
		else if (type.equalsIgnoreCase("audio"))
			return new Audio(posX, posY, data, extension, size);
		return null;
	}

	private static String getMimeType(String fileUrl) {
		String extension = MimeTypeMap.getFileExtensionFromUrl(fileUrl);
		return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
	}

	private static String checkXML4Media(XmlObject xml) {
		String xqNamespace = "declare namespace p14='http://schemas.microsoft.com/office/powerpoint/2010/main';";
		XmlCursor cursor = xml.newCursor();
		cursor.toFirstChild();
		cursor.push();
		cursor.selectPath(xqNamespace + "$this//p14:media");
		if (cursor.toNextSelection()) {
			cursor.toFirstAttribute();
			return cursor.getTextValue();
		}
		cursor.dispose();
		return null;
	}

	// arriere-plans
	public static byte[] getBackground(XSLFSlide slide, Dimension pgsize) throws IOException {

		// getting the dimensions and size of the slide

		BufferedImage img = new BufferedImage(pgsize.width, pgsize.height, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = img.createGraphics();

		// clear the drawing area
		graphics.setPaint(Color.white);
		graphics.fill(new Rectangle2D.Float(0, 0, pgsize.width, pgsize.height));

		slide.getShapes().clear();
		slide.draw(graphics);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		javax.imageio.ImageIO.write(img, "png", out);
		byte[] res = out.toByteArray();
		out.close();
		return res;
	}

	// source = https://www.journaldev.com/830/java-delete-file-directory
	public static void deleteDirectory(String chemin) throws IOException {
		Path directory = Paths.get(chemin);
		Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
				Files.delete(file); // this will work because it's always a File
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				Files.delete(dir); // this will work because Files in the directory are already deleted
				return FileVisitResult.CONTINUE;
			}
		});
	}

	public static void disableWarning() {
		System.err.close();
		System.setErr(System.out);
	}

	public static boolean valideFile(String filename) {
		String extension = "";

		int i = filename.lastIndexOf('.');
		if (i > 0) {
			extension = filename.substring(i + 1);
		} else
			return false;
		if (!extension.equalsIgnoreCase("pptx")) {
			return false;
		}
		return true;
	}

	public static String getProjectName(String filename) {
		int i1 = filename.lastIndexOf("\\");
		int i2 = filename.lastIndexOf("/");
		int res;
		if (i1 >= i2)
			res = i1;
		else
			res = i2;
		if (res != -1)
			;
		filename = filename.substring(res + 1);
		res = filename.lastIndexOf('.');
		return filename.substring(0, res);
	}

	public static void main(String args[]) throws IOException {
		disableWarning(); // pour enlever le warning de POI apache (ne correspond pas a une erreur dans
							// l'algorithme)
		if (args.length != 1) {

			System.out.println("Invalid arguments! Usage is: java PTTX2HTML <filename>.pptx");
			System.exit(0);
		}
		if (!valideFile(args[0])) {
			System.out.println("Invalid arguments! File must have .pptx extension" + "");
			System.exit(0);
		}
		System.out.println("Reading pptx...");
		Zip.decompress(args[0], "temp");
		List<Slide> slides = initSlides(args[0]);
		deleteDirectory("temp");
		System.out.println("Exporting html to \"exportHTML\" folder...");

		String pname = getProjectName(args[0]);
		ExportHTML.export2HTML(slides, pname);
		System.out.println("Done!");

	}
}

package ppt;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.sl.usermodel.PaintStyle.SolidPaint;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOuterShadowEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTRegularTextRun;

public class ExportHTML {
	static String javascript;
	static String css;

	// source : https://www.javatpoint.com/how-to-read-file-line-by-line-in-java
	public static String initResources(String filepath) throws IOException {
		File file = new File(filepath); // creates a new file instance
		FileReader fr = new FileReader(file); // reads the file
		BufferedReader br = new BufferedReader(fr); // creates a buffering character input stream
		StringBuffer sb = new StringBuffer(); // constructs a string buffer with no characters
		sb.append("\n");
		String line;
		while ((line = br.readLine()) != null) {
			sb.append(line); // appends line to string buffer
			sb.append("\n"); //  line feed
		}
		fr.close(); // closes the stream and release the resources
		return sb.toString();
	}

	public static void export2HTML(List<Slide> slides, String projectname) throws IOException {
		File file = new File("exportHTML/" + projectname);
		file.mkdirs();
		file = new File("exportHTML/" + projectname + "/media");
		file.mkdir();
		List<Media> media = new ArrayList<>();
		for (Slide s : slides) {
			media.addAll(s.getMedia());
		}
		if (media.size() > 0)
			exportMedia(media, projectname);
		file = new File("exportHTML/" + projectname + "/index.html");
		String res = generateHTML(slides);
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.write(res);
		writer.close();
	}

	// exporte media
	private static void exportMedia(List<Media> media, String projectname) throws IOException {
		for (int i = 0; i < media.size(); i++) {
			File file = new File("exportHTML/" + projectname + "/media/media" + i + "." + media.get(i).getExtension());
			try {
				OutputStream os = new FileOutputStream(file);
				os.write(media.get(i).data);
				media.get(i).setFilename("media/media" + i + "." + media.get(i).getExtension());
				os.close();
			} catch (FileNotFoundException e) {
				System.out.println("Error : Couldn't export the media files from the pptx!");
			}
		}
	}

	public static String generateHTML(List<Slide> slides) throws IOException {
		css = "<style>" + initResources("resources/style.css") + "</style>";
		javascript = "<script>" + initResources("resources/script.js") + "</script>";

		String res = "<!DOCTYPE html>\r\n" + "<html>\r\n" + "<head>\r\n" + "    <meta charset=\"utf-8\" />        \r\n"
				+ css + javascript + "</head>\r\n" + "<body onload=\"init()\">\r\n"
				+ "        <!--Partie avec toutes les diapos du pptx-->";
		boolean first = true;
		for (Slide s : slides) {
			if (first) {
				res += "<div class=\"diapo\" style = \"background-image:url(data:image/png;base64," + s.toBase64()
						+ "); background-attachment:fixed; width:" + s.getSize().getWidth()
						+ "pt; height:" + s.getSize().getHeight() + "pt; \">";
				first = false;
			} else
				res += "<div class=\"diapo none\" style = \"background-image:url(data:image/png;base64," + s.toBase64()
						+ "); background-attachment:fixed; width:" + s.getSize().getWidth()
						+ "pt; height:" + s.getSize().getHeight() + "pt; \">";
			for (Element e : s.getElements()) {
				if (e instanceof Texte)
					res += "\n" + generateTexteHTML((Texte) e, false);
				if (e instanceof Image)
					res += "\n" + generateImageHTML((Image) e);
				if (e instanceof Video)
					res += "\n" + generateVideoHTML((Video) e);
				if (e instanceof Audio)
					res += "\n" + generateAudioHTML((Audio) e);
				if (e instanceof Table)
					res += "\n" + generateTableHTML((Table) e);
				if (e instanceof Connecteur)
					res += "\n" + generateConnecteurHTML((Connecteur) e);
			}
			res += "\n</div>";

		}
		res += "        <!--Barre de navigation dans les diapos-->\r\n" + "    <div class=\"nav\">\r\n"
				+ "            \r\n" + "            <button type=\"button\" onclick=\"precedent()\"><</button>\r\n"
				+ "            <button type=\"button\" onclick=\"suivant()\">></button>\r\n"
				+ "                <button type=\"button\" id=\"fullscreen\" onclick=\"fullScreen()\"><img src=\"data:image/png;base64,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\n" + 
				"\r\n"
				+ "\" alt=\"icone\" /><span></span></button> \r\n" + "                 \r\n"
				+ "            <input type=\"text\" id=\"numDiapo\" name=\"numero de diapo\" size=\"1\" onchange=\"changeDiapo()\">\r\n"
				+ "        </div> \r\n" + "    </body>\r\n" + "</html>";
		return res;
	}

	private static String generateConnecteurHTML(Connecteur cs) {
		String res = "";
		/*
		double cmHeight = cs.size.getHeight()*0.02646;
		double cmWidth = cs.size.getWidth()*0.02646;
		*/		
		double rotation = Math.atan((cs.size.getHeight()/cs.size.width))*45;
		if(cs.flipVertical)
			rotation *=-1;
		res += "<div style=\"left:" + cs.posX + "pt; border-bottom:"+cs.lineWidth*1.333333+"px solid "+cs.lineColor+"; position:absolute; top:" + cs.posY
				+ "pt; width:" + cs.size.getWidth() + "pt;   transform: rotate("+rotation+"deg);\"></div>";
		return res;
	}

	private static String generateTableHTML(Table tb) {
		String res = "";
		res += "<table style=\"left:" + tb.posX + "pt;  border: 1px solid black;\n"
				+ " position:absolute;border-collapse: collapse; top:" + tb.posY + "pt;" + "width:" + tb.size.getWidth()
				+ "pt;" + " height:" + tb.size.getHeight() + "pt;\">";
		for (TableRow tr : tb.tablerows) {
			res += "<tr style =\"height:" + tr.height + ";\">";
			for (TexteBox txt : tr.cells) {
				res += "<td style =\"border-spacing: 0px; padding : 0px; border: 1px solid black;\">";
				res += generateTexteHTML(txt, true);
				res += "</td>";
			}
			res += "</tr>";
		}
		res += "</table>";

		return res;
	}

	private static String generateAudioHTML(Audio ad) {
		String res = "";
		res += "<audio controls src=\"" + ad.filename + "\" style=\"left:" + ad.posX * 0.80
				+ "pt; position:absolute; top:" + ad.posY + "pt;\"> Your browser does not support audio elements :(\n"
				+ "    </audio>";
		return res;
	}

	private static String generateVideoHTML(Video vd) {
		String res = "";
		res += "<video controls width=\"" + vd.size.getWidth()* 1.3281472327365 + "\" height=\"" + vd.size.getHeight()* 1.3281472327365
				+ "\" style=\"left:" + vd.posX + "pt; position:absolute; top:" + vd.posY + "pt;\">\r\n" + "\r\n"
				+ "    <source src=\"" + vd.filename + "\"\r\n" + "type=\"video/" + vd.extension + "\">\r\n"
				+ "    Sorry, your browser doesn't support embedded videos :(\r\n" + "</video>";
		return res;
	}

	private static String generateImageHTML(Image img) {
		String res = "";
		res += "<img src=\"" + img.filename + "\" style=\"left:" + img.posX + "pt; position:absolute; top:" + img.posY
				+ "pt; width:" + img.size.getWidth() + "pt;       \r\n" + " height:" + img.size.getHeight() + "pt;\"/>";
		return res;
	}

	private static String generateTexteHTML(Texte txt, boolean table) {
		String res = "";
		int i = 0;
		List<Boolean> debListe = new ArrayList<>();
		debListe.add(false);
		List<XSLFTextParagraph> parag = txt.getParagraphes();
		res += "<div style=\"left:" + txt.posX + "pt; height:auto; top:" + txt.posY + "pt; width:" + txt.size.getWidth()
				+ "pt;" + " min-height:" + txt.size.getHeight() + "pt;\r\n" + " display: flex;\r\n"
				+ "  flex-direction: column;\r\n";
		if (txt instanceof TexteBox) {
			if (table)
				res += "border : 0px";
			res += "   border-style: solid; border-color:" + ((TexteBox) txt).getBorderColor() + ";";
			res += "  background-color: " + ((TexteBox) txt).getFillColor() + ";";
		}
		if (!table)
			res += "position:absolute;";

		if (txt.getVertical().toString().equals("TOP"))
			res += "  justify-content: flex-start;\r\n" + "\"> ";
		else if (txt.getVertical().toString().equals("MIDDLE"))
			res += "  justify-content: center;\r\n" + "\"> ";
		else
			res += "  justify-content: flex-end;\r\n" + "\"> ";
		for (XSLFTextParagraph tg : parag) {
			boolean bulletspace = false;
			int nvIndent = 0;
			List<XSLFTextRun> runs = tg.getTextRuns();
			if (runs.size() != 0) {
				if (tg.isBullet()) {
					bulletspace = true;
					nvIndent = tg.getIndentLevel();
					double indent = tg.getIndent();
					String resIndent;
					if (indent <= 0)
						 resIndent = indent * -1 + "pt;";
					else
						resIndent = indent + "%;";
					if (nvIndent > debListe.size() - 1) {
						res += "<ul style = \"margin-block-start: 0; margin-block-end: 0; padding-inline-start:"+resIndent+";\">";
						debListe.add(true);
					} else if (!debListe.get(nvIndent)) {
						res += "<ul style = \"margin-block-start: 0; margin-block-end: 0; padding-inline-start:"+resIndent+";\">";
						debListe.set(nvIndent, true);
					}
					res += "<li style=\"";
					if (bulletspace) {
						if (tg.getSpaceBefore() != null)
							res += "margin-top:" + paragSpacing(tg.getSpaceBefore());
						if (tg.getSpaceAfter() != null)
							res += "margin-bottom:" + paragSpacing(tg.getSpaceAfter());
						if (tg.getLineSpacing() != null)
							res += "line-height:" + paragSpacing(tg.getLineSpacing());
					}
					res+="\" >";

				}
				res += "\n<div class = \"parag\" style= \"text-align:" + tg.getTextAlign().toString() + ";";
				if (txt instanceof TexteBox) {
					res += "padding:" + 3.6 * 1.3281472327365 + "px " + 7.2 * 1.3281472327365 + "px "
							+ 3.6 * 1.3281472327365 + "px " + 7.2 * 1.3281472327365 + "px;";
				}
				res += "\">";
				for (XSLFTextRun tr : runs) {
					String shadow;
					
					res += "<span style = \"font-size:" + tr.getFontSize() + "pt;\r\n" + "    font-family:"
							+ tr.getFontFamily() + ";\r\n" + "    color :" + getColorHexa(tr) + ";";
					if (!bulletspace) {
						if (tg.getSpaceBefore() != null)
							res += "margin-top:" + paragSpacing(tg.getSpaceBefore());
						if (tg.getSpaceAfter() != null)
							res += "margin-bottom:" + paragSpacing(tg.getSpaceAfter());
						if (tg.getLineSpacing() != null)
							res += "line-height:" + paragSpacing(tg.getLineSpacing());
					}
					
					shadow = shadowFromTextRun(tr);
					if(shadow!=null)
						res +=shadow;
					if (tr.isBold())
						res += " font-weight: bold;";
					if (tr.isItalic())
						res += " font-style: italic;";
					if (tr.isUnderlined())
						res += "text-decoration: underline;";
					if (tr.isStrikethrough())
						res += "text-decoration: line-through;";
					res += "\">";
					if (tr.getRawText().equals("\n"))
						res += "<br/>";
					if (tr.getRawText().equals("\t"))
						res += "&nbsp;&nbsp;&nbsp;&nbsp;";
					else {
						String rawTxt = tr.getRawText();
						rawTxt = rawTxt.replaceAll("<", "&lt;");
						rawTxt = rawTxt.replaceAll(">", "&gt;");
						if (tr.getHyperlink() != null) {
							res += "<a href=" + tr.getHyperlink().getAddress() + ">" + rawTxt + "</a>\r\n" + "";
						} else
							res += rawTxt + "</span>";
					}
				}
				res += "\n</div>";

			} else {
				if (tg.isBullet()) {
					nvIndent = tg.getIndentLevel();
					if (nvIndent > debListe.size() - 1) {
						res += "<ul style = \"margin-block-start: 0; margin-block-end: 0; padding-inline-start:"+paragSpacing(tg.getIndent())+"\">";
						debListe.add(true);
					} else if (!debListe.get(nvIndent)) {
						res += "<ul style = \"margin-block-start: 0; margin-block-end: 0; padding-inline-start:"+paragSpacing(tg.getIndent())+"\">";
						debListe.set(nvIndent, true);
					}
					res += "<li style=\"list-style-type:none; padding-bottom: 18px;";
					if (tg.getSpaceBefore() != null)
						res += "margin-top:" + paragSpacing(tg.getSpaceBefore());
					if (tg.getSpaceAfter() != null)
						res += "margin-bottom:" + paragSpacing(tg.getSpaceAfter());
					if (tg.getLineSpacing() != null)
						res += "line-height:" + paragSpacing(tg.getLineSpacing());
					res += "\">";
				}
			}
			if (tg.isBullet()) {
				res += "</li>";
				if (i + 1 == parag.size()) {
					res += "</ul>";
					debListe.clear();
					debListe.add(false);
				} else if (parag.get(i + 1).isBullet()) {
					if (i + 1 == parag.size()) {
						res += "</ul>";
						debListe.clear();
						debListe.add(false);
					} else if (parag.get(i + 1).isBullet()) {
						while (parag.get(i + 1).getIndentLevel() < nvIndent) {
							res += "</ul>";
							debListe.set(nvIndent, false);
							nvIndent--;
						}
					}
				}
			}
			i++;
		}
		res += "\n</div>";

		return res;
	}
	
	private static String shadowFromTextRun(XSLFTextRun run) {
	    if (run.getXmlObject() instanceof CTRegularTextRun) {
	        CTRegularTextRun cTRun = (CTRegularTextRun) run.getXmlObject();
	        if (cTRun.getRPr() != null) {
	            if (cTRun.getRPr().getEffectLst() != null) {
	                if (cTRun.getRPr().getEffectLst().getOuterShdw() != null) {
	                	/*CTOuterShadowEffect shadow = cTRun.getRPr().getEffectLst().getOuterShdw();
	                	System.out.println(shadow.xmlText());
	                	if(shadow.isSetPrstClr())
	                	System.out.println(shadow.getPrstClr().getVal());
	                	*/
	                	return " text-shadow: 1px 1px 3px";
	                }
	            }
	        }
	    }
	    return null;
	}

	private static String paragSpacing(double val) {
		String res;
		if (val <= 0)
			res = Math.abs((val * 1.5)) + "pt;";
		else
			res = val*1.3 + "%;";
		return res;
	}

	private static String getColorHexa(XSLFTextRun tr) {
		Color c;
		try {
			c = ((SolidPaint) tr.getFontColor()).getSolidColor().getColor();
		} catch (NullPointerException ee) {
			c = new Color(-1);
		}
		if (c == null)
			c = new Color(-1);
		return  String.format("#%06x", Integer.valueOf(c.getRGB() & 0x00FFFFFF)); 
	}
}

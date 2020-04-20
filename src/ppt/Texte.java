package ppt;

import java.awt.Dimension;
import java.util.List;

import org.apache.poi.sl.usermodel.VerticalAlignment;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;

public class Texte extends Element {
	List<XSLFTextParagraph> paragraphes;
	// chaque paragraphe est constituer de XSLFTextRuns (voir methode)
	// qui correspondent a chaque bout de texte dans le paragraphe ayant des styles
	// differents
	// exemple : si un bout de texte est en italique et un autre ne l'est pas, ils
	// seront 2 textruns differents
	String fillColor;
	VerticalAlignment vertical;

	public Texte(double pX, double pY, List<XSLFTextParagraph> pg, String fc, VerticalAlignment v, Dimension sz) {
		super(pX, pY, sz);
		paragraphes = pg;
		size = sz;
		fillColor = fc;
		vertical = v;

	}

	public String getFillColor() {
		return fillColor;
	}

	public VerticalAlignment getVertical() {
		return vertical;
	}

	public List<XSLFTextParagraph> getParagraphes() {
		return paragraphes;
	}

	public void setParagraphes(List<XSLFTextParagraph> paragraphes) {
		this.paragraphes = paragraphes;
	}
}

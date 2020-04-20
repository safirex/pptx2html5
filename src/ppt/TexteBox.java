package ppt;

import java.awt.Dimension;
import java.util.List;

import org.apache.poi.sl.usermodel.VerticalAlignment;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;

public class TexteBox extends Texte{

	String borderColor;
	
	public TexteBox(double pX, double pY, List<XSLFTextParagraph> pg, String fc, VerticalAlignment v, Dimension sz, String bc) {
		super(pX, pY, pg, fc, v, sz);
		borderColor = bc;
	}
	
	public String getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(String borderColor) {
		this.borderColor = borderColor;
	}


}

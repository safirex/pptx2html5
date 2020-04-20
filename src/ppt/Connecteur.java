package ppt;

import java.awt.Dimension;

public class Connecteur extends Element {
	String lineColor;
	boolean flipVertical;
	boolean flipHorizontal;
	double lineWidth;
	double headWidth;

	public Connecteur(double pX, double pY, String lc, boolean fh, boolean fv, Dimension sz, double lw) {
		super(pX, pY, sz);
		size = sz;
		lineColor = lc;
		flipVertical = fv;
		flipHorizontal = fh;
		lineWidth = lw;
	}
	
}
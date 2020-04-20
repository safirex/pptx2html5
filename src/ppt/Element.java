package ppt;

import java.awt.Dimension;

public abstract class Element {
	double posX;
	double posY;
	Dimension size;


	public Element (double pX, double pY, Dimension sz) {
		posX = pX;
		posY = pY;
		size = sz;
	}
}

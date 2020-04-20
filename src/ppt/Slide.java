package ppt;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class Slide {
	int nDiapo; //numero de diapo
	Dimension size; //dimension de la diapo
	List<String> notes; //notes associees a la diapo
	List<Element> elements;
	byte[] background; //arriere-plan de la slide

	public Slide(int n,Dimension sz, List<String> nt, List<Element> el, byte[] bk) {
		nDiapo = n;
		size= sz;
		notes = nt;
		elements = el;
		background = bk;
	}
	
	
	public int getnDiapo() {
		return nDiapo;
	}


	public Dimension getSize() {
		return size;
	}


	public List<String> getNotes() {
		return notes;
	}


	public List<Element> getElements() {
		return elements;
	}


	public byte[] getBackground() {
		return background;
	}

	//a utiliser uniquement pour deboguer
	public String toString() {
		return "Slide = Dimension = "+size.toString()+", Note = "+notes.toString()+", Elements ="+elements.toString();
	}
	
	public String toBase64() {
		return new String(Base64.getEncoder().encode(background));
	}
	
	public List<Media> getMedia(){
		List<Media> res = new ArrayList<>();
		for(Element e : elements) {
			if(e instanceof Media)
				res.add((Media)e);
		}
		return res;
	}

}

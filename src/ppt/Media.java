package ppt;

import java.awt.Dimension;
import java.util.Base64;

public class Media extends Element {
	
	byte[] data;
	String extension;
	String filename; 

	public Media (double pX, double pY,byte[] dt,String ext,Dimension sz) {
		super(pX,pY, sz);
		data = dt;
		extension = ext;
		size = sz;
	}
	
	public String getExtension() {
		return extension;
	}
	
	public void setFilename(String fn) {
		filename=fn;
	}
	
	public String toBase64() {
		return new String(Base64.getEncoder().encode(data));
	}

	public void setExtension(String ext) {
		extension = ext;		
	}

}

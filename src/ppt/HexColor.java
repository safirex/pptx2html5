package ppt;

import java.awt.Color;

public class HexColor extends Color {
	  
	  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public HexColor(int r, int g, int b) {
		    super(r,g,b);
		  }
	  /**
	   * Returns the HEX value representing the colour in the default sRGB ColorModel.
	   *
	   * @return the HEX value of the colour in the default sRGB ColorModel
	   */
	  
	  public String getHex() {
	    return toHex(getRed(), getGreen(), getBlue());
	  }

	  /**
	   * Returns a web browser-friendly HEX value representing the colour in the default sRGB
	   * ColorModel.
	   *
	   * @param r red
	   * @param g green
	   * @param b blue
	   * @return a browser-friendly HEX value
	   */
	  public static String toHex(int r, int g, int b) {
	    return "#" + toBrowserHexValue(r) + toBrowserHexValue(g) + toBrowserHexValue(b);
	  }

	  private static String toBrowserHexValue(int number) {
	    StringBuilder builder = new StringBuilder(Integer.toHexString(number & 0xff));
	    while (builder.length() < 2) {
	      builder.append("0");
	    }
	    return builder.toString().toUpperCase();
	  }

	}

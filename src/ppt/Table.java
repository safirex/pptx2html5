package ppt;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

public class Table extends Element {
	List<TableRow> tablerows = new ArrayList<>();

	public Table(double pX, double pY, List<TableRow> tr, Dimension sz) {
		super(pX, pY, sz);
		tablerows = tr;

		}
	public Table(double pX, double pY, Dimension sz) {
		super(pX, pY, sz);
		}
	}


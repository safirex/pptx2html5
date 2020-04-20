package ppt;

import java.util.ArrayList;
import java.util.List;

public class TableRow {
	List<TexteBox> cells = new ArrayList<>();
	double height;
	public TableRow(double hg) {
		height = hg;
	}
	public TableRow (double hg, List<TexteBox> cls) {
		height = hg;
		cells = cls;
	}
}

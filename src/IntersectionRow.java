import java.util.ArrayList;

public class IntersectionRow extends ArrayList<Double> {
	public int intersectionsAfter(double d)  {
		int ans = 0;
		for(Double d2:this) {
			if(d2 >d) ans++;
		}
		return ans;
	}

	public void process(Outline o, int y) {
		for(Line2D line:o.getEdges())  {
			double intersection = line.findHorizontalIntersection(y);
			if(intersection>=0) add(intersection);
		}
	}
}

package geometry;

import java.util.ArrayList;

class IntersectionRow extends ArrayList<Double> {
	int intersectionsAfter(double d)  {
		int ans = 0;
		for(Double d2:this) {
			if(d2 >d) ans++;
		}
		return ans;
	}

	void process(Polygon o, int y) {
		for(Segment2D line:o.getEdges())  {
			double intersection = line.findHorizontalIntersection(y);
			if(intersection>=0) add(intersection);
		}
	}
}

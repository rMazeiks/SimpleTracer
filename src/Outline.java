import javafx.geometry.Point2D;

import java.util.ArrayList;

public class Outline extends ArrayList<Point2D> implements Surface {
	Line2D edgesCache[];
	boolean edgesCacheValid = false;

	public Outline() {
		super();
	}

	public boolean addPoint(Point2D point2D) {
		edgesCacheValid = false;
		return super.add(point2D);
		//return super.add(new Point2D(point2D.getX(), point2D.getY()));
	}

	public Line2D[] getEdges() {
		if (edgesCacheValid) return edgesCache;

		edgesCache = new Line2D[this.size()];
		for (int i = 0; i < this.size(); i++) {
			Point2D p1 = this.get(i);
			Point2D p2 = this.get((i + 1) % this.size());
			edgesCache[i] = new Line2D(p1, p2);
		}
		edgesCacheValid = true;
		return edgesCache;
	}

	public Point2D first() {
		return super.get(0);
	}

	@Override
	@Deprecated
	public boolean isOnSurface(Point2D point) {
		/*
		A point is on the surface if and only if there is an odd number of intersections when we addPoint a ray in some direction.
		 */
		boolean yes = false;
		for (Line2D line : this.getEdges()) {
			if (line.intersectsHorizontalRay(point.getX(), point.getY()))
				yes = !yes;
		}
		return yes;
	}

	@Override
	public boolean isNearBorder(double x, double y, double tolerance) {
		for (Point2D vertex : this) {
			if (Math.abs(vertex.getX() - x)
					+ Math.abs(vertex.getY() - y)
					< tolerance) return true; // close by manhattan distance (approximations are OK)
		}
		return false;
	}
}

package geometry;

import javafx.geometry.Point2D;

import java.util.ArrayList;

public class Polygon extends ArrayList<Point2D> implements Surface {
	private Segment2D edgesCache[];
	private boolean edgesCacheValid = false;

	public Polygon() {
		super();
	}

	public Polygon offset(double amount)  {
		Polygon polygon = new Polygon();

		Segment2D[] edges = getEdges();

		for (int i = 0; i < size(); i++) {
			Segment2D one = edges[i];
			Segment2D two = edges[(i+1)%size()];

			Point2D d1 = one.normalUnitVector();
			Point2D d2 = two.normalUnitVector();

			Point2D direction = d1.midpoint(d2).normalize().multiply(-amount); // multiplied by a negative, to shift outwards

			polygon.addPoint(get((i+1)%size()).add(direction));
		}

		return polygon;
	}

	/**
	 * Adds the next point in this outline.
	 * Points that represent a shape should be added in the clockwise direction, and points that represent a hole in a shape should be added counterclockwise.
	 * @param point2D the next point to add
	 * @return true
	 */
	public boolean addPoint(Point2D point2D) {
		edgesCacheValid = false;
		return super.add(point2D);
		//return super.add(new Point2D(point2D.getX(), point2D.getY()));
	}

	/**
	 *
	 * @return the list of segments that represent the edges of this polygon
	 */
	public Segment2D[] getEdges() {
		if (edgesCacheValid) return edgesCache;

		edgesCache = new Segment2D[this.size()];
		for (int i = 0; i < this.size(); i++) {
			Point2D p1 = this.get(i);
			Point2D p2 = this.get((i + 1) % this.size());
			edgesCache[i] = new Segment2D(p1, p2);
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
		for (Segment2D line : this.getEdges()) {
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

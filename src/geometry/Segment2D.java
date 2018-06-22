package geometry;

import javafx.geometry.Point2D;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Segment2D {
	/**
	 * The starting point of the segment
	 */
	private Point2D from;
	/**
	 * The ending point of the segment
	 */
	private Point2D to;

	/**
	 * Creates a segment that starts and ends at the points specified.
	 *
	 * @param from the starting point
	 * @param to   the ending point
	 */
	public Segment2D(Point2D from, Point2D to) {
		this.from = from;
		this.to = to;
	}

	/**
	 * Given three colinear points p, q, r, the function checks if
	 * point q lies on segment segment 'pr'
	 *
	 * @param p
	 * @param q
	 * @param r
	 * @return
	 */
	private static boolean onSegment(Point2D p, Point2D q, Point2D r) {
		return q.getX() <= max(p.getX(), r.getX()) && q.getX() >= min(p.getX(), r.getX()) &&
				q.getY() <= max(p.getY(), r.getY()) && q.getY() >= min(p.getY(), r.getY());
	}

	/**
	 * To find orientation of ordered triplet (p, q, r).
	 * The function returns following values
	 * 0 --> p, q and r are colinear
	 * 1 --> Clockwise
	 * 2 --> Counterclockwise
	 */
	private static int orientation(Point2D p, Point2D q, Point2D r) {
		// See https://www.geeksforgeeks.org/orientation-3-ordered-points/
		// for details of below formula.
		int val = (int) ((q.getY() - p.getY()) * (r.getX() - q.getX()) -
				(q.getX() - p.getX()) * (r.getY() - q.getY()));

		if (val == 0) return 0;  // colinear

		return (val > 0) ? 1 : 2; // clock or counterclock wise
	}

	/**
	 * Returns the starting point of the segment
	 *
	 * @return the starting point
	 */
	public Point2D getFrom() {
		return from;
	}

	/**
	 * Sets the starting point of the segment
	 *
	 * @param from the point to use as the starting point
	 */
	public void setFrom(Point2D from) {
		this.from = from;
	}

	/**
	 * Returns the ending point of the segment
	 *
	 * @return the end point
	 */
	public Point2D getTo() {
		return to;
	}


	/**
	 * Sets the ending point of the segment
	 *
	 * @param to the point to use as the ending point
	 */
	public void setTo(Point2D to) {
		this.to = to;
	}

	/**
	 * Returns the length of the segment
	 *
	 * @return the length of the segment
	 */
	public double length() {
		return from.distance(to);
	}


	/**
	 * 
	 * Determines whether this segment intersects with the specified segment
	 * <p>
	 * Translated from C++ here
	 * https://www.geeksforgeeks.org/check-if-two-given-segment-segments-intersect/
	 * @return  true if this segment and the specified segment intersect.
	 */
	public boolean intersects(Segment2D other) {
		Point2D p1 = from;
		Point2D q1 = to;
		Point2D p2 = other.getFrom();
		Point2D q2 = other.getTo();
		// Find the four orientations needed for general and
		// special cases
		int o1 = orientation(p1, q1, p2);
		int o2 = orientation(p1, q1, q2);
		int o3 = orientation(p2, q2, p1);
		int o4 = orientation(p2, q2, q1);

		// General case
		if (o1 != o2 && o3 != o4)
			return true;

		// Special Cases
		// p1, q1 and p2 are colinear and p2 lies on segment p1q1
		if (o1 == 0 && onSegment(p1, p2, q1)) return true;

		// p1, q1 and q2 are colinear and q2 lies on segment p1q1
		if (o2 == 0 && onSegment(p1, q2, q1)) return true;

		// p2, q2 and p1 are colinear and p1 lies on segment p2q2
		if (o3 == 0 && onSegment(p2, p1, q2)) return true;

		// p2, q2 and q1 are colinear and q1 lies on segment p2q2
		if (o4 == 0 && onSegment(p2, q1, q2)) return true;

		return false; // Doesn't fall in any of the above cases
	}

	/**
	 * Determines whether the ray starting at (x,y) and moving right intersects with this segment.
	 * (returns false if the intersection is not to the right of the origin of the ray)
	 * Useful for boundary checking.
	 * Hopefully more efficient than the general-case intersect method.
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	boolean intersectsHorizontalRay(double x, double y) {
		double smallY, x1;
		double largeY, x2;

		if (from.getY() < to.getY()) {
			smallY = from.getY();
			x1 = from.getX();

			largeY = to.getY();
			x2 = to.getX();
		} else {
			smallY = to.getY();
			x1 = to.getX();

			largeY = from.getY();
			x2 = from.getX();
		}

		if (y <= smallY || y > largeY) return false; // outside of bounds

		double fraction = (y - smallY) / (largeY - smallY);  // maps smallY-largeY to 0-1
		double intersectX = (x2 - x1) * fraction + x1; // maps 0-1 to x1-x2
		if (intersectX < x) return false; // intersects to the left of the ray; no good

		return true;
	}

	/**
	 * Returns -1 if the segment does not intersect, or intersects at a negative x-value.
	 * Otherwise, returns the x-coordinate where this segment intersects with the segment of the specified y-value
	 *
	 * @param y the vertical position of the line
	 * @return -1 if the segment does not intersect at a positive value, the coordinate of intersection otherwise
	 */
	double findHorizontalIntersection(double y) {
		double smallY, x1;
		double largeY, x2;

		if (from.getY() < to.getY()) {
			smallY = from.getY();
			x1 = from.getX();

			largeY = to.getY();
			x2 = to.getX();
		} else {
			smallY = to.getY();
			x1 = to.getX();

			largeY = from.getY();
			x2 = from.getX();
		}

		if (y <= smallY || y > largeY) return -1; // outside of bounds

		double fraction = (y - smallY) / (largeY - smallY);  // maps smallY-largeY to 0-1
		double intersectX = (x2 - x1) * fraction + x1; // maps 0-1 to x1-x2

		return intersectX;
	}
}
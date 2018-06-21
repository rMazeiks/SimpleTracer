import javafx.geometry.Point2D;

import java.util.ArrayList;

public class Outline extends ArrayList<Point2D> implements Surface {
	public Outline() {
		super();
	}

	public boolean addPoint(Point2D point2D) {
		return super.add(point2D);
		//return super.add(new Point2D(point2D.getX(), point2D.getY()));
	}

	public Line2D[] getEdges() {
		Line2D edges[] = new Line2D[this.size()];
		for (int i = 0; i < this.size(); i++) {
			Point2D p1 = this.get(i);
			Point2D p2 = this.get((i + 1) % this.size());
			edges[i] = new Line2D(p1, p2);
		}
		return edges;
	}

	public Point2D first() {
		return super.get(0);
	}

	@Override
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
	public boolean isNearBorder(Point2D point, double tolerance) {
		for(Point2D vertex: this)  {
			if(Math.abs(vertex.getX()-point.getX())
					+Math.abs(vertex.getY()-point.getY())
					<tolerance) return true; // close by manhattan distance (approximations are OK)
		}
		return false;
	}
}

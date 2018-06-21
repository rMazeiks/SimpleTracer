import javafx.geometry.Point2D;

public interface Surface {
	/*
	returns true if the specified point is on the surface.
	 */
	boolean isOnSurface(Point2D point);

	/*
	Returns true if the specified point is near the border. Nearness can be approximate to optimize performance.
	Tolerance should be the distance, in pixels, that is considered "close"
	 */
	boolean isNearBorder(Point2D point, double tolerance);
}

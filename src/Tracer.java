import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

import java.util.ArrayList;

import static java.lang.Math.*;

public class Tracer {
	private double level = 0.9; // threshold of what's considered black/white
	private double length = 3; // length of each segment
	private double accuracy = 0.01; // angle accuracy in radians.
	private double deviation = PI / 2; // max deviation from direction at each step (determines the sharpest angle of a turn the tracer can make while tracing) in rad
	private int safe = 1; // additional pixels checked (imaginary extension of segment)
	private int ignore = 2;// ignore first few pixels of the segment when checking if they touck black pixels.

	public ArrayList<Outline> traceAllOutlines(Image image) {
		ArrayList<Outline> outlines = new ArrayList<Outline>();

		PixelReader reader = image.getPixelReader();

		int count = 0;
		for (int y = 0; y < image.getHeight(); y++) {
			IntersectionRow intersectionRow = new IntersectionRow();
			for (Outline o : outlines) {
				intersectionRow.process(o, y);
			}
			horizontal:
			for (int x = 0; x < image.getWidth(); x++) {
				boolean black = false;
				Color color = reader.getColor(x, y);
				if (color.getBrightness() < level) black = true;

				boolean inside = intersectionRow.intersectionsAfter(x) % 2 == 1;
				for (Outline o : outlines) { //check if we are inside a traced area
					if (o.isNearBorder(new Point2D(x, y), length * 3)) continue horizontal;
					/*
					The tolerance in the line above had to be this large because the tracer would otherwise get stuck.
					For example, if there is a very sharp v-shape, the tracer
					 */
				}

				if (black != inside) { // either we are outside, and found a black pixel, or we are inside, and found a white pixel. Like XOR
					System.out.println("tracing...");
					Outline outline = traceOutline(new Point2D(x, y), image, black);
					outlines.add(outline);
					intersectionRow.process(outline, y);
					count++;
					System.out.println("traced");
				}

//				if(count>12) return outlines;

			}
		}
		System.out.println("Traced all!" + count);
		return outlines;
	}

	public Outline traceOutline(Point2D point, Image image, boolean black) {
		PixelReader reader = image.getPixelReader();
		// we rely on the fact that the pixelreader appears to return colors that are classified as white whenever it is given parameters outside the bounds of the image. Bad code todo

		Outline outline = new Outline();
		outline.addPoint(point);
		double direction = black ? 0 : PI; // in rad, math-style
		boolean done = false;

		int count = 0;
		while (!done) {
			count++;

			// first, perform a binarysearch-style algorithm to find the angle at which the next segment would just touch the object we're tracing
			double right = direction + deviation;
			double left = direction - deviation;
			while (right - left > accuracy) {
				double middle = (right + left) / 2;
				if (touchesBlack(image, reader, point, middle, length)) {
					right = middle;
				} else {
					left = middle;
				}
			}
			point = new Point2D(
					point.getX() + cos(left) * length,
					point.getY() + sin(left) * length);
			outline.addPoint(point);
			if (close(point, outline.first()) && count > 1) {
				done = true;
			}

			if (count > 10000) {
				System.err.println("Trace created too many segments. stopping this trace. It was probably going in loops because it wasn't finding the starting point again.");
				return outline;
			}

			direction = left;
		}

		return outline;
	}

	private boolean close(Point2D tracer, Point2D first) {
		return hypot(
				tracer.getX() - first.getX(),
				tracer.getY() - first.getY()
		) < length * 1.8; // multiplication for safety
	}

	/**
	 * Goes through the line of length @param length that starts at the point @param start and goes out in the direction @param direction.
	 * If a black pixel is encountered on this line, returns true.
	 * Otherwise, returns true
	 *
	 * @param image
	 * @param reader
	 * @param start
	 * @param direction
	 * @param length
	 * @return true if the specified line touches a black pixel, false otherwise.
	 */
	private boolean touchesBlack(Image image, PixelReader reader, Point2D start, double direction, double length) {
		// I apologize for the duplicate code here todo?
		double dx = cos(direction);
		double dy = sin(direction);

		if (abs(dy) > abs(dx)) { // direction is more vertical
			double slope = dx / dy;
			// x = my + c
			double offset = start.getX() - slope * start.getY(); // this is the c
			if (dy > 0) { // goin' down
				for (double y = start.getY() + ignore; y <= start.getY() + dy * length + safe; y++) {
					double x = slope * y + offset;
					if (inBounds(x, y, image) && reader.getColor((int) x, (int) y).getBrightness() < level) {
						return true;
					}
				}
			} else { // goin' up
				for (double y = start.getY() - ignore; y >= start.getY() + dy * length - safe; y--) {
					double x = slope * y + offset;
					if (inBounds(x, y, image) && reader.getColor((int) x, (int) y).getBrightness() < level) {
						return true;
					}
				}
			}
		} else { // direction is more horizontal
			double slope = dy / dx;
			// y = mx + c
			double offset = start.getY() - slope * start.getX(); // this is the c
			if (dx > 0) { // goin' right
				for (double x = start.getX() + ignore; x <= start.getX() + dx * length + safe; x++) {
					double y = slope * x + offset;
					if (inBounds(x, y, image) && reader.getColor((int) x, (int) y).getBrightness() < level) {

						return true;
					}
				}
			} else { // goin' left
				for (double x = start.getX() - ignore; x >= start.getX() + dx * length - safe; x--) {
					double y = slope * x + offset;
					if (inBounds(x, y, image) && reader.getColor((int) x, (int) y).getBrightness() < level) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean inBounds(double x, double y, Image image) {
		if (x < 0) return false;
		if (y < 0) return false;
		if (x >= image.getWidth()) return false;
		if (y >= image.getHeight()) return false;

		return true;
	}

	private Point2D findIsland(Image image, PixelReader reader) {
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				Color color = reader.getColor(x, y);
				if (color.getBrightness() < level) {
					return new Point2D(x, y);
				}
			}
		}
		return null;
	}
}

package geometry;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

import java.util.ArrayList;

import static java.lang.Math.*;

public class Tracer {
	private DoubleProperty threshold; // threshold of what's considered black/white
	private DoubleProperty segmentLength; // length of each segment
	private DoubleProperty accuracy; // angle accuracy in radians.
	private DoubleProperty deviation; // max deviation from direction at each step (determines the sharpest angle of a turn the tracer can make while tracing) in rad
	private IntegerProperty additionalPixels; // additional pixels checked (imaginary extension of segment)
	private IntegerProperty ignoredPixels;// ignoredPixels first few pixels of the segment when checking if they touck black pixels.

	public Tracer() {
		threshold = new SimpleDoubleProperty(0.9);
		segmentLength = new SimpleDoubleProperty(4);
		accuracy = new SimpleDoubleProperty(0.01);
		deviation = new SimpleDoubleProperty(PI/2);
		additionalPixels = new SimpleIntegerProperty(1);
		ignoredPixels = new SimpleIntegerProperty(3);
	}

	public double getThreshold() {
		return threshold.get();
	}

	public void setThreshold(double threshold) {
		this.threshold.set(threshold);
	}

	public DoubleProperty thresholdProperty() {
		return threshold;
	}

	public double getSegmentLength() {
		return segmentLength.get();
	}

	public void setSegmentLength(double segmentLength) {
		this.segmentLength.set(segmentLength);
	}

	public DoubleProperty segmentLengthProperty() {
		return segmentLength;
	}

	public double getAccuracy() {
		return accuracy.get();
	}

	public void setAccuracy(double accuracy) {
		this.accuracy.set(accuracy);
	}

	public DoubleProperty accuracyProperty() {
		return accuracy;
	}

	public double getDeviation() {
		return deviation.get();
	}

	public void setDeviation(double deviation) {
		this.deviation.set(deviation);
	}

	public DoubleProperty deviationProperty() {
		return deviation;
	}

	public int getAdditionalPixels() {
		return additionalPixels.get();
	}

	public void setAdditionalPixels(int additionalPixels) {
		this.additionalPixels.set(additionalPixels);
	}

	public IntegerProperty additionalPixelsProperty() {
		return additionalPixels;
	}

	public int getIgnoredPixels() {
		return ignoredPixels.get();
	}

	public void setIgnoredPixels(int ignoredPixels) {
		this.ignoredPixels.set(ignoredPixels);
	}

	public IntegerProperty ignoredPixelsProperty() {
		return ignoredPixels;
	}

	public ArrayList<Polygon> traceAllOutlines(Image image) {
		ArrayList<Polygon> polygons = new ArrayList<Polygon>();

		PixelReader reader = image.getPixelReader();

		int count = 0;
		for (int y = 0; y < image.getHeight(); y++) {
			IntersectionRow intersectionRow = new IntersectionRow();
			for (Polygon o : polygons) {
				intersectionRow.process(o, y);
			}
			horizontal:
			for (int x = 0; x < image.getWidth(); x++) {
				boolean black = false;
				Color color = reader.getColor(x, y);
				if (color.getBrightness() < threshold.get()) black = true;

				boolean inside = intersectionRow.intersectionsAfter(x) % 2 == 1;
				for (Polygon o : polygons) { //check if we are inside a traced area
					if (o.isNearBorder(x, y, segmentLength.get() * 3)) continue horizontal;
					/*
					The tolerance in the line above had to be this large because the tracer would otherwise get stuck.
					For example, if there is a very sharp v-shape, the tracer
					 */
				}

				if (black != inside) { // either we are outside, and found a black pixel, or we are inside, and found a white pixel. Like XOR
					System.out.println("tracing...");
					Polygon polygon = traceOutline(new Point2D(x, y), image, black);
					polygons.add(polygon);
					intersectionRow.process(polygon, y);
					count++;
					System.out.println("traced");
				}

//				if(count>12) return polygons;

			}
		}
		System.out.println("Traced all!" + count);
		return polygons;
	}

	public Polygon traceOutline(Point2D point, Image image, boolean black) {
		PixelReader reader = image.getPixelReader();
		// we rely on the fact that the pixelreader appears to return colors that are classified as white whenever it is given parameters outside the bounds of the image. Bad code todo

		Polygon polygon = new Polygon();
		polygon.addPoint(point);
		double direction = black ? 0 : PI; // in rad, math-style
		boolean done = false;

		int count = 0;
		while (!done) {
			count++;

			// first, perform a binarysearch-style algorithm to find the angle at which the next segment would just touch the object we're tracing
			double right = direction + deviation.get();
			double left = direction - deviation.get();
			while (right - left > accuracy.get()) {
				double middle = (right + left) / 2;
				if (touchesBlack(image, reader, point, middle, segmentLength.get())) {
					//right = middle; // actual binarysearch
					right=(right+middle)/2; // slowed down to minimize jumping over thin lines
				} else {
//					left = middle; // actual binarysearch
					left = (left+middle)/2;
				}
			}
			point = new Point2D(
					point.getX() + cos(left) * segmentLength.get(),
					point.getY() + sin(left) * segmentLength.get());
			polygon.addPoint(point);
			if (close(point, polygon.first()) && count > 1) {
				done = true;
			}

			if (count > 10000) {
				System.err.println("Trace created too many segments. stopping this trace. It was probably going in loops because it wasn't finding the starting point again.");
				return polygon;
			}

			direction = left;
		}

		return polygon;
	}

	private boolean close(Point2D tracer, Point2D first) {
		return hypot(
				tracer.getX() - first.getX(),
				tracer.getY() - first.getY()
		) < segmentLength.get() * 1.8; // multiplication for safety
	}

	/**
	 * Goes through the line of segmentLength @param segmentLength that starts at the point @param start and goes out in the direction @param direction.
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
				for (double y = start.getY() + ignoredPixels.get(); y <= start.getY() + dy * length + additionalPixels.get(); y++) {
					double x = slope * y + offset;
					if (inBounds(x, y, image) && reader.getColor((int) x, (int) y).getBrightness() < threshold.get()) {
						return true;
					}
				}
			} else { // goin' up
				for (double y = start.getY() - ignoredPixels.get(); y >= start.getY() + dy * length - additionalPixels.get(); y--) {
					double x = slope * y + offset;
					if (inBounds(x, y, image) && reader.getColor((int) x, (int) y).getBrightness() < threshold.get()) {
						return true;
					}
				}
			}
		} else { // direction is more horizontal
			double slope = dy / dx;
			// y = mx + c
			double offset = start.getY() - slope * start.getX(); // this is the c
			if (dx > 0) { // goin' right
				for (double x = start.getX() + ignoredPixels.get();
					 x <= start.getX() + dx * length + additionalPixels.get();
					 x++) {
					double y = slope * x + offset;
					if (inBounds(x, y, image) && reader.getColor((int) x, (int) y).getBrightness() < threshold.get()) {

						return true;
					}
				}
			} else { // goin' left
				for (double x = start.getX() - ignoredPixels.get(); x >= start.getX() + dx * length - additionalPixels.get(); x--) {
					double y = slope * x + offset;
					if (inBounds(x, y, image) && reader.getColor((int) x, (int) y).getBrightness() < threshold.get()) {
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

	@Deprecated
	private Point2D findIsland(Image image, PixelReader reader) {
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				Color color = reader.getColor(x, y);
				if (color.getBrightness() < threshold.get()) {
					return new Point2D(x, y);
				}
			}
		}
		return null;
	}
}

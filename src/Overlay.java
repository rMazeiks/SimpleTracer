import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Overlay extends Canvas {
	private GraphicsContext graphics;

	public Overlay(Image image) {
		super(image.getWidth(), image.getHeight());
		graphics = super.getGraphicsContext2D();
		graphics.setStroke(Color.color(1, 0.2, 0));
	}

	public void render(ArrayList<Outline> outlines) {
		for(Outline outline: outlines) {
			Point2D first = outline.get(0);
			graphics.moveTo(first.getX(), first.getY());
			graphics.beginPath();
			for (javafx.geometry.Point2D point : outline) {
				graphics.lineTo(point.getX(), point.getY());
			}
			graphics.closePath();

			graphics.stroke();

			//debug test
			//graphics.strokeLine(0, 0, getWidth() / 2, getHeight() / 2);
		}

//		PixelWriter writer = graphics.getPixelWriter();
//		for (int y = 0; y < this.getHeight(); y++) {
//			for (int x = 0; x <this.getWidth(); x++) {
//				if(outlines.get(0).isNearBorder(new Point2D(x, y), 5))
//					writer.setColor(x, y, Color.color(0, 1, 0.2));
//			}
//		}
	}
}

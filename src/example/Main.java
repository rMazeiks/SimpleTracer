package example;

import geometry.Polygon;
import geometry.Tracer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Main extends Application {

	public static void main(String[] args) {
		Application.launch();
	}

	@Override
	public void start(Stage primaryStage) {

		Image image = new Image("test/1.png");
		ImageView i = new ImageView(image);

		StackPane stackPane = new StackPane();
		stackPane.getChildren().add(i);


		ScrollPane parent = new ScrollPane();
		parent.setContent(stackPane);
		parent.setPannable(true);
//		parent.setPrefSize(800, 600);

		ArrayList<Polygon> polygons = trace(image);

		Overlay overlay = new Overlay(image);
		overlay.render(polygons);
		stackPane.getChildren().add(overlay);

		Scene scene = new Scene(parent);
		primaryStage.setScene(scene);
		primaryStage.show();


	}

	private ArrayList<Polygon> trace(Image image) {
		Tracer tracer = new Tracer();
		long millis = System.currentTimeMillis();
		ArrayList<Polygon> ans = null;
//		for (int i = 0; i < 4; i++) {
		ans = tracer.traceAllOutlines(image);
//		}
		System.out.println(System.currentTimeMillis() - millis);
		return ans;
	}
}

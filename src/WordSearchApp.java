/*
 * Copyright (c) 2013, 2014 Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *	notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *	notice, this list of conditions and the following disclaimer in
 *	the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle nor the names of its
 *	contributors may be used to endorse or promote products derived
 *	from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

// java -Dprism.forceGPU=true WordSearchApp --grid=../../grid.txt --locations=../../locations.txt

import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
//import javafx.scene.paint.PhongMaterial; //never used
//import javafx.scene.shape.Box; //never used
//import javafx.scene.shape.Cylinder; //never used
//import javafx.scene.shape.Sphere; //never used
//import javafx.scene.transform.Rotate; //never used
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import java.util.*;
import java.io.*;

/**
 *
 * @author jrwhitehill based on an earlier version by cmcastil
 */
public class WordSearchApp extends Application {
	final Group root = new Group();
	final Xform textGroup = new Xform();
	final Xform world = new Xform();
	final PerspectiveCamera camera = new PerspectiveCamera(true);
	final Xform cameraXform = new Xform();
	final Xform cameraXform2 = new Xform();
	final Xform cameraXform3 = new Xform();
	private static final double CAMERA_INITIAL_DISTANCE = -600;
	private static final double CAMERA_INITIAL_X_ANGLE = 0;
	private static final double CAMERA_INITIAL_Y_ANGLE = 0;
	private static final double CAMERA_NEAR_CLIP = 0.1;
	private static final double CAMERA_FAR_CLIP = 10000.0;
	//private static final double AXIS_LENGTH = 250.0; //never used
	private static final double CONTROL_MULTIPLIER = 0.1;
	private static final double SHIFT_MULTIPLIER = 10.0;
	private static final double MOUSE_SPEED = 0.1;
	private static final double ROTATION_SPEED = 2.0;
	private static final double TRACK_SPEED = 0.3;
	private static final double KEYBOARD_SPEED = 100.0;
	
	double mousePosX;
	double mousePosY;
	double mouseOldX;
	double mouseOldY;
	double mouseDeltaX;
	double mouseDeltaY;

	/**
	 * Helper class to hold a 3-tuple
	 */
	private static class Tuple {
		int _x, _y, _z;
		Tuple (int x, int y, int z) {
			_x = x;
			_y = y;
			_z = z;
		}

		@Override
		public int hashCode () {
			return _x + _y + _z;
		}

		@Override
		public boolean equals (Object o) {
			final Tuple other = (Tuple) o;
			return _x == other._x && _y == other._y && _z == other._z;
		}
	}
		
	private void buildCamera() {
		System.out.println("buildCamera()");
		root.getChildren().add(cameraXform);
		cameraXform.getChildren().add(cameraXform2);
		cameraXform2.getChildren().add(cameraXform3);
		cameraXform3.getChildren().add(camera);
		cameraXform3.setRotateZ(0);

		camera.setNearClip(CAMERA_NEAR_CLIP);
		camera.setFarClip(CAMERA_FAR_CLIP);
		camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
		cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
		cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
	}

	private void handleMouse(Scene scene, final Node root) {
		scene.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override public void handle(MouseEvent me) {
				mousePosX = me.getSceneX();
				mousePosY = me.getSceneY();
				mouseOldX = me.getSceneX();
				mouseOldY = me.getSceneY();
			}
		});
		scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override public void handle(MouseEvent me) {
				mouseOldX = mousePosX;
				mouseOldY = mousePosY;
				mousePosX = me.getSceneX();
				mousePosY = me.getSceneY();
				mouseDeltaX = (mousePosX - mouseOldX); 
				mouseDeltaY = (mousePosY - mouseOldY); 
				
				double modifier = 1.0;
				
				if (me.isControlDown()) {
					modifier = CONTROL_MULTIPLIER;
				} 
				if (me.isShiftDown()) {
					modifier = SHIFT_MULTIPLIER;
				}	 
				if (me.isPrimaryButtonDown()) {
					cameraXform.ry.setAngle(cameraXform.ry.getAngle() - mouseDeltaX*MOUSE_SPEED*modifier*ROTATION_SPEED);  
					cameraXform.rx.setAngle(cameraXform.rx.getAngle() + mouseDeltaY*MOUSE_SPEED*modifier*ROTATION_SPEED);  
				}
				else if (me.isSecondaryButtonDown()) {
					double z = camera.getTranslateZ();
					double newZ = z + mouseDeltaX*MOUSE_SPEED*modifier;
					camera.setTranslateZ(newZ);
				}
				else if (me.isMiddleButtonDown()) {
					cameraXform2.t.setX(cameraXform2.t.getX() + mouseDeltaX*MOUSE_SPEED*modifier*TRACK_SPEED);  
					cameraXform2.t.setY(cameraXform2.t.getY() + mouseDeltaY*MOUSE_SPEED*modifier*TRACK_SPEED);  
				}
			}
		});
	}
	
	private void handleKeyboard(Scene scene, final Node root) {
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				switch (event.getCode()) {
					case LEFT:
						camera.setTranslateX(camera.getTranslateX() - KEYBOARD_SPEED);
						break;
					case RIGHT:
						camera.setTranslateX(camera.getTranslateX() + KEYBOARD_SPEED);
						break;
					case DOWN:
						camera.setTranslateY(camera.getTranslateY() + KEYBOARD_SPEED);
						break;
					case UP:
						camera.setTranslateY(camera.getTranslateY() - KEYBOARD_SPEED);
						break;
					case A:
						camera.setTranslateZ(camera.getTranslateZ() + KEYBOARD_SPEED);
						break;
					case Z:
						camera.setTranslateZ(camera.getTranslateZ() - KEYBOARD_SPEED);
						break;
					default:
						break;
				}
			}
		});
	}
  
	/**
	 * Loads all the 3-d locations of all the letters of the words contained in the grid.
	 * Assigns random colors to each location.
	 * @return a map from a 3-d coordinate to the color the letter should have in the grid.
	 */
	private Map<Tuple, Color> loadLocations () throws FileNotFoundException {
		final Map<Tuple, Color> locations = new HashMap<Tuple, Color>();
		final Random rng = new Random();
		final String locationsFilename = getParameters().getNamed().get("locations");
		if (locationsFilename != null) {  // If user gave us a list of locations
			try(final Scanner s = new Scanner(new File(locationsFilename))) {
				final int numWords = s.nextInt();
				for (int i = 0; i < numWords; i++) {
					final int wordLen = s.nextInt();  // number of characters in the word
					final Color color = Color.rgb(rng.nextInt(255), rng.nextInt(255), rng.nextInt(255),0.5);
					for (int j = 0; j < wordLen; j++) {
						final int x = s.nextInt();
						final int y = s.nextInt();
						final int z = s.nextInt();
						locations.put(new Tuple(x, y, z), color);
					}
				}
			}
		}
		return locations;
	}

	/**
	 * Loads the grid of characters comprising the word search puzzle..
	 * @return a map from a 3-d coordinate to the color the letter should have in the grid.
	 */
	private char[][][] loadGrid () throws FileNotFoundException, IllegalArgumentException {
		final String gridFilename = getParameters().getNamed().get("grid");
		if (gridFilename == null) {
			throw new IllegalArgumentException("No \"grid\" in parameters list");
		}
		try(final Scanner s = new Scanner(new File(gridFilename))) {
			// First scan for the size of the grid
			final int sizeX = s.nextInt();
			final int sizeY = s.nextInt();
			final int sizeZ = s.nextInt();
			final char[][][] grid = new char[sizeX][sizeY][sizeZ];
			// Now scan for the characters in the grid
			for (int x = 0; x < sizeX; x++) {
				for (int y = 0; y < sizeY; y++) {
					for (int z = 0; z < sizeZ; z++) {
						final String str = s.next();
						grid[x][y][z] = str.charAt(0);
					}
				}
			}
			return grid;
		}
	}

	/**
	 * Create the 3-d grid of (2-d, image-based) characters.
	 */
	private void buildTextGrid() {
		final Font font = new Font(48);

		// Load grid contents and list of word locations
		final char[][][] grid;
		final Map<Tuple, Color> locations;
		try {
			grid = loadGrid();
			locations = loadLocations();
		} catch (IllegalArgumentException e) {
			System.out.println("Invalid filenames");
			return;
		} catch (FileNotFoundException e) {
			System.out.println("Could not load files");
			return;
		}

		// Create the grid with the appropriate color for the selected locations
		final int GAP = 72;
		for (int x = 0; x < grid.length; x++) {
			for (int y = 0; y < grid[0].length; y++) {
				for (int z = 0; z < grid[0][0].length; z++) {
					final char ch = grid[x][y][z];
					final Text text = new Text(x * GAP, y * GAP, "" + ch);

					final Tuple tuple = new Tuple(x, y, z);
					if (locations.containsKey(tuple)) {
						text.setFill(locations.get(tuple));
					}
					text.setFont(font);
					text.setTranslateZ((z + 1) * GAP);
					textGroup.getChildren().add(text);
				}
			}
		}

		world.getChildren().addAll(textGroup);
	}

	@Override
	public void start(Stage primaryStage) {
		root.getChildren().add(world);
		root.setDepthTest(DepthTest.ENABLE);

		buildCamera();
		buildTextGrid();

		Scene scene = new Scene(root, 1024, 768, true);
		scene.setFill(Color.GREY);
		handleKeyboard(scene, world);
		handleMouse(scene, world);

		primaryStage.setTitle("Word Search 3D");
		primaryStage.setScene(scene);
		primaryStage.show();

		scene.setCamera(camera);
	}

	/**
	 * The main() method is ignored in correctly deployed JavaFX application.
	 * main() serves only as fallback in case the application can not be
	 * launched through deployment artifacts, e.g., in IDEs with limited FX
	 * support. NetBeans ignores main().
	 *
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
}

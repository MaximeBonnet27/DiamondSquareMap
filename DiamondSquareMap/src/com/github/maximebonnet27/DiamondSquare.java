package com.github.maximebonnet27;

import java.util.Random;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;


public class DiamondSquare {

  public static int MAX_VALUE = 256;
  public static Random random;


  public static void seed(long seed){
    random = new Random(seed);
  }
  
  public static void seed(){
    random = new Random();
  }

  public static Biomes[][] generateBiomes(double[][] height, double[][] moisture, int size, double amplitude) {
    Biomes[][] biomes = new Biomes[size][size];

    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        if (height[i][j] <= MAX_VALUE / 10.0) {
          biomes[i][j] = Biomes.DEEP_OCEAN;
        } else if (height[i][j] <= MAX_VALUE / 2.0) {
          biomes[i][j] = Biomes.OCEAN;
        }
        /*
         * Elevation = 1
         */
        else if (height[i][j] <= 5 * MAX_VALUE / 8.0) {
          if (moisture[i][j] <= MAX_VALUE / 6.0) {
            biomes[i][j] = Biomes.SUBTROPICAL_DESERT;

          } else if (moisture[i][j] <= 2 * MAX_VALUE / 6.0) {
            biomes[i][j] = Biomes.GRASSLAND;

          } else if (moisture[i][j] <= 4 * MAX_VALUE / 6.0) {
            biomes[i][j] = Biomes.TROPICAL_SEASONAL_FOREST;

          } else {
            biomes[i][j] = Biomes.TROPICAL_RAIN_FOREST;

          }
        }
        /*
         * Elevation = 2
         */
        else if (height[i][j] <= 6 * MAX_VALUE / 8.0) {
          if (moisture[i][j] <= MAX_VALUE / 6.0) {
            biomes[i][j] = Biomes.TEMPERATE_DESERT;

          } else if (moisture[i][j] <= 3 * MAX_VALUE / 6.0) {
            biomes[i][j] = Biomes.GRASSLAND;

          } else if (moisture[i][j] <= 5 * MAX_VALUE / 6.0) {
            biomes[i][j] = Biomes.TEMPERATE_DECIDUOUS_FOREST;

          } else {
            biomes[i][j] = Biomes.TEMPERATE_RAIN_FOREST;

          }
        }
        /*
         * Elevation = 3
         */
        else if (height[i][j] <= 7 * MAX_VALUE / 8.0) {
          if (moisture[i][j] <= 2 * MAX_VALUE / 6.0) {
            biomes[i][j] = Biomes.TEMPERATE_DESERT;

          } else if (moisture[i][j] <= 4 * MAX_VALUE / 6.0) {
            biomes[i][j] = Biomes.SHRUBLAND;

          } else {
            biomes[i][j] = Biomes.TAIGA;

          }
        }
        /*
         * Elevation = 4
         */
        else {
          if (moisture[i][j] <= MAX_VALUE / 6.0) {
            biomes[i][j] = Biomes.SCORCHED;

          } else if (moisture[i][j] <= 2 * MAX_VALUE / 6.0) {
            biomes[i][j] = Biomes.BARE;

          } else if (moisture[i][j] <= 3 * MAX_VALUE / 6.0) {
            biomes[i][j] = Biomes.TUNDRA;

          } else {
            biomes[i][j] = Biomes.SNOW;

          }
        }
      }
    }
    return biomes;
  }

  /**
   * Generate a array of doubles, of size size x size.
   * 
   * @param size
   *            Size of the array generated, size = 2^n + 1 (the algorithm
   *            wouldn't work properly otherwise)
   */
  public static double[][] generate(int size, double amplitude, long seed) {

    // Array Initialization
    double[][] array = new double[size][size];
    array[0][0] = random.nextDouble() * MAX_VALUE;
    array[0][size - 1] = random.nextDouble() * MAX_VALUE;
    array[size - 1][0] = random.nextDouble() * MAX_VALUE;
    array[size - 1][size - 1] = random.nextDouble() * MAX_VALUE;

    // Space is the size of the square - 1
    int space = size - 1;
    // Half space is space / 2
    int halfspace;
    while (space > 1) {
      halfspace = space / 2;

      // Square step

      for (int i = halfspace; i < size; i += space) {
        for (int j = halfspace; j < size; j += space) {
          array[i][j] = squareStep(array, i, j, halfspace, amplitude,seed);
        }
      }

      // Diamond step

      for (int i = 0; i < size; i += halfspace) {
        /*
         * If i is at the top or bottom of the diamond, jStart =
         * halfSpace else jStart = 0
         */
        int jStart = ((i / halfspace) % 2 == 0) ? halfspace : 0;
        for (int j = jStart; j < size; j += space) {
          array[i][j] = diamondStep(array, i, j, halfspace, amplitude,seed);
        }
      }

      space = halfspace;

    }

    return array;
  }

  
  public static double[][] generateIsland(int size, double amplitude, long seed){
    double[][] array = new double[size][size];
    for(int i = 0; i < size; i++){
      for(int j = 0; j < size; j++){
        if(i == 0 || j == 0 || i == size - 1 || j == size -1)
          array[i][j] = 0;
        else
        array[i][j] = -1;
      }
    }
    array[(size - 1)/ 2][(size - 1) / 2] = MAX_VALUE;

    // Space is the size of the square - 1
    int space = size - 1;
    // Half space is space / 2
    int halfspace;
    while (space > 1) {
      halfspace = space / 2;

      // Square step

      for (int i = halfspace; i < size; i += space) {
        for (int j = halfspace; j < size; j += space) {
          if(array[i][j] == -1)
            array[i][j] = squareStep(array, i, j, halfspace, amplitude,seed);
        }
      }

      // Diamond step

      for (int i = 0; i < size; i += halfspace) {
        /*
         * If i is at the top or bottom of the diamond, jStart =
         * halfSpace else jStart = 0
         */
        int jStart = ((i / halfspace) % 2 == 0) ? halfspace : 0;
        for (int j = jStart; j < size; j += space) {
          if(array[i][j] == -1)

            array[i][j] = diamondStep(array, i, j, halfspace, amplitude,seed);
        }
      }

      space = halfspace;

    }

    return array;
  }

  private static double squareStep(double[][] array, int i, int j,
      int halfspace, double amplitude, long seed) {
    double a = array[i - halfspace][j - halfspace];
    double b = array[i - halfspace][j + halfspace];
    double c = array[i + halfspace][j - halfspace];
    double d = array[i + halfspace][j + halfspace];
    double noise = amplitude * halfspace * (random.nextDouble() * 2 - 1);
    return (a + b + c + d) / 4 + noise;
  }

  private static double diamondStep(double[][] array, int i, int j,
      int halfspace, double amplitude, long seed) {
    double a, b, c, d;
    double noise = amplitude * halfspace * (random.nextDouble() * 2 - 1);

    if (i == 0) {
      a = array[i][j + halfspace];
      b = array[i][j - halfspace];
      c = array[i + halfspace][j];
      return (a + b + c) / 3 + noise;
    } else if (i == array.length - 1) {
      a = array[i][j + halfspace];
      b = array[i][j - halfspace];
      c = array[i - halfspace][j];
      return (a + b + c) / 3 + noise;

    } else if (j == 0) {
      a = array[i + halfspace][j];
      b = array[i - halfspace][j];
      c = array[i][j + halfspace];
      return (a + b + c) / 3 + noise;

    } else if (j == array.length - 1) {
      a = array[i + halfspace][j];
      b = array[i - halfspace][j];
      c = array[i][j - halfspace];
      return (a + b + c) / 3 + noise;

    }

    a = array[i][j + halfspace];
    b = array[i][j - halfspace];
    c = array[i + halfspace][j];
    d = array[i - halfspace][j];

    return (a + b + c + d) / 4 + noise;
  }

  /**
   * 
   * @param mode
   *            1 = Black and White, 2 = islands
   */
  public static void display(double[][] array, int mode) {
    try {
      Display.setDisplayMode(new DisplayMode(array.length, array.length));
      Display.setTitle("DiamondSquare Map");
      Display.create();
    } catch (LWJGLException e) {
      e.printStackTrace();
      System.exit(0);
    }

    // init OpenGL
    GL11.glMatrixMode(GL11.GL_PROJECTION);
    GL11.glLoadIdentity();
    GL11.glOrtho(0, array.length, 0, array.length, 1, -1);
    GL11.glMatrixMode(GL11.GL_MODELVIEW);

    while (!Display.isCloseRequested()) {
      // Clear the screen and depth buffer
      GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

      // set the color of the quad (R,G,B,A)
      GL11.glBegin(GL11.GL_POINTS);
      for (int i = 0; i < array.length; i++) {
        for (int j = 0; j < array.length; j++) {
          setColor(array[i][j], mode);
          GL11.glVertex2i(i, j);
        }
      }
      GL11.glEnd();

      Display.update();
    }

    Display.destroy();
  }

  private static void setColor(double d, int mode) {
    if (mode == 1)
      GL11.glColor3d(d / MAX_VALUE, d / MAX_VALUE, d / MAX_VALUE);
    else if (mode == 2) {
      if (d <= MAX_VALUE / 10) { // Deep Sea Blue
        GL11.glColor3d(16 / 255.0, 52 / 255.0, 166 / 255.0);
      } else if (d <= MAX_VALUE / 2) { // Sea blue
        GL11.glColor3d(0, 127 / 255.0, 1.0);
      } else if (d <= MAX_VALUE / 2 + MAX_VALUE / 10.0) { // Yelow sand
        GL11.glColor3d(224 / 255.0, 205 / 255.0, 169 / 255.0);
      } else if (d >= 19 * MAX_VALUE / 20) { // Snow White
        GL11.glColor3d(239 / 255.0, 239 / 255.0, 239 / 255.0);
      } else if (d >= 9 * MAX_VALUE / 10) { // Grey Rock
        GL11.glColor3d(169 / 255.0, 169 / 255.0, 169 / 255.0);
      }

      else { // Grass Green
        GL11.glColor3d(58 / 255.0, 137 / 255.0, 35 / 255.0);
      }
    }
  }

  public static void displayBiomesMap(Terrain[][] array) {
    try {
      Display.setDisplayMode(new DisplayMode(array.length, array.length));
      Display.create();
    } catch (LWJGLException e) {
      e.printStackTrace();
      System.exit(0);
    }

    // init OpenGL
    GL11.glMatrixMode(GL11.GL_PROJECTION);
    GL11.glLoadIdentity();
    GL11.glOrtho(0, array.length, 0, array.length, 1, -1);
    GL11.glMatrixMode(GL11.GL_MODELVIEW);

    while (!Display.isCloseRequested()) {
      // Clear the screen and depth buffer
      GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

      // set the color of the quad (R,G,B,A)

      GL11.glBegin(GL11.GL_POINTS);
      for (int i = 0; i < array.length; i++) {
        for (int j = 0; j < array.length; j++) {
          setBiomeColor(array[i][j].biome);
          GL11.glVertex2i(i, j);
        }
      }
      GL11.glEnd();

      Display.update();
    }

    Display.destroy();
  }

  private static void setBiomeColor(Biomes biome) {
    switch (biome) {
    case BARE:
      setRGBColor(175, 175, 175);
      break;
    case DEEP_OCEAN:
      setRGBColor(85, 125, 166);
      //setRGBColor(54, 54, 97); // Deep Ocean original color
      break;
    case GRASSLAND:
      setRGBColor(196, 212, 170);
      break;
    case OCEAN:
      setRGBColor(85, 125, 166);
      break;
    case SCORCHED:
      setRGBColor(153, 153, 153);
      break;
    case SHRUBLAND:
      setRGBColor(196, 204, 187);
      break;
    case SNOW:
      setRGBColor(248, 248, 248);
      break;
    case SUBTROPICAL_DESERT:
      setRGBColor(233, 221, 199);
      break;
    case TAIGA:
      setRGBColor(204, 212, 187);
      break;
    case TEMPERATE_DECIDUOUS_FOREST:
      setRGBColor(180, 201, 169);
      break;
    case TEMPERATE_DESERT:
      setRGBColor(228, 232, 202);
      break;
    case TEMPERATE_RAIN_FOREST:
      setRGBColor(164, 196, 168);
      break;
    case TROPICAL_RAIN_FOREST:
      setRGBColor(156, 187, 169);
      break;
    case TROPICAL_SEASONAL_FOREST:
      setRGBColor(169, 204, 164);
      break;
    case TUNDRA:
      setRGBColor(221, 221, 187);
      break;

    }
  }

  private static void setRGBColor(int r, int g, int b) {
    GL11.glColor3d(r / 255.0, g / 255.0, b / 255.0);

  }

  public static double[][] generateZoom(double[][] array, int x, int y, double amplitude, long seed){
    double[][] zoom = new double[array.length][array.length];
    if(x > array.length / 2)
      x = array.length / 2;
    if(y > array.length / 2)
      y = array.length / 2;
    for(int i = 0; i < array.length; i++){
      for(int j = 0; j < array.length; j++){
        if(i % 2 == 0 && j % 2 == 0)
          zoom[i][j] = array[x + i/2][y + j/2];
        else
          zoom[i][j] = -1;
      }
    }

    // Space is the size of the square - 1
    int space = array.length - 1;
    // Half space is space / 2
    int halfspace;
    while (space > 1) {
      halfspace = space / 2;

      // Square step

      for (int i = halfspace; i < zoom.length; i += space) {
        for (int j = halfspace; j < zoom.length; j += space) {
          if(zoom[i][j] == -1)
            zoom[i][j] = squareStep(zoom, i, j, halfspace, amplitude,seed);
        }
      }

      // Diamond step

      for (int i = 0; i < zoom.length; i += halfspace) {
        /*
         * If i is at the top or bottom of the diamond, jStart =
         * halfSpace else jStart = 0
         */
        int jStart = ((i / halfspace) % 2 == 0) ? halfspace : 0;
        for (int j = jStart; j < zoom.length; j += space) {
          if(zoom[i][j] == -1)
            zoom[i][j] = diamondStep(zoom, i, j, halfspace, amplitude,seed);
        }
      }

      space = halfspace;

    }

    return zoom;


  }

  public static Terrain[][] generateTerrain(double[][] height, double[][] moisture,int size, double amplitude){


    Biomes[][] biomes = generateBiomes(height, moisture,size, amplitude);
    Terrain[][] terrain = new Terrain[biomes.length][biomes.length];
    for(int i = 0; i < biomes.length; i++){
      for(int j = 0; j < biomes.length; j++){
        terrain[i][j] = new Terrain(biomes[i][j], moisture[i][j], height[i][j]);
      }
    }
    return terrain;
  }

  public static Terrain[][] generateZoomTerrain(Terrain[][] terrain, int x, int y, long seed, double amplitude){
    double[][] height = generateZoom(getHeightArray(terrain), x, y, amplitude, seed);
    double[][] moisture = generateZoom(getMoistureArray(terrain), x, y, amplitude, seed);
    return generateTerrain(height, moisture, terrain.length, amplitude);
  }

  public static double[][] getHeightArray(Terrain[][] terrain){
    double[][] h = new double[terrain.length][terrain.length];
    for(int i = 0; i< h.length; i++){
      for(int j = 0; j < h.length; j++){
        h[i][j] = terrain[i][j].height;
      }
    }
    return h;
  }

  public static double[][] getMoistureArray(Terrain[][] terrain){
    double[][] m = new double[terrain.length][terrain.length];
    for(int i = 0; i< m.length; i++){
      for(int j = 0; j < m.length; j++){
        m[i][j] = terrain[i][j].height;
      }
    }
    return m;
  }

  public static void main(String[] args) {
    seed();
    double[][] h = generateIsland(1025, 1, 2);
    double[][] m = generate(1025, 1, 2);
    Terrain[][] t = generateTerrain(h,m,1025, 1);
    displayBiomesMap(t);



  }
}
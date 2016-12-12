package floydsteinberg;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class FloydSteinberg {

    /*------------------------------------------------------------------------*/
    public static void main(String[] args) throws IOException {

        FloydSteinberg floyd = new FloydSteinberg();
        floyd.RGB_ColorSpace();
        floyd.process("/Users/ktd/Desktop/img/rose_bw.png", "red");
    }
    /*------------------------------------------------------------------------*/

    public void RGB_ColorSpace() throws IOException {
        String s = "/Users/ktd/Desktop/rose.png"; // -- read image from project directory

        // -- read input image (1)
        File infile = new File(s);
        BufferedImage bi = ImageIO.read(infile);

        int width = bi.getWidth();
        int height = bi.getHeight();

        // -- separate out image components (2)
        int red[][] = new int[width][height];
        int grn[][] = new int[width][height];
        int blu[][] = new int[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                red[x][y] = bi.getRGB(x, y) >> 16 & 0xFF;
                grn[x][y] = bi.getRGB(x, y) >> 8 & 0xFF;
                blu[x][y] = bi.getRGB(x, y) & 0xFF;
                //System.out.printf("red=%d green=%d blue=%d \n"
                // , red[x][y], grn[x][y], blu[x][y]);
            }
        }

        /*----------------------- Part 1----------------------------------*/
        // -- move image into BufferedImage object
        bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        //for (double i = 0; i <= numberOfImages; i++) {

        double currentAlpha = 1;//i / numberOfImages;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                int Y = (int) (0.299 * red[x][y] + 0.587 * grn[x][y] + 0.114 * blu[x][y]);

                // Alpha Blend
                double newRed = ((1 - currentAlpha) * red[x][y] + currentAlpha * Y);
                double newGreen = ((1 - currentAlpha) * grn[x][y] + currentAlpha * Y);
                double newBlue = ((1 - currentAlpha) * blu[x][y] + currentAlpha * Y);

                int pixel = ((int) newRed << 16) | ((int) newGreen << 8) | ((int) newBlue);
                bi.setRGB(x, y, pixel);

            }
        }

        String filename = String.format("/Users/ktd/Desktop/img/rose_bw.png");
        System.out.println(filename);
        File outputfile = new File(filename);
        ImageIO.write(bi, "png", outputfile);
        //}
    }

    public void process(String fileIn, String color) throws IOException {

        File infile = new File(fileIn);
        BufferedImage bi = ImageIO.read(infile);

        int width = bi.getWidth();
        int height = bi.getHeight();

        int red[][] = new int[width][height];
        int grn[][] = new int[width][height];
        int blu[][] = new int[width][height];


        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                red[x][y] = bi.getRGB(x, y) >> 16 & 0xFF; // unsigned int
                grn[x][y] = bi.getRGB(x, y) >> 8 & 0xFF;  // unsigned int
                blu[x][y] = bi.getRGB(x, y) & 0xFF;       // unsigned int
            }
        }
        int[][] pixel_floyd = dither(red, height, width);

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {


                int pixel = ((int) pixel_floyd[x][y] << 16)
                          | ((int) pixel_floyd[x][y] << 8)
                          | ((int) pixel_floyd[x][y]);
                img.setRGB(x, y, pixel);
            }
        }
        String filename = "/Users/ktd/Desktop/img/FloydSteinberg.png";
        System.out.println(filename);
        File outputfile = new File(filename);
        ImageIO.write(img, "png", outputfile);

    }
    /*------------------------------------------------------------------------*/

    private int[][] dither(int[][] pixel, int height, int width) {
        int oldpixel, newpixel, error;
        boolean nbottom, nleft, nright;

        for (int y = 0; y < height; y++) {
            nbottom = y < height - 1;

            for (int x = 0; x < width; x++) {
                nleft = x > 0;
                nright = x < width - 1;

                oldpixel = pixel[x][y];
                newpixel = oldpixel < 128 ? 0 : 255;

                pixel[x][y] = newpixel;

                error = oldpixel - newpixel;

                if (nright) {
                    pixel[x + 1][y] += 7 * error / 16;
                }
                if (nleft & nbottom) {
                    pixel[x - 1][y + 1] += 3 * error / 16;
                }
                if (nbottom) {
                    pixel[x][y + 1] += 5 * error / 16;
                }
                if (nright && nbottom) {
                    pixel[x + 1][y + 1] += error / 16;
                }
            }
        }
        return pixel;
    }
    /*------------------------------------------------------------------------*/
}

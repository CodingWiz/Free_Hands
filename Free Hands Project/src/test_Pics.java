/**
 * Created by astrophysicsguy on 7/6/17.
 */

import com.github.sarxos.webcam.Webcam;
import javax.imageio.ImageIO;
import java.io.File;

public class test_Pics {
    public static void main(String[] args) throws Exception {
        Webcam webcam = Webcam.getDefault();
        webcam.open();

        for (int x = 1; x <= 500; x++) {
            ImageIO.write(webcam.getImage(), "PNG", new File("/home/astrophysicsguy/Pics/Pics" + x + ".png"));

            System.out.println("Taking picture " + x);
        }
    }
}

package axyl.client.util.math;

import java.math.BigDecimal;   
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.Random;

import axyl.client.util.Utility;
import net.minecraft.potion.Potion;

public class MathUtils extends Utility {
	
	public static double roundToPlace(final double value, final int places)
    {
        if (places < 0)
        {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
	
    public static double clamp(double value, double minimum, double maximum) {
        return value > maximum ? maximum : value < minimum ? minimum : value;
    }
	
    public static float[] constrainAngle(float[] vector) {

        vector[0] = (vector[0] % 360F);
        vector[1] = (vector[1] % 360F);

        while (vector[0] <= -180) {
            vector[0] = (vector[0] + 360);
        }

        while (vector[1] <= -180) {
            vector[1] = (vector[1] + 360);
        }

        while (vector[0] > 180) {
            vector[0] = (vector[0] - 360);
        }

        while (vector[1] > 180) {
            vector[1] = (vector[1] - 360);
        }

        return vector;
    }
}

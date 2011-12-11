package my.javagroup.util;

import java.awt.*;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;

/**
 * User: Admin
 * Date: 11.12.11
 * Time: 1:34
 */
public class ImageRender {
    //todo: has some bugs. Image sometimes has "bad" color
    //Copy-Paste from StackOverflow (thanks to Cazra and kleopatra)
    public static Image setSemiTransparency(Image srcImg, double semiTrans) // method accepts a transparent color.
    {
        // It'll transform all pixels of the transparent color to transparent.
        if(semiTrans > 1.0)
            semiTrans = 1.0;
        if(semiTrans < 0.0)
            semiTrans = 0.0;
        final int alpha = (int) (255 * (1.0 - semiTrans));

        // overriding part of the RGBImageFilterClass to produce a specialized filter.
        java.awt.image.ImageFilter filter = new RGBImageFilter()
        {
            public int  filterRGB(int x, int y, int rgb) // overriden method
            {
                //System.out.println(alpha);
                if((rgb & 0xFF000000) != 0)
                    return (rgb & 0x00FFFFFF) + (alpha << 24); // alpha bits set to 0 yields transparency.
                else
                    return rgb;
            }
        };

        ImageProducer ip = new FilteredImageSource(srcImg.getSource(),filter);
        Image result = Toolkit.getDefaultToolkit().createImage(ip);

        return result;
    }
}

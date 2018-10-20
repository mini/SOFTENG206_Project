package assignment4.utils;



import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

/**
 * Permanent Tooltip is a class that was referenced by an open-source code online on Github
 * by Dan Armbrust. The code is a utility that changes the tooltip delays that can only be
 * achieved through Java10 - due to the project being based of Java8, this utility was necessary.
 *
 * The code can be found here: https://gist.github.com/darmbrust/9559744d1b1dada434a3
 *
 * {@link PermanentTooltip}
 *
 * @author <a href="mailto:daniel.armbrust.list@gmail.com">Dan Armbrust</a>
 */
public class PermanentTooltip {

    /**
     * This class changes the default delays of the standard JavaFX Tooltips.
     * Returns true if successful.
     * Current defaults are 1000, 5000, 200;
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static boolean setTooltipTimers(long openDelay, long visibleDuration, long closeDelay)
    {
        try
        {
            Field f = Tooltip.class.getDeclaredField("BEHAVIOR");
            f.setAccessible(true);


            Class[] classes = Tooltip.class.getDeclaredClasses();
            for (Class clazz : classes)
            {
                if (clazz.getName().equals("javafx.scene.control.Tooltip$TooltipBehavior"))
                {
                    Constructor ctor = clazz.getDeclaredConstructor(Duration.class, Duration.class, Duration.class, boolean.class);
                    ctor.setAccessible(true);
                    Object tooltipBehavior = ctor.newInstance(new Duration(openDelay), new Duration(visibleDuration), new Duration(closeDelay), false);
                    f.set(null, tooltipBehavior);
                    break;
                }
            }
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }
}
package com.group_finity.mascot.action;

import com.group_finity.mascot.Main;
import com.group_finity.mascot.Mascot;
import com.group_finity.mascot.animation.Animation;
import com.group_finity.mascot.exception.BehaviorInstantiationException;
import com.group_finity.mascot.exception.CantBeAliveException;
import com.group_finity.mascot.exception.LostGroundException;
import com.group_finity.mascot.exception.VariableException;
import com.group_finity.mascot.script.VariableMap;

import java.awt.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Original Author: Yuki Yamada of Group Finity (http://www.group-finity.com/Shimeji/)
 * Currently developed by Shimeji-ee Group.
 */
public class Breed extends Animate {

    public static final String PARAMETER_BORNX = "BornX";
    public static final String PARAMETER_BORNY = "BornY";
    public static final String PARAMETER_BORNBEHAVIOR = "BornBehavior";
    private static final Logger log = Logger.getLogger(Breed.class.getName());
    private static final int DEFAULT_BORNX = 0;
    private static final int DEFAULT_BORNY = 0;
    private static final String DEFAULT_BORNBEHAVIOR = "";

    public Breed(final List<Animation> animations, final VariableMap params) {
        super(animations, params);
    }

    @Override
    protected void tick() throws LostGroundException, VariableException {

        super.tick();

        if (getTime() == getAnimation().getDuration() - 1) {
            breed();
        }
    }

    private void breed() throws VariableException {

        final Mascot mascot = new Mascot(getMascot().getImageSet());

        log.log(Level.INFO, "Breed Mascot ({0},{1},{2})", new Object[]{getMascot(), this, mascot});

        if (getMascot().isLookRight()) {
            mascot.setAnchor(new Point(getMascot().getAnchor().x - getBornX(), getMascot().getAnchor().y
                    + getBornY().intValue()));
        } else {
            mascot.setAnchor(new Point(getMascot().getAnchor().x + getBornX(), getMascot().getAnchor().y
                    + getBornY().intValue()));
        }
        mascot.setLookRight(getMascot().isLookRight());

        try {
            mascot.setBehavior(Main.getInstance().getConfiguration(getMascot().getImageSet()).buildBehavior(getBornBehavior()));

            getMascot().getManager().add(mascot);

        } catch (final BehaviorInstantiationException e) {
            log.log(Level.SEVERE, "Fatal Exception", e);
            Main.showError("Failed to create new Shimeji.\nSee log for more details.");
            mascot.dispose();
        } catch (final CantBeAliveException e) {
            log.log(Level.SEVERE, "Fatal Exception", e);
            Main.showError("Failed to create new Shimeji.\nSee log for more details.");
            mascot.dispose();
        }
    }

    private Number getBornY() throws VariableException {
        return eval(PARAMETER_BORNY, Number.class, DEFAULT_BORNY);
    }

    private int getBornX() throws VariableException {
        return eval(PARAMETER_BORNX, Number.class, DEFAULT_BORNX).intValue();
    }

    private String getBornBehavior() throws VariableException {
        return eval(PARAMETER_BORNBEHAVIOR, String.class, DEFAULT_BORNBEHAVIOR);
    }

}

package com.group_finity.mascot.action;

import com.group_finity.mascot.animation.Animation;
import com.group_finity.mascot.environment.Area;
import com.group_finity.mascot.exception.LostGroundException;
import com.group_finity.mascot.exception.VariableException;
import com.group_finity.mascot.script.VariableMap;

import java.awt.*;
import java.util.List;
import java.util.logging.Logger;

/**
 * Original Author: Yuki Yamada of Group Finity (http://www.group-finity.com/Shimeji/)
 * Currently developed by Shimeji-ee Group.
 */
public class ThrowIE extends Animate {

    public static final String PARAMETER_INITIALVX = "InitialVX";
    public static final String PARAMETER_INITIALVY = "InitialVY";
    public static final String PARAMETER_GRAVITY = "Gravity";
    private static final Logger log = Logger.getLogger(ThrowIE.class.getName());
    private static final int DEFAULT_INITIALVX = 32;
    private static final int DEFAULT_INITIALVY = -10;
    private static final double DEFAULT_GRAVITY = 0.5;

    public ThrowIE(final List<Animation> animations, final VariableMap params) {
        super(animations, params);

    }

    @Override
    public boolean hasNext() throws VariableException {

        final boolean ieVisible = getEnvironment().getActiveIE().isVisible();

        return super.hasNext() && ieVisible;
    }

    @Override
    protected void tick() throws LostGroundException, VariableException {

        super.tick();

        final Area activeIE = getEnvironment().getActiveIE();

        if (activeIE.isVisible()) {
            if (getMascot().isLookRight()) {
                getEnvironment().moveActiveIE(new Point(activeIE.getLeft() + getInitialVx(), activeIE.getTop()
                        + getInitialVy() + (int) (getTime() * getGravity())));
            } else {
                getEnvironment().moveActiveIE(new Point(activeIE.getLeft() - getInitialVx(), activeIE.getTop()
                        + getInitialVy() + (int) (getTime() * getGravity())));
            }
        }

    }

    private double getGravity() throws VariableException {
        return eval(PARAMETER_GRAVITY, Number.class, DEFAULT_GRAVITY).doubleValue();
    }

    private int getInitialVy() throws VariableException {
        return eval(PARAMETER_INITIALVY, Number.class, DEFAULT_INITIALVY).intValue();
    }

    private int getInitialVx() throws VariableException {
        return eval(PARAMETER_INITIALVX, Number.class, DEFAULT_INITIALVX).intValue();
    }
}

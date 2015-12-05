package com.group_finity.mascot.action;

import com.group_finity.mascot.exception.VariableException;
import com.group_finity.mascot.script.VariableMap;

import java.awt.*;
import java.util.logging.Logger;

/**
 * Original Author: Yuki Yamada of Group Finity (http://www.group-finity.com/Shimeji/)
 * Currently developed by Shimeji-ee Group.
 */
public class Offset extends InstantAction {

    public static final String PARAMETER_OFFSETX = "X";
    public static final String PARAMETER_OFFSETY = "Y";
    private static final Logger log = Logger.getLogger(Offset.class.getName());
    private static final int DEFAULT_OFFSETX = 0;
    private static final int DEFAULT_OFFSETY = 0;

    public Offset(final VariableMap params) {
        super(params);
    }

    @Override
    protected void apply() throws VariableException {
        getMascot().setAnchor(
                new Point(getMascot().getAnchor().x + getOffsetX(), getMascot().getAnchor().y + getOffsetY()));
    }

    private int getOffsetY() throws VariableException {
        return eval(PARAMETER_OFFSETY, Number.class, DEFAULT_OFFSETY).intValue();
    }

    private int getOffsetX() throws VariableException {
        return eval(PARAMETER_OFFSETX, Number.class, DEFAULT_OFFSETX).intValue();
    }

}

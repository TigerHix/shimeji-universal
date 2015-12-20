package com.group_finity.mascot.action;

import com.group_finity.mascot.exception.VariableException;
import com.group_finity.mascot.script.VariableMap;

import java.util.logging.Logger;

/**
 * Original Author: Yuki Yamada of Group Finity (http://www.group-finity.com/Shimeji/)
 * Currently developed by Shimeji-ee Group.
 */
public class Look extends InstantAction {

    public static final String PARAMETER_LOOKRIGHT = "LookRight";
    private static final Logger log = Logger.getLogger(Look.class.getName());

    public Look(final VariableMap params) {
        super(params);
    }

    @Override
    protected void apply() throws VariableException {
        getMascot().setLookRight(isLookRight());
    }

    private Boolean isLookRight() throws VariableException {
        return eval(PARAMETER_LOOKRIGHT, Boolean.class, !getMascot().isLookRight());
    }
}

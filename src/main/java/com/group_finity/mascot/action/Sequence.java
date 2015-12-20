package com.group_finity.mascot.action;

import com.group_finity.mascot.exception.VariableException;
import com.group_finity.mascot.script.VariableMap;

import java.util.logging.Logger;

/**
 * Original Author: Yuki Yamada of Group Finity (http://www.group-finity.com/Shimeji/)
 * Currently developed by Shimeji-ee Group.
 */
public class Sequence extends ComplexAction {

    public static final String PARAMETER_LOOP = "Loop";
    private static final Logger log = Logger.getLogger(Sequence.class.getName());
    private static final boolean DEFAULT_LOOP = false;

    public Sequence(final VariableMap params, final Action... actions) {
        super(params, actions);
    }

    @Override
    public boolean hasNext() throws VariableException {

        seek();

        return super.hasNext();
    }

    @Override
    protected void setCurrentAction(final int currentAction) throws VariableException {
        super.setCurrentAction(isLoop() ? currentAction % getActions().length : currentAction);
    }

    private Boolean isLoop() throws VariableException {
        return eval(PARAMETER_LOOP, Boolean.class, DEFAULT_LOOP);
    }

}

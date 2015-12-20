package com.group_finity.mascot.action;

import com.group_finity.mascot.Mascot;
import com.group_finity.mascot.animation.Animation;
import com.group_finity.mascot.environment.MascotEnvironment;
import com.group_finity.mascot.exception.LostGroundException;
import com.group_finity.mascot.exception.VariableException;
import com.group_finity.mascot.script.Variable;
import com.group_finity.mascot.script.VariableMap;

import java.util.List;
import java.util.logging.Logger;

/**
 * Original Author: Yuki Yamada of Group Finity (http://www.group-finity.com/Shimeji/)
 * Currently developed by Shimeji-ee Group.
 */
public abstract class ActionBase implements Action {

    public static final String PARAMETER_DURATION = "Duration";
    public static final String PARAMETER_CONDITION = "Condition";
    private static final Logger log = Logger.getLogger(ActionBase.class.getName());
    private static final boolean DEFAULT_CONDITION = true;
    private static final int DEFAULT_DURATION = Integer.MAX_VALUE;

    private Mascot mascot;

    private int startTime;

    private List<Animation> animations;

    private VariableMap variables;

    public ActionBase(final List<Animation> animations, final VariableMap context) {
        this.animations = animations;
        this.variables = context;
    }

    @Override
    public String toString() {
        try {
            return "Action (" + getClass().getSimpleName() + "," + getName() + ")";
        } catch (final VariableException e) {
            return "Action (" + getClass().getSimpleName() + "," + null + ")";
        }
    }

    @Override
    public void init(final Mascot mascot) throws VariableException {
        this.setMascot(mascot);
        this.setTime(0);

        this.getVariables().put("mascot", mascot);
        this.getVariables().put("action", this);

        getVariables().init();

        for (final Animation animation : this.animations) {
            animation.init();
        }
    }

    @Override
    public void next() throws LostGroundException, VariableException {
        initFrame();
        tick();
    }

    private void initFrame() {

        getVariables().initFrame();

        for (final Animation animation : getAnimations()) {
            animation.initFrame();
        }
    }

    private List<Animation> getAnimations() {
        return this.animations;
    }

    protected abstract void tick() throws LostGroundException, VariableException;

    @Override
    public boolean hasNext() throws VariableException {

        final boolean effective = isEffective();
        final boolean intime = getTime() < getDuration();

        return effective && intime;
    }

    private Boolean isEffective() throws VariableException {
        return eval(PARAMETER_CONDITION, Boolean.class, DEFAULT_CONDITION);
    }

    private int getDuration() throws VariableException {
        return eval(PARAMETER_DURATION, Number.class, DEFAULT_DURATION).intValue();
    }

    protected Mascot getMascot() {
        return this.mascot;
    }

    private void setMascot(final Mascot mascot) {
        this.mascot = mascot;
    }

    protected int getTime() {
        return getMascot().getTime() - this.startTime;
    }

    protected void setTime(final int time) {
        this.startTime = getMascot().getTime() - time;
    }

    private String getName() throws VariableException {
        return this.eval("Name", String.class, null);
    }

    protected Animation getAnimation() throws VariableException {
        for (final Animation animation : getAnimations()) {
            if (animation.isEffective(getVariables())) {
                return animation;
            }
        }

        return null;
    }

    private VariableMap getVariables() {
        return this.variables;
    }

    protected void putVariable(final String key, final Object value) {
        synchronized (getVariables()) {
            getVariables().put(key, value);
        }
    }

    protected <T> T eval(final String name, final Class<T> type, final T defaultValue) throws VariableException {

        synchronized (getVariables()) {
            final Variable variable = getVariables().getRawMap().get(name);
            if (variable != null) {
                return type.cast(variable.get(getVariables()));
            }
        }

        return defaultValue;
    }

    protected MascotEnvironment getEnvironment() {
        return getMascot().getEnvironment();
    }
}

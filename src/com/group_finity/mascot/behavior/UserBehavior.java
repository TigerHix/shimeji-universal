package com.group_finity.mascot.behavior;

import com.group_finity.mascot.Mascot;
import com.group_finity.mascot.action.Action;
import com.group_finity.mascot.config.Configuration;
import com.group_finity.mascot.environment.MascotEnvironment;
import com.group_finity.mascot.exception.BehaviorInstantiationException;
import com.group_finity.mascot.exception.CantBeAliveException;
import com.group_finity.mascot.exception.LostGroundException;
import com.group_finity.mascot.exception.VariableException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simple Sample Behavior.
 * <p>
 * Original Author: Yuki Yamada of Group Finity (http://www.group-finity.com/Shimeji/)
 * Currently developed by Shimeji-ee Group.
 */
public class UserBehavior implements Behavior {
    public static final String BEHAVIORNAME_FALL = "Fall";
    public static final String BEHAVIORNAME_DRAGGED = "Dragged";
    public static final String BEHAVIORNAME_THROWN = "Thrown";
    private static final Logger log = Logger.getLogger(UserBehavior.class.getName());
    private final String name;

    private final Configuration configuration;

    private final Action action;

    private Mascot mascot;

    private boolean hidden;

    public UserBehavior(final String name, final Action action, final Configuration configuration, boolean hidden) {
        this.name = name;
        this.configuration = configuration;
        this.action = action;
        this.hidden = hidden;
    }

    @Override
    public String toString() {
        return "Behavior(" + getName() + ")";
    }

    @Override
    public synchronized void init(final Mascot mascot) throws CantBeAliveException {

        this.setMascot(mascot);

        log.log(Level.INFO, "Default Behavior({0},{1})", new Object[]{this.getMascot(), this});

        try {
            getAction().init(mascot);
            if (!getAction().hasNext()) {
                try {
                    mascot.setBehavior(this.getConfiguration().buildBehavior(getName(), mascot));
                } catch (final BehaviorInstantiationException e) {
                    throw new CantBeAliveException("Failed to initialize the following behavior", e);
                }
            }
        } catch (final VariableException e) {
            throw new CantBeAliveException("An error occurred in the evaluation of the variable", e);
        }

    }

    private Configuration getConfiguration() {
        return this.configuration;
    }

    private Action getAction() {
        return this.action;
    }

    private String getName() {
        return this.name;
    }

    /**
     * On Mouse Pressed.
     * Start dragging.
     *
     * @ Throws CantBeAliveException
     */
    public synchronized void mousePressed(final MouseEvent event) throws CantBeAliveException {

        if (SwingUtilities.isLeftMouseButton(event)) {
            // Begin dragging
            try {
                getMascot().setBehavior(this.getConfiguration().buildBehavior(BEHAVIORNAME_DRAGGED));
            } catch (final BehaviorInstantiationException e) {
                throw new CantBeAliveException("Failed to initialize the drag action", e);
            }
        }

    }

    /**
     * On Mouse Release.
     * End dragging.
     *
     * @ Throws CantBeAliveException
     */
    public synchronized void mouseReleased(final MouseEvent event) throws CantBeAliveException {

        if (SwingUtilities.isLeftMouseButton(event)) {
            // Termination of drag
            try {
                getMascot().setBehavior(this.getConfiguration().buildBehavior(BEHAVIORNAME_THROWN));
            } catch (final BehaviorInstantiationException e) {
                throw new CantBeAliveException("Failed to initialize the drop action", e);
            }
        }

    }

    @Override
    public synchronized void next() throws CantBeAliveException {

        try {
            if (getAction().hasNext()) {
                getAction().next();
            }

            if (getAction().hasNext()) {

                if ((getMascot().getBounds().getX() + getMascot().getBounds().getWidth() <=
                        getEnvironment().getScreen().getLeft())
                        || (getEnvironment().getScreen().getRight() <= getMascot().getBounds().getX())
                        || (getEnvironment().getScreen().getBottom() <= getMascot().getBounds().getY())) {

                    log.log(Level.INFO, "Out of the screen bounds({0},{1})", new Object[]{getMascot(), this});

                    getMascot().setAnchor(
                            new Point((int) (Math.random() * (getEnvironment().getScreen().getRight() - getEnvironment()
                                    .getScreen().getLeft()))
                                    + getEnvironment().getScreen().getLeft(), getEnvironment().getScreen().getTop() - 256));

                    try {
                        getMascot().setBehavior(this.getConfiguration().buildBehavior(BEHAVIORNAME_FALL));
                    } catch (final BehaviorInstantiationException e) {
                        throw new CantBeAliveException("Failed to initialize the falling action", e);
                    }
                }

            } else {
                log.log(Level.INFO, "Completed Behavior ({0},{1})", new Object[]{getMascot(), this});

                try {
                    getMascot().setBehavior(this.getConfiguration().buildBehavior(getName(), getMascot()));
                } catch (final BehaviorInstantiationException e) {
                    throw new CantBeAliveException("Failed to initialize the following actions", e);
                }
            }
        } catch (final LostGroundException e) {
            log.log(Level.INFO, "Lost Ground ({0},{1})", new Object[]{getMascot(), this});

            try {
                getMascot().setBehavior(this.getConfiguration().buildBehavior(BEHAVIORNAME_FALL));
            } catch (final BehaviorInstantiationException ex) {
                throw new CantBeAliveException("Failed to initialize the action falls", ex);
            }
        } catch (final VariableException e) {
            throw new CantBeAliveException("An error occurred in the evaluation of the variable", e);
        }

    }

    private Mascot getMascot() {
        return this.mascot;
    }

    private void setMascot(final Mascot mascot) {
        this.mascot = mascot;
    }

    protected MascotEnvironment getEnvironment() {
        return getMascot().getEnvironment();
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }
}

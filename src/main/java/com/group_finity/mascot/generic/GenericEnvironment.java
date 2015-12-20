package com.group_finity.mascot.generic;

import com.group_finity.mascot.environment.Area;
import com.group_finity.mascot.environment.Environment;

import java.awt.*;

/**
 * Java Environmental information is difficult to get used to get in the JNI.
 * <p>
 * Original Author: Yuki Yamada of Group Finity (http://www.group-finity.com/Shimeji/)
 * Currently developed by Shimeji-ee Group.
 */
class GenericEnvironment extends Environment {

    private Area activeWindow = new Area();

    @Override
    public void tick() {
        super.tick();
        this.activeWindow.setVisible(false);
    }

    @Override
    public void moveActiveWindow(final Point point) {
    }

    @Override
    public void restoreWindows() {

    }

    @Override
    public Area getWorkArea() {
        return getScreen();
    }

    @Override
    public Area getActiveWindow() {
        return this.activeWindow;
    }

    @Override
    public void refreshCache() {
        // I feel so refreshed
    }

}

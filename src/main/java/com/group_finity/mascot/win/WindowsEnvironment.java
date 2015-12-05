package com.group_finity.mascot.win;

import com.group_finity.mascot.Main;
import com.group_finity.mascot.environment.Area;
import com.group_finity.mascot.environment.Environment;
import com.group_finity.mascot.win.jna.Gdi32;
import com.group_finity.mascot.win.jna.RECT;
import com.group_finity.mascot.win.jna.User32;
import com.sun.jna.Pointer;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Original Author: Yuki Yamada of Group Finity (http://www.group-finity.com/Shimeji/)
 * Currently developed by Shimeji-ee Group.
 */
class WindowsEnvironment extends Environment {

    public static Area workArea = new Area();
    public static Area activeWindow = new Area();

    private static Map<Pointer, Boolean> windowCache = new LinkedHashMap<>();
    private static String[] willInteractWindows = null;
    private static Boolean willInteractAllWindows = null;

    private static Rectangle getWorkAreaRect() {
        final RECT rect = new RECT();
        User32.INSTANCE.SystemParametersInfoW(User32.SPI_GETWORKAREA, 0, rect, 0);
        return new Rectangle(rect.left, rect.top, rect.right - rect.left, rect.bottom - rect.top);
    }

    private static boolean willInteract(final Pointer window) {

        final Boolean cache = windowCache.get(window);
        if (cache != null) {
            return cache;
        }

        // FIXME not working :(
        if (willInteractAllWindows == null) {
            willInteractAllWindows = Boolean.valueOf(Main.getInstance().getProperties().getProperty("InteractWithAllWindows", "false"));
        }

        final char[] title = new char[1024];

        final int titleLength = User32.INSTANCE.GetWindowTextW(window, title, 1024);

        if (willInteractWindows == null) {
            willInteractWindows = Main.getInstance().getProperties().getProperty("InteractiveWindows", "").split("/");
        }

        for (String willInteractWindow : willInteractWindows) {
            if (willInteractAllWindows || (!willInteractWindow.trim().isEmpty() && new String(title, 0, titleLength).contains(willInteractWindow))) {
                windowCache.put(window, true);
                return true;
            }
        }

        final char[] className = new char[1024];

        final int classNameLength = User32.INSTANCE.GetClassNameW(window, className, 1024);

        if (new String(className, 0, classNameLength).contains("IMWindowClass")) {
            windowCache.put(window, true);
            return true;
        }

        windowCache.put(window, false);
        return false;
    }

    private static Pointer findActiveWindow() {

        Pointer window = User32.INSTANCE.GetWindow(User32.INSTANCE.GetForegroundWindow(), User32.GW_HWNDFIRST);

        while (User32.INSTANCE.IsWindow(window) != 0) {

            if (User32.INSTANCE.IsWindowVisible(window) != 0) {
                if ((User32.INSTANCE.GetWindowLongW(window, User32.GWL_STYLE) & User32.WS_MAXIMIZE) != 0) {
                    return null;
                }

                if (willInteract(window) && (User32.INSTANCE.IsIconic(window) == 0)) {
                    break;
                }
            }

            window = User32.INSTANCE.GetWindow(window, User32.GW_HWNDNEXT);

        }

        if (User32.INSTANCE.IsWindow(window) == 0) {
            return null;
        }

        return window;
    }

    private static Rectangle getActiveWindowRect() {

        final Pointer window = findActiveWindow();

        final RECT out = new RECT();
        User32.INSTANCE.GetWindowRect(window, out);
        final RECT in = new RECT();
        if (getWindowRgnBox(window, in) == User32.ERROR) {
            in.left = 0;
            in.top = 0;
            in.right = out.right - out.left;
            in.bottom = out.bottom - out.top;
        }

        return new Rectangle(out.left + in.left, out.top + in.top, in.Width(), in.Height());
    }

    private static int getWindowRgnBox(final Pointer window, final RECT rect) {

        Pointer hRgn = Gdi32.INSTANCE.CreateRectRgn(0, 0, 0, 0);
        try {
            if (User32.INSTANCE.GetWindowRgn(window, hRgn) == User32.ERROR) {
                return User32.ERROR;
            }
            Gdi32.INSTANCE.GetRgnBox(hRgn, rect);
            return 1;
        } finally {
            Gdi32.INSTANCE.DeleteObject(hRgn);
        }
    }

    private static boolean moveWindow(final Pointer window, final Rectangle rect) {

        if (window == null) {
            return false;
        }

        final RECT out = new RECT();
        User32.INSTANCE.GetWindowRect(window, out);
        final RECT in = new RECT();
        if (getWindowRgnBox(window, in) == User32.ERROR) {
            in.left = 0;
            in.top = 0;
            in.right = out.right - out.left;
            in.bottom = out.bottom - out.top;
        }

        User32.INSTANCE.MoveWindow(window, rect.x - in.left, rect.y - in.top, rect.width + out.Width() - in.Width(),
                rect.height + out.Height() - in.Height(), 1);

        return true;
    }

    private static void restoreAllWindows() {

        final RECT workArea = new RECT();
        User32.INSTANCE.SystemParametersInfoW(User32.SPI_GETWORKAREA, 0, workArea, 0);

        Pointer window = User32.INSTANCE.GetWindow(User32.INSTANCE.GetForegroundWindow(), User32.GW_HWNDFIRST);

        while (User32.INSTANCE.IsWindow(window) != 0) {
            if (willInteract(window)) {

                final RECT rect = new RECT();
                User32.INSTANCE.GetWindowRect(window, rect);
                if ((rect.right <= workArea.left + 100) || (rect.bottom <= workArea.top + 100)
                        || (rect.left >= workArea.right - 100) || (rect.top >= workArea.bottom - 100)) {

                    rect.OffsetRect(workArea.left + 100 - rect.left, workArea.top + 100 - rect.top);
                    User32.INSTANCE.MoveWindow(window, rect.left, rect.top, rect.Width(), rect.Height(), 1);
                    User32.INSTANCE.BringWindowToTop(window);
                }

                break;
            }

            window = User32.INSTANCE.GetWindow(window, User32.GW_HWNDNEXT);
        }
    }

    @Override
    public void tick() {
        super.tick();
        workArea.set(getWorkAreaRect());

        final Rectangle ieRect = getActiveWindowRect();
        activeWindow.setVisible((ieRect != null) && ieRect.intersects(getScreen().toRectangle()));
        activeWindow.set(ieRect == null ? new Rectangle(-1, -1, 0, 0) : ieRect);

    }

    @Override
    public void moveActiveWindow(final Point point) {
        moveWindow(findActiveWindow(), new Rectangle(point.x, point.y, activeWindow.getWidth(), activeWindow.getHeight()));
    }

    @Override
    public void restoreWindows() {
        restoreAllWindows();
    }

    @Override
    public Area getWorkArea() {
        return workArea;
    }

    @Override
    public Area getActiveWindow() {
        return activeWindow;
    }

    @Override
    public void refreshCache() {
        windowCache.clear(); // will be repopulated next willInteract call
        willInteractWindows = null;
        willInteractAllWindows = null;
    }

}

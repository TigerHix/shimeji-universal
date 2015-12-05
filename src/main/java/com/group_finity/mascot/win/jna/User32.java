package com.group_finity.mascot.win.jna;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.win32.StdCallLibrary;

/**
 * Original Author: Yuki Yamada of Group Finity (http://www.group-finity.com/Shimeji/)
 * Currently developed by Shimeji-ee Group.
 */

public interface User32 extends StdCallLibrary {

    User32 INSTANCE = (User32) Native.loadLibrary("User32", User32.class);

    int SM_CXSCREEN = 0;
    int SM_CYSCREEN = 1;
    int SPI_GETWORKAREA = 48;
    int GW_HWNDFIRST = 0;
    int GW_HWNDNEXT = 2;
    int GWL_STYLE = -16;
    int GWL_EXSTYLE = -20;
    int WS_MAXIMIZE = 0x01000000;
    int WS_EX_LAYERED = 0x00080000;
    int ERROR = 0;
    int ULW_ALPHA = 2;

    int GetSystemMetrics(int nIndex);

    int SystemParametersInfoW(int uiAction, int uiParam, RECT pvParam, int fWinIni);

    Pointer GetForegroundWindow();

    Pointer GetWindow(Pointer hWnd, int uCmd);

    int IsWindow(Pointer hWnd);

    int IsWindowVisible(Pointer hWnd);

    int GetWindowLongW(Pointer hWnd, int nIndex);

    int SetWindowLongW(Pointer hWnd, int nIndex, int dwNewLong);

    int IsIconic(Pointer hWnd);

    int GetWindowTextW(Pointer hWnd, char[] lpString, int nMaxCount);

    int GetClassNameW(Pointer hWnd, char[] lpString, int nMaxCount);

    int GetWindowRect(Pointer hWnd, RECT lpRect);

    int GetWindowRgn(Pointer hWnd, Pointer hRgn);

    int MoveWindow(Pointer hWnd, int X, int Y, int nWidth, int nHeight, int bRepaint);

    int BringWindowToTop(Pointer hWnd);

    Pointer GetDC(Pointer hWnd);

    int ReleaseDC(Pointer hWnd, Pointer hDC);

    int UpdateLayeredWindow(Pointer hWnd, Pointer hdcDst,
                            POINT pptDst, SIZE psize,
                            Pointer hdcSrc, POINT pptSrc, int crKey,
                            BLENDFUNCTION pblend, int dwFlags);

    boolean EnumWindows(WNDENUMPROC lpEnumFunc, Pointer arg);

    interface WNDENUMPROC extends StdCallCallback {
        /**
         * Return whether to continue enumeration.
         */
        boolean callback(Pointer hWnd, Pointer arg);
    }

}

package org.fastj.thunder.toolwindow;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public class Icons {
    private Icons() {
        throw new IllegalStateException();
    }
    public static final Icon THUNDER_ICON = IconLoader.findIcon("/icons/bolt-13.png");
}

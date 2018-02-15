package com.company.demotodo.web.screens;

import com.haulmont.cuba.gui.WindowManager.OpenType;
import com.haulmont.cuba.web.app.mainwindow.AppMainWindow;

public class ExtAppMainWindow extends AppMainWindow {
    @Override
    public void ready() {
        super.ready();

        openWindow("todoscreen", OpenType.NEW_TAB);
    }
}
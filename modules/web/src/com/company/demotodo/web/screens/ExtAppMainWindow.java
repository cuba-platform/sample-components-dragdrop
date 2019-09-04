package com.company.demotodo.web.screens;

import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.screen.OpenMode;
import com.haulmont.cuba.web.app.mainwindow.AppMainWindow;

import javax.inject.Inject;

public class ExtAppMainWindow extends AppMainWindow {

    @Inject
    private ScreenBuilders screenBuilders;

    @Override
    public void ready() {
        super.ready();

        screenBuilders.screen(this)
                .withScreenId("TodoScreen")
                .withOpenMode(OpenMode.NEW_TAB)
                .build()
                .show();
    }
}
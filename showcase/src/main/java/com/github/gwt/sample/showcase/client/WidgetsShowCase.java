/*
* Copyright 2014 Rinat Enikeev
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.github.gwt.sample.showcase.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.TreeViewModel;

/**
 * Main scene of showcase.
 *
 * @author      Rinat Enikeev (rinat.enikeev[at]gmail[dot]com)
 * @version     %I%, %G%
 * @since       1.0
 */
public class WidgetsShowCase extends ResizeComposite {
    interface WidgetsShowCaseUiBinder extends UiBinder<DockLayoutPanel, WidgetsShowCase> {
    }

    private static WidgetsShowCaseUiBinder ourUiBinder = GWT.create(WidgetsShowCaseUiBinder.class);

    /**
     * The panel that holds the content.
     */
    @UiField
    SimpleLayoutPanel contentPanel;

    /**
     * The main menu used to navigate to examples.
     */
    @UiField(provided = true)
    CellTree mainMenu;

    /**
     * The current {@link com.google.gwt.user.client.ui.Widget} being displayed.
     */
    private Widget content;


    /**
     * Construct the {@link WidgetsShowCase}.
     *
     * @param treeModel the treeModel that backs the main menu
     */
    public WidgetsShowCase(TreeViewModel treeModel) {

        // Create the cell tree.
        mainMenu = new CellTree(treeModel, null);
        mainMenu.setAnimationEnabled(true);
        mainMenu.setKeyboardSelectionPolicy(HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.DISABLED);
        mainMenu.ensureDebugId("mainMenu");

        // Initialize the ui binder.
        initWidget(ourUiBinder.createAndBindUi(this));

        // Default to no content.
        contentPanel.ensureDebugId("contentPanel");
        setContent(null);
    }

    /**
     * Get the main menu used to select examples.
     *
     * @return the main menu
     */
    public CellTree getMainMenu() {
        return mainMenu;
    }

    /**
     * Set the content to display.
     *
     * @param content the content
     */
    public void setContent(final Widget content) {
        this.content = content;
        if (content == null) {
            contentPanel.setWidget(null);
            return;
        }

        // Show the widget.
        showExample();
    }


    /**
     * Show a example.
     */
    private void showExample() {
        if (content == null) {
            return;
        }
        contentPanel.setWidget(content);
    }

}
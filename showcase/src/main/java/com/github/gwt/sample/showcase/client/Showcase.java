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

import com.github.gwt.sample.showcase.client.contents.LeftMenuTreeViewModel;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.TreeNode;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * Entry point for DateTimeBox showcase.
 *
 * @author      Rinat Enikeev (rinat.enikeev[at]gmail[dot]com)
 * @version     %I%, %G%
 * @since       1.0
 */
public class Showcase implements EntryPoint {

    private WidgetsShowCase showCase;

    public void onModuleLoad() {

        final SingleSelectionModel<Widget> selectionModel = new SingleSelectionModel<Widget>();
        final LeftMenuTreeViewModel treeModel = new LeftMenuTreeViewModel(selectionModel);
        showCase = new WidgetsShowCase(treeModel);

        RootLayoutPanel.get().add(showCase);

        bindHistory(selectionModel, treeModel);
        showInitialExample(selectionModel);
    }

    private void bindHistory(final SingleSelectionModel<Widget> selectionModel, final LeftMenuTreeViewModel treeModel) {

        // Setup a history handler to reselect the associate menu item.
        final CellTree mainMenu = showCase.getMainMenu();

        // Change the history token when a main menu item is selected.
        selectionModel.addSelectionChangeHandler(
                new SelectionChangeEvent.Handler() {
                    public void onSelectionChange(SelectionChangeEvent event) {
                        Widget selected = selectionModel.getSelectedObject();
                        if (selected != null) {
                            History.newItem(selected.getTitle(), true);
                        }
                    }
                });

        final ValueChangeHandler<String> historyHandler = new ValueChangeHandler<
                String>() {
            public void onValueChange(ValueChangeEvent<String> event) {
                // Get the content widget associated with the history token.
                Widget contentWidget = treeModel.getWidget(event.getValue());
                if (contentWidget == null) {
                    return;
                }

                // Expand the tree node associated with the content.
                LeftMenuTreeViewModel.Category category = treeModel.getCategoryForWidget(
                        contentWidget);
                TreeNode node = mainMenu.getRootTreeNode();
                int childCount = node.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    if (node.getChildValue(i) == category) {
                        node.setChildOpen(i, true, true);
                        break;
                    }
                }

                // Select the node in the tree.
                selectionModel.setSelected(contentWidget, true);

                // Display the content widget.
                displayContentWidget(contentWidget);
            }

            /**
             * Set the content to the {@link com.google.gwt.user.client.ui.Widget}.
             *
             * @param content the {@link com.google.gwt.user.client.ui.Widget} to display
             */
            private void displayContentWidget(Widget content) {
                if (content == null) {
                    return;
                }

                showCase.setContent(content);
                Window.setTitle("Showcase of Features: " + content.getTitle());
            }
        };
        History.addValueChangeHandler(historyHandler);
    }

    private void showInitialExample(final SingleSelectionModel<Widget> selectionModel) {
        final CellTree mainMenu = showCase.getMainMenu();
        // Show the initial example.
        if (History.getToken().length() > 0) {
            History.fireCurrentHistoryState();
        } else {

            // Use the first token available.
            TreeNode root = mainMenu.getRootTreeNode();
            TreeNode category = root.setChildOpen(0, true);
            Widget content = (Widget) category.getChildValue(0);
            selectionModel.setSelected(content, true);
        }
    }

}

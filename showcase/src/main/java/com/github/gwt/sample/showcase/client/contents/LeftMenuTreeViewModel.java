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

package com.github.gwt.sample.showcase.client.contents;

import com.github.gwt.user.client.ui.DateTimeBox;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.TreeViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the tree model for left menu of showcase.
 *
 * @author      Rinat Enikeev (rinat.enikeev[at]gmail[dot]com)
 * @version     %I%, %G%
 * @since       1.0
 */
public class LeftMenuTreeViewModel implements TreeViewModel {

    /**
     * The cell used to render categories.
     */
    private static class CategoryCell extends AbstractCell<Category> {
        @Override
        public void render(Context context, Category value, SafeHtmlBuilder sb) {
            if (value != null) {
                sb.appendEscaped(value.getName());
            }
        }
    }

    /**
     * The cell used to render examples.
     */
    private static class WidgetCell extends AbstractCell<Widget> {
        @Override
        public void render(Context context, Widget value, SafeHtmlBuilder sb) {
            if (value != null) {
                sb.appendEscaped(value.getTitle());
            }
        }
    }

    /**
     * A top level category in the tree.
     */
    public class Category {

        private final ListDataProvider<Widget> examples =
                new ListDataProvider<Widget>();
        private final String name;
        private NodeInfo<Widget> nodeInfo;

        public Category(String name) {
            this.name = name;
        }

        public void addExample(Widget example) {
            examples.getList().add(example);
            widgetToCategory.put(example, this);
            titleToWidget.put(example.getTitle(), example);
        }

        public String getName() {
            return name;
        }

        /**
         * Get the node info for the examples under this category.
         *
         * @return the node info
         */
        public NodeInfo<Widget> getNodeInfo() {
            if (nodeInfo == null) {
                nodeInfo = new DefaultNodeInfo<Widget>(examples,
                        widgetCell, selectionModel, null);
            }
            return nodeInfo;
        }
    }

    /**
     * A mapping of {@link com.google.gwt.user.client.ui.Widget}s to their associated categories.
     */
    private final Map<Widget, Category> widgetToCategory = new HashMap<Widget, Category>();

    /**
     * Titles are used to navigate in {@link com.google.gwt.user.client.History}
     */
    private final Map<String, Widget> titleToWidget = new HashMap<String, Widget>();

    /**
     * The top level categories.
     */
    private final ListDataProvider<Category> categories = new ListDataProvider<Category>();

    /**
     * The cell used to render examples.
     */
    private final WidgetCell widgetCell = new WidgetCell();


    /**
     * The selection model used to select examples.
     */
    private final SelectionModel<Widget> selectionModel;

    public LeftMenuTreeViewModel(SelectionModel<Widget> selectionModel) {
        this.selectionModel = selectionModel;
        initializeTree();
    }

    public <T> NodeInfo<?> getNodeInfo(T value) {
        if (value == null) {
            // Return the top level categories.
            return new DefaultNodeInfo<Category>(categories, new CategoryCell());
        } else if (value instanceof Category) {
            // Return the examples within the category.
            Category category = (Category) value;
            return category.getNodeInfo();
        }
        return null;
    }

    public boolean isLeaf(Object value) {
        return value != null && !(value instanceof Category);
    }

    public Widget getWidget(final String value) {
        return titleToWidget.get(value);
    }

    public Category getCategoryForWidget(final Widget contentWidget) {
        return widgetToCategory.get(contentWidget);
    }

    /**
     * Initialize the top level categories in the tree.
     */
    private void initializeTree() {
        List<Category> catList = categories.getList();

        // DateTimeBox.
        {
            Category category = new Category("DateTimeBox");
            catList.add(category);

            HorizontalPanel widgets = new HorizontalPanel();
            widgets.setWidth("100%");
            widgets.setHeight("100%");
            widgets.setTitle("DateTimeBox");

            DateTimeBox dateTimeBox = new DateTimeBox();
            dateTimeBox.setTitle("DateTimeBox");
            widgets.add(dateTimeBox);

            category.addExample(widgets);
        }

        // ClearBox.
        {
            Category category = new Category("ClearBox");
            catList.add(category);
        }

        // DateTimePicker
        {
            Category category = new Category("DateTimePicker");
            catList.add(category);

        }

        // CellGrid
        {
            Category category = new Category("CellGrid");
            catList.add(category);
        }

        // LayerPanel
        {
            Category category = new Category("LayerPanel");
            catList.add(category);
        }
    }
}

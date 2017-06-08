package com.company.demotodo.web.screens;

import com.google.common.collect.Iterables;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.web.theme.HaloTheme;
import com.haulmont.addon.dnd.components.DDVerticalLayout;
import com.haulmont.addon.dnd.components.DDVerticalLayoutTargetDetails;
import com.haulmont.addon.dnd.components.DropHandler;
import com.haulmont.addon.dnd.components.LayoutBoundTransferable;
import com.haulmont.addon.dnd.components.acceptcriterion.AcceptCriterion;
import com.haulmont.addon.dnd.components.dragevent.DragAndDropEvent;
import com.haulmont.addon.dnd.components.enums.VerticalDropLocation;


import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TodoScreen extends AbstractWindow {

    @Inject
    private ComponentsFactory factory;

    @Inject
    private DDVerticalLayout dashboard;

    @Inject
    private DDVerticalLayout palette;

    private List<String> contactsList;
    private List<String> goodsList;
    private List<String> restaurantsList;

    @Override
    public void init(Map<String, Object> params) {
        initSampleData();
        addCreateActions();

        dashboard.setDropHandler(new DropHandler() {
            @Override
            public void drop(DragAndDropEvent event) {
                LayoutBoundTransferable t = (LayoutBoundTransferable) event.getTransferable();
                DDVerticalLayoutTargetDetails details = (DDVerticalLayoutTargetDetails) event.getTargetDetails();

                Component sourceLayout = t.getSourceComponent();
                DDVerticalLayout targetLayout = (DDVerticalLayout) details.getTarget();
                Component tComponent = t.getTransferableComponent();

                VerticalDropLocation loc = details.getDropLocation();

                int indexTo = details.getOverIndex();
                int indexFrom = targetLayout.indexOf(tComponent);

                if (tComponent == null) {
                    return;
                }
                Component componentToAdd;

                if (sourceLayout == targetLayout) {
                    componentToAdd = tComponent;
                    if (indexFrom == indexTo) {
                        return;
                    }
                    targetLayout.remove(tComponent);
                    if (indexTo > indexFrom) {
                        indexTo--;
                    }
                    if (indexTo == -1) {
                        targetLayout.add(tComponent, indexFrom);
                    }
                } else {
                    componentToAdd = createDashboardElement(tComponent);
                    if (indexTo == -1) {
                        targetLayout.add(componentToAdd, targetLayout.getOwnComponents().size());
                    }
                }

                if (indexTo != -1) {
                    if (loc == VerticalDropLocation.MIDDLE || loc == VerticalDropLocation.BOTTOM) {
                        indexTo++;
                    }
                    targetLayout.add(componentToAdd, indexTo);
                }
                updateDashboardComponents(targetLayout);
            }

            @Override
            public AcceptCriterion getCriterion() {
                return AcceptCriterion.ACCEPT_ALL;
            }
        });
    }

    public Component createDashboardElement(Component component) {
        HBoxLayout layout = factory.createComponent(HBoxLayout.class);
        layout.setStyleName("dashboard-" + component.getId());
        layout.setWidth("100%");
        layout.setSpacing(true);

        Label countLabel = factory.createComponent(Label.class);
        countLabel.setStyleName("label-count");
        countLabel.setWidth("30px");

        Label titleLabel = factory.createComponent(Label.class);
        titleLabel.setValue(((Button) component).getCaption());
        titleLabel.setStyleName("h1");
        titleLabel.setWidth("85px");

        layout.add(countLabel);
        layout.add(titleLabel);

        Component field = createField(component.getId());
        layout.add(field);

        Button deleteButton = factory.createComponent(Button.class);
        deleteButton.addStyleName(HaloTheme.BUTTON_BORDERLESS_COLORED);
        deleteButton.addStyleName("button-remove");
        deleteButton.setIcon("font-icon:TIMES");
        BaseAction action = new BaseAction("remove") {
            @Override
            public void actionPerform(Component component) {
                Component.Container hBox = (Component.Container) component.getParent();
                DDVerticalLayout ddLayout = (DDVerticalLayout) hBox.getParent();
                ddLayout.remove(hBox);
                updateDashboardComponents(ddLayout);
            }
        };
        action.setCaption("");
        deleteButton.setAction(action);

        layout.add(deleteButton);
        layout.expand(field);

        return layout;
    }

    private Component createField(String id) {
        Component field;
        if (id.equals("other")) {
            field = factory.createComponent(TextField.class);
        } else {
            field = factory.createComponent(LookupField.class);
            if (id.equals("call") || id.equals("chat") || id.equals("meeting")) {
                ((LookupField) field).setOptionsList(contactsList);
            } else if (id.equals("dinner")) {
                ((LookupField) field).setOptionsList(restaurantsList);
            } else if (id.equals("buy")) {
                ((LookupField) field).setOptionsList(goodsList);
            }
        }
        return field;
    }

    public void addCreateActions(){
        List<Component> buttonsList = new ArrayList<>(palette.getOwnComponents());
        for (Component component : buttonsList) {
            BaseAction createAction = new BaseAction("create") {
                @Override
                public void actionPerform(Component component) {
                    Component componentToAdd = createDashboardElement(component);
                    dashboard.add(componentToAdd);
                    updateDashboardComponents(dashboard);
                }
            };
            createAction.setCaption("");
            ((Button) component).setAction(createAction);
        }
    }

    private void initSampleData(){
        contactsList = new ArrayList<>();
        contactsList.add("Lewis Moore");
        contactsList.add("Martin Williams");
        contactsList.add("Taylor Pitts");

        restaurantsList = new ArrayList<>();
        restaurantsList.add("Eastern cuisine");
        restaurantsList.add("Russian cuisine");
        restaurantsList.add("English cuisine");
        restaurantsList.add("German cuisine");

        goodsList = new ArrayList<>();
        goodsList.add("Teapot");
        goodsList.add("Yoga mat");
        goodsList.add("Washing machine");
        goodsList.add("Sneakers");
        goodsList.add("Milk");
    }

    public void updateDashboardComponents(DDVerticalLayout layout) {
        List<Component> components = new ArrayList<>(layout.getOwnComponents());
        int count = 0;
        for (Component component : components) {
            BoxLayout lineLayout = (BoxLayout) component;
            Label countLabel = (Label) Iterables.get(lineLayout.getComponents(), 0);
            countLabel.setValue(++count);
        }
    }
}
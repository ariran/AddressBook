package fi.nic.ariran.addressbook.ui;

import java.util.Collections;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import fi.nic.ariran.addressbook.domain.AddressBookItem;
import fi.nic.ariran.addressbook.persistence.PersistenceService;
import fi.nic.ariran.addressbook.persistence.PersistenceServiceFactory;

@SuppressWarnings("serial")
@Theme("addressbook")
public class AddressBookUI extends UI {
	
	PersistenceService persistSvc = PersistenceServiceFactory.getInstance();

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = AddressBookUI.class)
	public static class Servlet extends VaadinServlet {
	}

	@Override
	protected void init(VaadinRequest request) {
		final VerticalLayout layout = new VerticalLayout();

		layout.addComponent(getMenuBar());
		layout.addComponent(getMainLayout());
		
		layout.setMargin(true);
		setContent(layout);
	}

	@Override
	public void close() {
		setContent(new Label("Voit sulkea tämän ikkunan."));
		persistSvc.close();
		super.close();
	}

	private Component getMenuBar() {
		MenuBar menuBar = new MenuBar();
		menuBar.setWidth(100.0f, Unit.PERCENTAGE);
		
		Command openSearchCommand = new Command() {
			@Override
			public void menuSelected(MenuItem selectedItem) {
				searchItems();
			}
		};
		menuBar.addItem("Hae", openSearchCommand);
		
		Command openAddNewDialogCommand = new Command() {
			@Override
			public void menuSelected(MenuItem selectedItem) {
				addWindow(getAddNewDialogWindow());
			}
		};
		menuBar.addItem("Uusi", openAddNewDialogCommand);
		
		Command closeCommand = new Command() {
			@Override
			public void menuSelected(MenuItem selectedItem) {
				close();
			}
		};
		menuBar.addItem("Sulje", closeCommand);

		return menuBar;
	}
	
	private Component getMainLayout() {
		Iterable<AddressBookItem> allItems = persistSvc.getAllItems();
		BeanContainer<String, AddressBookItem> beanContainer
			= new BeanContainer<String, AddressBookItem>(AddressBookItem.class);
		beanContainer.setBeanIdProperty("internalId");
		
		for (AddressBookItem item : allItems) {
			beanContainer.addBean(item);
		}

		Table table = new Table();
		table.setContainerDataSource(beanContainer);
		table.setSizeFull();
		table.setSelectable(true);
		table.setMultiSelect(false);
		table.setImmediate(true);
		table.setColumnReorderingAllowed(false);
        table.setColumnCollapsingAllowed(false);
        table.setVisibleColumns(new Object[] {"lastName", "firstNames"});
        table.setColumnHeaders(new String[] {"Sukunimi", "Etunimi"});
        
		HorizontalLayout hl = new HorizontalLayout();
		hl.setSizeFull();
		hl.addComponent(table);
		
		hl.addComponent(new Label());
		return hl;
	}
	
	protected void searchItems() {
		Notification.show("Not implemented!");
	}
	
	protected Window getAddNewDialogWindow() {
		final AddressBookItem bean = new AddressBookItem(null, "", "");
        final BeanItem<AddressBookItem> item = new BeanItem<AddressBookItem>(bean);
        FieldGroup fieldGroup = new FieldGroup(item);
        fieldGroup.setBuffered(false);
        
        final Window addNewWindow = new Window("Lisää uusi");
        addNewWindow.setModal(true);
        FormLayout formLayout = new FormLayout();
        formLayout.setMargin(true);
        
        TextField lastNameField = new TextField("Sukunimi");
        lastNameField.setImmediate(true);
        formLayout.addComponent(lastNameField);
        fieldGroup.bind(lastNameField, "lastName");
        
        TextField firstNamesField = new TextField("Etunimet");
        firstNamesField.setImmediate(true);
        formLayout.addComponent(firstNamesField);
        fieldGroup.bind(firstNamesField, "firstNames");
        
        TextField extraInfoField = new TextField("Lisätieto");
        extraInfoField.setImmediate(true);
        formLayout.addComponent(extraInfoField);
        fieldGroup.bind(extraInfoField, "extraInfo");
        
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setMargin(true);
        buttonLayout.setSpacing(true);
        
        Button cancelButton = new Button("Peruuta");
        cancelButton.addClickListener(new ClickListener() {
        	
        	@Override
        	public void buttonClick(ClickEvent event) {
        		addNewWindow.close();
        	}
        });
        buttonLayout.addComponent(cancelButton);
        
        Button saveButton = new Button("Tallenna");
        saveButton.addClickListener(new ClickListener() {
        	
        	@Override
        	public void buttonClick(ClickEvent event) {
        		addNewWindow.close();
        		persistSvc.insertNewItem(bean);
        	}
        });
        buttonLayout.addComponent(saveButton);
        formLayout.addComponent(buttonLayout);
        
        addNewWindow.setContent(formLayout);
        
        // Center it in the browser window
        addNewWindow.center();

		return addNewWindow; 
	}
}
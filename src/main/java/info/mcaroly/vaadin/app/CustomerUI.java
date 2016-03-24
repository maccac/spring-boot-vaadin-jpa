package info.mcaroly.vaadin.app;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.SelectionEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.annotation.WebServlet;

@SpringUI
@Theme("mytheme")
public class CustomerUI extends UI {

    private final CustomerService customerService;
    private CustomerForm customerForm;
    private Grid grid = new Grid();
    private TextField filterTextField = new TextField();

    @Autowired
    public CustomerUI(CustomerService customerService) {
        this.customerService = customerService;
        this.customerForm = new CustomerForm(this, customerService);
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        Button clearTextFilterButton = new Button(FontAwesome.TIMES);
        clearTextFilterButton.setDescription("Clear the current filter");
        clearTextFilterButton.addClickListener(this::clearTextFilterListener);

        CssLayout filtering = new CssLayout();
        filtering.addComponents(filterTextField, clearTextFilterButton);
        filtering.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

        Button addCustomerButton = new Button("Add new customer");
        addCustomerButton.addClickListener(this::addCustomerListener);

        HorizontalLayout toolbar = new HorizontalLayout(filtering, addCustomerButton);
        toolbar.setSpacing(true);

        customerForm.setVisible(false);

        HorizontalLayout main = new HorizontalLayout(grid, customerForm);
        main.setSpacing(true);
        main.setSizeFull();
        grid.setSizeFull();
        main.setExpandRatio(grid, 1);

        grid.addSelectionListener(this::customerSelectedListener);

        filterTextField.setInputPrompt("Filter by name...");
        filterTextField.addTextChangeListener(this::searchFilterListener);

        final VerticalLayout layout = new VerticalLayout();
        layout.addComponents(toolbar, main);
        layout.setSpacing(true);
        grid.setColumns("firstName", "lastName", "email");
        updateCustomerList();

        layout.setMargin(true);

        setContent(layout);
    }

    private void addCustomerListener(Button.ClickEvent clickEvent) {
        grid.select(null);
        customerForm.setCustomer(new Customer());
    }

    private void customerSelectedListener(SelectionEvent selectionEvent) {
        if (selectionEvent.getSelected().isEmpty()) {
            customerForm.setVisible(false);
        } else {
            Customer customer = (Customer) selectionEvent.getSelected().iterator().next();
            customerForm.setCustomer(customer);
        }
    }

    private void clearTextFilterListener(Button.ClickEvent clickEvent) {
        filterTextField.clear();
        updateCustomerList();
    }

    private void searchFilterListener(FieldEvents.TextChangeEvent textChangeEvent) {
        grid.setContainerDataSource(new BeanItemContainer<>(Customer.class, customerService.findAll(textChangeEvent.getText())));
    }

    public void updateCustomerList() {
        grid.setContainerDataSource(new BeanItemContainer<>(Customer.class, customerService.findAll(filterTextField.getValue())));
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = CustomerUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}

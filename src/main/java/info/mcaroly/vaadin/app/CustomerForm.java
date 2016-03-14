package info.mcaroly.vaadin.app;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Created by mcaroly on 12/03/2016.
 */
public class CustomerForm extends FormLayout {

    private TextField firstName = new TextField("First name");
    private TextField lastName = new TextField("Last name");
    private TextField email = new TextField("Email");
    private NativeSelect status = new NativeSelect("Status");
    private PopupDateField birthdate = new PopupDateField("Birthday");
    private Button save = new Button("Save");
    private Button delete = new Button("Delete");

    private CustomerService service;
    private Customer customer;
    private CustomerUI customerUI;

    public CustomerForm(CustomerUI customerUI, CustomerService service) {
        this.customerUI = customerUI;
        this.service = service;

        setSizeUndefined();
        HorizontalLayout buttons = new HorizontalLayout(save, delete);
        buttons.setSpacing(true);
        addComponents(firstName, lastName, email, status, birthdate, buttons);

        status.addItems(CustomerStatus.values());

        save.setStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setClickShortcut(ShortcutAction.KeyCode.ENTER);

        save.addClickListener(this::save);
        delete.addClickListener(this::delete);
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        BeanFieldGroup.bindFieldsUnbuffered(customer, this);

        // Show delete button for only customers already in the database
        delete.setVisible(customer.isPersisted());
        setVisible(true);
        firstName.selectAll();
    }

    private void delete(Button.ClickEvent clickEvent) {
        service.delete(customer);
        customerUI.updateCustomerList();
        setVisible(false);
    }

    private void save(Button.ClickEvent clickEvent) {
        service.save(customer);
        customerUI.updateCustomerList();
        setVisible(false);
    }



}

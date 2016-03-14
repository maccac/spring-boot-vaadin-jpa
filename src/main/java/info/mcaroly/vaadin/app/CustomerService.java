package info.mcaroly.vaadin.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.util.stream.StreamSupport.stream;

@Service
public class CustomerService {

    private static final Logger LOGGER = Logger.getLogger(CustomerService.class.getName());

    private CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
        ensureTestData();
    }

    public synchronized List<Customer> findAll() {
        return stream(customerRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }

    public synchronized List<Customer> findAll(String stringFilter) {
        return stream(customerRepository.findNamesLike(stringFilter.toUpperCase()).spliterator(), false).collect(Collectors.toList());
    }

    public synchronized void delete(Customer value) {
        customerRepository.delete(value);
    }

    public synchronized void save(Customer entry) {
        if (entry == null) {
            LOGGER.log(Level.SEVERE,
                    "Customer is null. Are you sure you have connected your form to the application as described in tutorial chapter 7?");
            return;
        }
        customerRepository.save(entry);
    }

    public void ensureTestData() {
        if (findAll().isEmpty()) {
            final String[] names = new String[]{"Gabrielle Patel", "Brian Robinson", "Eduardo Haugen",
                    "Koen Johansen", "Alejandro Macdonald", "Angel Karlsson", "Yahir Gustavsson", "Haiden Svensson",
                    "Emily Stewart", "Corinne Davis", "Ryann Davis", "Yurem Jackson", "Kelly Gustavsson",
                    "Eileen Walker", "Katelyn Martin", "Israel Carlsson", "Quinn Hansson", "Makena Smith",
                    "Danielle Watson", "Leland Harris", "Gunner Karlsen", "Jamar Olsson", "Lara Martin",
                    "Ann Andersson", "Remington Andersson", "Rene Carlsson", "Elvis Olsen", "Solomon Olsen",
                    "Jaydan Jackson", "Bernard Nilsen"};
            Random r = new Random(0);
            for (String name : names) {
                String[] split = name.split(" ");
                Customer c = new Customer();
                c.setFirstName(split[0]);
                c.setLastName(split[1]);
                c.setEmail(split[0].toLowerCase() + "@" + split[1].toLowerCase() + ".com");
                c.setStatus(CustomerStatus.values()[r.nextInt(CustomerStatus.values().length)]);
                Calendar cal = Calendar.getInstance();
                int daysOld = 0 - r.nextInt(365 * 15 + 365 * 60);
                cal.add(Calendar.DAY_OF_MONTH, daysOld);
                c.setBirthDate(cal.getTime());
                customerRepository.save(c);
            }
        }
    }

}
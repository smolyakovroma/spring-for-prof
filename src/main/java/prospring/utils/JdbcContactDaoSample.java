package prospring.utils;

import org.springframework.context.support.GenericXmlApplicationContext;
import prospring.beans.Contact;
import prospring.beans.ContactTelDetail;
import prospring.dao.ContactDao;

import java.sql.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class JdbcContactDaoSample {
    public static void main(String[] args) {
        GenericXmlApplicationContext ctx = new GenericXmlApplicationContext();
        ctx.load("classpath:config/spring-config.xml");
        ctx.refresh();

        ContactDao contactDao = ctx.getBean("contactDao", ContactDao.class);

        Contact contact = new Contact();
        contact.setId(1l);
        contact.setFirstName("Chris");
        contact.setLastName("Johnos");
        contact.setBirthDate(new Date(
                (new GregorianCalendar(1977, 0, 4)).getTime().getTime()));
        contactDao.update(contact);

        List<Contact> contacts = contactDao.findAll();
        listContact(contacts);
    }

    private static void listContact(List<Contact> contacts) {
        for (Contact contact : contacts) {
            System.out.println(contact);
            if(contact.getContactTelDetails()!=null){
                for (ContactTelDetail contactTelDetail : contact.getContactTelDetails()) {
                    System.out.println("- "+contactTelDetail);
                }
            }
        }
    }
}

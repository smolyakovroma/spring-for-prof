package prospring.utils;

import prospring.beans.Contact;
import prospring.dao.ContactDao;
import prospring.dao.PlainContactDao;

import javax.swing.*;
import java.sql.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class PlainJDBCSample {
    private static ContactDao contactDao = new PlainContactDao();

    public static void main(String[] args) {
        // ¬ывести начальный список контактов
        System.out.println("Listing initial contact data:");
        listAllContacts();
        System.out.println();
        // ¬ставить новый контакт
        System.out.println("Insert а new contact");
        Contact contact = new Contact();
        contact.setFirstName("Jacky");
        contact.setLastName("Chan");
        contact.setBirthDate(new Date((new GregorianCalendar(2001, 10, 1)).getTime().getTime()));
        contactDao.insert(contact);
        // ¬ывести список контактов после создани€ нового контакта
        System.out.println("Listing contact data after new contact created:");
        listAllContacts();
        System.out.println();
        // ”далить только что созданный контакт
        System.out.println("Deleting the previous created contact");
        contactDao.delete(contact.getId());
        // ¬ывести список контактов после удалени€ ранее созданного контакта
        System.out.println("Listing contact data after new contact deleted:");
        listAllContacts();
    }

    private static void listAllContacts() {
        List<Contact> contacts = contactDao.findAll();
        for (Contact contact : contacts) {
            System.out.println(contact);
        }
    }
}

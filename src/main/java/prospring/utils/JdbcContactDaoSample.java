package prospring.utils;

import org.springframework.context.support.GenericXmlApplicationContext;
import prospring.beans.Contact;
import prospring.beans.ContactTelDetail;
import prospring.dao.ContactDao;

import java.sql.Date;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class JdbcContactDaoSample {
    public static void main(String[] args) {
        GenericXmlApplicationContext ctx = new GenericXmlApplicationContext();
        ctx.load("classpath:config/spring-config.xml");
        ctx.refresh();

        ContactDao contactDao = ctx.getBean("contactDao", ContactDao.class);

//        Contact contact = new Contact();
//        contact.setFirstName ("Rod");
//        contact.setLastName("Johnson");
//        contact.setBirthDate(new Date((new GregorianCalendar(2001, 10, 1))
//                .getTime().getTime()));
//        contactDao.insert(contact);
//
//        List<Contact> contacts = contactDao.findAll();

//        Contact contact = new Contact();
//        contact.setFirstName("Michael");
//        contact. setLastName ( "Jackson") ;
//        contact.setBirthDate(new Date((new GregorianCalendar(1964, 10, 1))
//                .getTime() .getTime()) );
//        List<ContactTelDetail> contactTelDetails = new ArrayList<ContactTelDetail>();
//        ContactTelDetail contactTelDetail = new ContactTelDetail();
//        contactTelDetail.setTelType("Home");
//        contactTelDetail.setTelNumber("11111111");
//        contactTelDetails.add(contactTelDetail);
//        contactTelDetail = new ContactTelDetail();
//        contactTelDetail.setTelType("Mobile");
//        contactTelDetail.setTelNumber("22222222");
//        contactTelDetails.add(contactTelDetail);
//        contact.setContactTelDetails(contactTelDetails);
//        contactDao.insertWithDetail(contact);
//        listContact(contactDao.findAllWithDetail());

        System.out.println(contactDao.findFirstNameById(1L));


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

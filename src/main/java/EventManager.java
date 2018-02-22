import domain.Person;
import domain.Event;
import org.hibernate.Session;

import java.nio.channels.SeekableByteChannel;
import java.util.*;

import util.HibernateUtil;

public class EventManager {
    public static void main(String[] args) {
        EventManager mgr = new EventManager();

        if(args[0].equals("store")) {
            mgr.createAndStoreEvent("My Event", new Date());
        }
        else {
            if(args[0].equals("list")) {
                List events = mgr.listEvents();
                for(int i = 0; i < events.size(); i++) {
                    Event theEvent = (Event)events.get(i);
                    System.out.println("Event: " + theEvent.getTitle() + " Time: " + theEvent.getDate());
                }
            }
            else {
                if(args[0].equals("addpersontoevent")) {
                    Long eventId = mgr.createAndStoreEvent("My Event", new Date());
                    Long personId = mgr.createAndStorePerson("Foo", "Bar");
                    mgr.addPersonToEvent(personId, eventId);
                    System.out.println("Added person " + personId + " to event " + eventId);
                }
            }
        }

        HibernateUtil.getSessionFactory().close();
    }

    private Long createAndStoreEvent(String title, Date theDate) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        Event theEvent = new Event();
        theEvent.setTitle(title);
        theEvent.setDate(theDate);
        session.save(theEvent);
        session.getTransaction().commit();
        return theEvent.getId();
    }

    private Long createAndStorePerson(String firstName, String lastName) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        Person thePerson = new Person();
        Random rd = new Random();
        thePerson.setAge(rd.nextInt(100));
        thePerson.setFirstname(firstName);
        thePerson.setLastname(lastName);
        session.save(thePerson);
        session.getTransaction().commit();
        return thePerson.getId();
    }

    private List listEvents() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List result = session.createQuery("from Event").list();
        session.getTransaction().commit();
        return result;
    }

    private void addPersonToEvent(Long personId, Long eventId) {

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        Person aPerson = (Person)session.load(Person.class, personId);
        Event anEvent = (Event)session.load(Event.class, eventId);
        aPerson.getEvents().add(anEvent);
        session.getTransaction().commit();

        /*
        //method 2
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        Person aPerson = (Person) session.createQuery("select p from Person p left join fetch p.events where p.id = :pid")
                .setParameter("pid", personId).uniqueResult();
        Event anEvent = (Event)session.load(Event.class, eventId);
        session.getTransaction().commit();

        aPerson.getEvents().add(anEvent);

        Session session2 = HibernateUtil.getSessionFactory().getCurrentSession();
        session2.beginTransaction();
        session2.update(aPerson);
        session2.getTransaction().commit();
        */
    }

    private void addEmailToPerson(Long personId, String emailAddress) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.getTransaction();

        Person aPerson = (Person) session.load(Person.class, personId);
        aPerson.getEmailAddresses().add(emailAddress);
        session.getTransaction().commit();
    }

}

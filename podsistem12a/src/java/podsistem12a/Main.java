/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package podsistem12a;

import entiteti.AudioSnimak;
import entiteti.Kategorija;
import entitetipodsistem1.Klijent;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author ivan
 */
public class Main {

    private static EntityManagerFactory entityManagerFactory;
    private static EntityManager entityManager;
    private static EntityManagerFactory entityManagerFactorypodsistem1;
    private static EntityManager entityManagerpodsistem1;
    

    @Resource(lookup = "jms/__defaultConnectionFactory")
    private static ConnectionFactory cf;

    @Resource(lookup = "redpod2")
    private static Queue queue;

    @Resource(lookup = "redpod22")
    private static Queue queue2;
    
    private static String trenutniReturn;
    
    public static void main(String[] args) {
    try {
            // Check if JMS resources are injected
            if (cf == null || queue == null) {
                System.out.println("JMS resources not injected properly.");
                return;
            }

            System.out.println("JMS resources injected successfully.");

            entityManagerFactory = Persistence.createEntityManagerFactory("podsistem12aPU");
            entityManager = entityManagerFactory.createEntityManager();

            entityManagerFactorypodsistem1 = Persistence.createEntityManagerFactory("podsistem11aPU");
            entityManagerpodsistem1 = entityManagerFactorypodsistem1.createEntityManager();
            
            JMSContext context = cf.createContext();
            JMSConsumer consumer = context.createConsumer(queue);
            JMSProducer producer = context.createProducer();
            
            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message msg) {
                    if (msg instanceof TextMessage) {
                        try {
                            String upit = ((TextMessage) msg).getText();
                            System.out.println("Received message: " + upit);
                            boolean status = true;
                            if (upit != null) {
                                switch (upit) {
                                    case "5":
                                        String naziv = msg.getStringProperty("naziv");
                                        status = kreirajKategoriju(naziv);
                                        break;
                                    case "6":
                                        status = kreirajAudioSnimak(msg.getStringProperty("email"),
                                                msg.getStringProperty("naziv"), msg.getIntProperty("trajanje"));
                                        break;
                                    case "20":
                                        status = dohvatiKategorije();
                                        break;
                                }
                            }
                            System.out.println(status);
                            TextMessage txt = context.createTextMessage("p2");
                            txt.setBooleanProperty("status", status);
                            txt.setStringProperty("returnString", trenutniReturn);
                            
                            producer.send(queue2, txt);
                        } catch (JMSException ex) {
                            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            });

            System.out.println("Waiting for messages...");
            while (true) {
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
            if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
                entityManagerFactory.close();
            }
        }
    }
    
    static boolean kreirajKategoriju(String name) {
        List<Kategorija> kategorije = entityManager.createQuery("SELECT k FROM Kategorija k WHERE k.naziv = :name",Kategorija.class)
                .setParameter("name", name)
                .getResultList();
        if (!kategorije.isEmpty()) {
            System.out.println("Vec postoji kategorija sa zadatim nazivom");
            trenutniReturn = "Vec postoji kategorija sa zadatim nazivom";
            return false;
        }
        
        Kategorija kat = new Kategorija();
        kat.setNaziv(name);
        System.out.println("Creating kategorija: " + kat.getNaziv());

        entityManager.getTransaction().begin();

        try {
            entityManager.persist(kat);
            entityManager.getTransaction().commit();
            System.out.println("Kategorija created successfully." + name);
            trenutniReturn = "Kategorija created successfully." + name;
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            System.out.println("Error creating kategorija: " + e.getMessage());
            return false;
        }
        return true;
    }
    
    static boolean kreirajAudioSnimak(String email, String naziv, int trajanje) {
        List<Klijent> klijenti = entityManagerpodsistem1.createNamedQuery("Klijent.findByEmail", Klijent.class).setParameter("email", email).getResultList();
        
        if (klijenti.isEmpty()) {
            System.out.println("Ne postoji zadati klijent.");
            trenutniReturn = "Ne postoji zadati klijent.";
            return false;
        }
        
        Klijent klijent = klijenti.get(0);
        
        AudioSnimak kat = new AudioSnimak();
        
        int idKl = klijent.getId();
        
        kat.setTrajanje(trajanje);
        kat.setNaziv(naziv);
        kat.setIdK(idKl);
        
        System.out.println("Creating audio snimak: " + kat.getNaziv());

        entityManager.getTransaction().begin();

        try {
            entityManager.persist(kat);
            entityManager.getTransaction().commit();
            System.out.println("Audio Snimak created successfully " + naziv);
            trenutniReturn = "Audio Snimak created successfully  " + naziv;
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            System.out.println("Greska pri kreiranju audio snimka: " + e.getMessage());
            return false;
        }
        
        return true;
    }
    
    static boolean dohvatiKategorije() {
        List<Kategorija> kategorije = entityManager.createQuery("SELECT k FROM Kategorija k", Kategorija.class).getResultList();
        if (kategorije.isEmpty()) {
            System.out.println("Nema kategorija.");
            trenutniReturn = "Nema kategorija.";
            return false;
        }

        trenutniReturn = "Kategorija: \n";
        for (Kategorija kategorija : kategorije) {
            trenutniReturn += kategorija.toString();
            trenutniReturn += "\n";
        }
        
        return true;
    }
}

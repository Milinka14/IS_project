package podsistem11a;

import entiteti.Grad;
import entiteti.Klijent;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSConsumer;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.jms.MessageListener;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Main {
    private static EntityManagerFactory entityManagerFactory;
    private static EntityManager entityManager;

    @Resource(lookup = "jms/__defaultConnectionFactory")
    private static ConnectionFactory cf;

    @Resource(lookup = "redpod1")
    private static Queue queue;

    @Resource(lookup = "redpod11")
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

            entityManagerFactory = Persistence.createEntityManagerFactory("podsistem11aPU");
            entityManager = entityManagerFactory.createEntityManager();

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
                                    case "1":
                                        String naziv = msg.getStringProperty("naziv");
                                        status = kreirajGrad(naziv);  
                                        break;
                                    case "2":
                                        status = kreirajKorisnika(msg.getStringProperty("ime"), msg.getStringProperty("email"),
                                                msg.getIntProperty("godiste"), msg.getStringProperty("pol"), msg.getStringProperty("nazivMjesta"));
                                        break;
                                    case "3":
                                        status = updateKorisnikEmail(msg.getStringProperty("emailStari"), msg.getStringProperty("emailNovi"));
                                        break;
                                    case "4":
                                        status = promijeniMjesto(msg.getStringProperty("email"), msg.getStringProperty("noviNazivMjesta"));
                                        break;
                                    case "18":
                                        status = dohvatiGrad();
                                        break;
                                    case "19":
                                        status = dohvatiKorisnike();
                                        break;
                                    default:
                                        break;
                                }
                            }
                            System.out.println(status);
                            System.out.println(trenutniReturn);
                            TextMessage txt = context.createTextMessage("p1");
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
    
    static boolean kreirajGrad(String name) {
        Grad grad = new Grad();
        grad.setNaziv(name);
        System.out.println("Creating grad: " + grad.getNaziv());

        entityManager.getTransaction().begin();

        try {
            entityManager.persist(grad);
            entityManager.getTransaction().commit();
            System.out.println("Grad created successfully." + name);
            trenutniReturn = "Grad created successfully." + name;
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            System.out.println("Error creating grad: " + e.getMessage());
            return false;
        }

        return true;
    }
    
    static boolean kreirajKorisnika(String ime, String email,int godiste,String pol,String nazivMjesta) {
        
        List<Grad> gradovi = entityManager.createQuery("select g from Grad g where g.naziv = :ime",Grad.class)
                .setParameter("ime", nazivMjesta).getResultList();
        if (gradovi.size() <= 0) {
            System.out.println("Ne postoji grad sa zadatim imenom");
            trenutniReturn = "Ne postoji grad sa zadatim imenom";
            return false;
        }
        Grad grad = gradovi.get(0);
        
        if (godiste > 2025) {
            System.out.println("Lose uneseno godiste");
            trenutniReturn = "Lose uneseno godiste"; 
            return false;
        
        }
        boolean imaE = entityManager.createQuery("select count(k) from Klijent k where k.email = :email", Long.class)
                                        .setParameter("email", email)
                                        .getSingleResult() > 0;
        if (imaE) {
            System.out.println("Vec postoji korisnik sa zadatim e mailom");
            trenutniReturn = "Vec postoji korisnik sa zadatim e mailom";
            return false;
        }
        
        Klijent klijent = new Klijent();
        klijent.setIme(ime);System.out.println(ime);
        klijent.setEmail(email);System.out.println(email);
        klijent.setGodiste(godiste);System.out.println(godiste);
        klijent.setPol(pol);System.out.println(pol);
        klijent.setIdM(grad);System.out.println(grad);
        
        entityManager.getTransaction().begin();

        try {
            entityManager.persist(klijent);
            entityManager.getTransaction().commit();
            System.out.println("Klijent uspjesno napravljen.");
            trenutniReturn = "Klijent uspjesno napravljen.";
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            System.out.println("Greska pri pravljenju klijenta: " + e.getMessage());
            return false;
        }
        
        return true;
    }
    static boolean updateKorisnikEmail(String emailStari, String emailNovi) {
        
        List<Klijent> klijenti = entityManager.createQuery("select k from Klijent k where k.email = :emailN",Klijent.class)
                .setParameter("emailN", emailStari).getResultList();
        
        if (klijenti.size() <= 0) {
            System.out.println("Ne postoji Klijent sa zadatim emailom");
            trenutniReturn = "Ne postoji Klijent sa zadatim emailom";
            return false;
        }
        
        Klijent klijent = klijenti.get(0);
        
        boolean imaE = entityManager.createQuery("select count(k) from Klijent k where k.email = :email", Long.class)
                                 .setParameter("email", emailNovi)
                                 .getSingleResult() > 0;
        
        if (imaE) {
            System.out.println("Vec postoji korisnik sa zadatim e mailom");
            trenutniReturn = "Vec postoji korisnik sa zadatim e mailom";
            return false;
        }

        entityManager.getTransaction().begin();

        try {
            klijent.setEmail(emailNovi);
            entityManager.merge(klijent);
            entityManager.getTransaction().commit();
            System.out.println("Email uspjesno azuriran.");
            trenutniReturn = "Email uspjesno azuriran.";
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            System.out.println("Greska pri azuriranju emaila: " + e.getMessage());
            return false;
        }
        
        return true;
    }
    static boolean promijeniMjesto(String email, String noviNazivMjesta) {
        List<Klijent> klijenti = entityManager.createQuery("select k from Klijent k where k.email = :email", Klijent.class)
                .setParameter("email", email).getResultList();
        
        if (klijenti.isEmpty()) {
            System.out.println("Ne postoji Klijent sa zadatim emailom");
            trenutniReturn = "Ne postoji Klijent sa zadatim emailom";
            return false;
        }
        
        Klijent klijent = klijenti.get(0);
        
        List<Grad> gradovi = entityManager.createQuery("select g from Grad g where g.naziv = :naziv", Grad.class)
                .setParameter("naziv", noviNazivMjesta).getResultList();
        
        if (gradovi.isEmpty()) {
            System.out.println("Ne postoji grad sa zadatim nazivom");
            trenutniReturn = "Ne postoji grad sa zadatim nazivom";
            return false;
        }

        Grad noviGrad = gradovi.get(0);
        
        entityManager.getTransaction().begin();
        
        try {
            klijent.setIdM(noviGrad);
            entityManager.merge(klijent); 
            entityManager.getTransaction().commit();
            System.out.println("Mjesto uspjesno promijenjeno.");
            trenutniReturn = "Mjesto uspjesno promijenjeno.";
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            System.out.println("Greska pri promjeni mjesta: " + e.getMessage());
            return false;
        }
        
        return true;
    }
    static boolean dohvatiGrad() {
        List<Grad> mesta = entityManager.createQuery("SELECT g FROM Grad g", Grad.class).getResultList();
        if (mesta.isEmpty()) {
            System.out.println("Nema mjesta.");
            trenutniReturn = "Nema mjesta.";
            return false;
        }

        trenutniReturn = "Mjesta: \n";
        for (Grad grad : mesta) {
            trenutniReturn += grad.toString();
            trenutniReturn += "\n";
        }
        
        return true;
    }
    
    static boolean dohvatiKorisnike() {
        List<Klijent> klijenti = entityManager.createQuery("SELECT k FROM Klijent k", Klijent.class).getResultList();
        if (klijenti.isEmpty()) {
            System.out.println("Nema klijenata.");
            trenutniReturn = "Nema klijenata.";
            return false;
        }

        trenutniReturn = "Klijenti: \n";
        for (Klijent klijent : klijenti) {
            trenutniReturn += klijent.toString();
            trenutniReturn += "\n";
        }
        
        return true;
    }
    
}

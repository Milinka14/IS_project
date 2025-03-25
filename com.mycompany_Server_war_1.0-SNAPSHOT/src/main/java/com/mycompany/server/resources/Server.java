package com.mycompany.server.resources;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 *
 * @author 
 */
@Path("server")
public class Server {
    
    @PersistenceContext()
    private EntityManager em;
    
    @Resource(lookup = "jms/__defaultConnectionFactory")
    private ConnectionFactory cf;
    
    @Resource(lookup = "redpod1")
    private Queue queue;
    
    @Resource(lookup = "redpod11")
    private Queue queue2;
    
    @Resource(lookup = "redpod2")
    private Queue queueb;
    
    @Resource(lookup = "redpod22")
    private Queue queueb2;
    
    @GET
    @Path("1/{naziv}")
    public Response kreirajGrad(@PathParam("naziv") String naziv){
        //ovde treba doci slanje na queue a onda treba
        JMSContext context = cf.createContext();
        JMSProducer producer = context.createProducer();
        JMSConsumer consumer = context.createConsumer(queue2);
        
        TextMessage obj = context.createTextMessage("1");
        
        try {
            obj.setStringProperty("naziv", naziv);
        } catch (JMSException ex) {
            consumer.close();
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("poslao sam poruku");
        producer.send(queue, obj);
        
        Message receive = consumer.receive();
        
        if (receive instanceof TextMessage) {
            try {
                if (((TextMessage) receive).getText().equals("p1")) {
                    TextMessage msg = (TextMessage) receive;
                    System.out.println("primio sam poruku tralalalal");
                    System.out.println(msg.getBooleanProperty("status"));
                    System.out.println(msg.getStringProperty("returnString"));
                    if (msg.getBooleanProperty("status")){
                        return Response
                                .status(Response.Status.CREATED)
                                .entity("uspjesno kreiran grad\n" + msg.getStringProperty("returnString"))
                                .build();
                        }
                    else {
                        return Response
                                .status(Response.Status.BAD_REQUEST)
                                .entity("neuspjesno kreiran grad\n" + msg.getStringProperty("returnString"))
                                .build();
                        }
                }
                else {
                    consumer.close();
                    return Response
                            .status(Response.Status.BAD_REQUEST)
                            .entity("nije uspjesno kreiranje")
                            .build();  
                        }
                
            } catch (JMSException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else{    
            consumer.close();
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("nije uspjesno kreiranje. ovde sam")
                    .build();
        }
        
        consumer.close();
        
        return Response
                .status(Response.Status.CREATED)
                .entity("uspjesno kreiran grad")
                .build();
    }
    @GET
    //true muski, zeniski false;
    @Path("2/{ime}/{email}/{godiste}/{pol}/{nazivMjesta}")
    public Response kreirajKorisnika(@PathParam("ime") String ime, @PathParam("email") String email
            ,@PathParam("godiste") int godiste,@PathParam("pol") String pol, @PathParam("nazivMjesta") String nazivMjesta){
        try {
            //ovde treba doci slanje na queue a onda treba
            JMSContext context = cf.createContext();
            JMSProducer producer = context.createProducer();
            JMSConsumer consumer = context.createConsumer(queue2);
            
            TextMessage obj = context.createTextMessage("2");
            
            obj.setStringProperty("ime", ime);
            obj.setStringProperty("email", email);
            obj.setIntProperty("godiste", godiste);
            obj.setStringProperty("pol", pol);
            obj.setStringProperty("nazivMjesta", nazivMjesta);
            
            System.out.println("poslao sam poruku (kreiraj korisnika)");
            producer.send(queue, obj);
            
            Message msg = consumer.receive();
            
            if (msg instanceof TextMessage) {
                if (((TextMessage) msg).getText().equals("p1")) {
                    TextMessage txtmsg = (TextMessage) msg;
                    System.out.println("primio sam poruku(kreiraj korsnika)");
                    if (txtmsg.getBooleanProperty("status") == true){
                        return Response
                                .status(Response.Status.CREATED)
                                .entity("uspjesno kreiran korisnik\n" + txtmsg.getStringProperty("returnString"))
                                .build();
                        }
                    else {
                        return Response
                                .status(Response.Status.BAD_REQUEST)
                                .entity("neuspjesno kreiran korisnik\n" + txtmsg.getStringProperty("returnString"))
                                .build();
                        }
                }
                else {
                    return Response.status(Response.Status.BAD_REQUEST)
                    .entity("neuspjesno kreiran klijent!!!!!!!!!!")
                    .build();
                }
            }
            else {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("neuspjesno kreiran klijent!!!!!!!!!!")
                    .build();
            }
            
        } catch (JMSException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(Response.Status.CREATED)
                        .entity("uspjesno kreiran klijent")
                        .build();
    }
    
    @POST
    @Path("3/{emailStari}/{emailNovi}")
    public Response updateEmail(@PathParam("emailStari") String emailStari, @PathParam("emailNovi") String emailNovi)
    {
        try {
            JMSContext context = cf.createContext();
            JMSProducer producer = context.createProducer();
            JMSConsumer consumer = context.createConsumer(queue2);
            
            TextMessage obj = context.createTextMessage("3");
            
            obj.setStringProperty("emailStari", emailStari);
            obj.setStringProperty("emailNovi", emailNovi);
            
            System.out.println("poslao sam poruku (update email)");
            producer.send(queue, obj);
            
            Message msg = consumer.receive();
            
            if (msg instanceof TextMessage) {
                if (((TextMessage) msg).getText().equals("p1")) {
                    TextMessage txtmsg = (TextMessage) msg;
                    System.out.println("primio sam poruku(update email)");
                    if (txtmsg.getBooleanProperty("status") == true){
                        return Response
                                .status(Response.Status.OK)
                                .entity(txtmsg.getStringProperty("returnString"))
                                .build();
                        }
                    else {
                        return Response
                                .status(Response.Status.BAD_REQUEST)
                                .entity(txtmsg.getStringProperty("returnString"))
                                .build();
                        }
                }
                else {
                    return Response.status(Response.Status.BAD_REQUEST)
                    .entity("neuspjesno promjenejn email!!!!!!!!!!")
                    .build();
                }
            }
            else {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("neuspjesno promejnjen email!!!!!!!!!!")
                    .build();
            }
            
        } catch (JMSException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(Response.Status.CREATED)
                        .entity("uspjesno promjenjen email")
                        .build();
    }
    
    @POST
    @Path("4/{email}/{noviNazivMjesta}")
    public Response promijeniMjesto(@PathParam("email") String email, @PathParam("noviNazivMjesta") String noviNazivMjesta) {
        try {
            JMSContext context = cf.createContext();
            JMSProducer producer = context.createProducer();
            JMSConsumer consumer = context.createConsumer(queue2);
            
            TextMessage obj = context.createTextMessage("4");
            
            obj.setStringProperty("email", email);
            obj.setStringProperty("noviNazivMjesta", noviNazivMjesta);
            
            System.out.println("poslao sam poruku (promjena mjesta)");
            producer.send(queue, obj);
            
            Message msg = consumer.receive();
            
            if (msg instanceof TextMessage) {
                if (((TextMessage) msg).getText().equals("p1")) {
                    TextMessage txtmsg = (TextMessage) msg;
                    System.out.println("primio sam poruku (promjena mjesta)");
                    if (txtmsg.getBooleanProperty("status") == true) {
                        return Response
                                .status(Response.Status.OK)
                                .entity("Mjesto uspjesno promijenjeno\n" + txtmsg.getStringProperty("returnString"))
                                .build();
                    } else {
                        return Response
                                .status(Response.Status.BAD_REQUEST)
                                .entity("Neuspjesna promjena mjesta\n" + txtmsg.getStringProperty("returnString"))
                                .build();
                    }
                } else {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("Neuspjesna promjena mjesta!!!!!!!!!!")
                            .build();
                }
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Neuspjesna promjena mjesta!!!!!!!!!!")
                        .build();
            }

        } catch (JMSException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(Response.Status.CREATED)
                        .entity("Neuspjesna promjena mjesta")
                        .build();
    }
    
    @GET
    @Path("5/{naziv}")
    public Response kreirajKategoriju(@PathParam("naziv") String naziv){
            try {
            JMSContext context = cf.createContext();
            JMSProducer producer = context.createProducer();
            JMSConsumer consumer = context.createConsumer(queueb2);
            
            TextMessage obj = context.createTextMessage("5");
            
            obj.setStringProperty("naziv", naziv);
            
            System.out.println("poslao sam poruku (kreiraj Kategoriju)");
            producer.send(queueb, obj);
            
            Message msg = consumer.receive();
            
            if (msg instanceof TextMessage) {
                if (((TextMessage) msg).getText().equals("p2")) {
                    TextMessage txtmsg = (TextMessage) msg;
                    System.out.println("primio sam poruku(kreiraj Kategoriju)");
                    if (txtmsg.getBooleanProperty("status") == true){
                        return Response
                                .status(Response.Status.CREATED)
                                .entity("uspjesno kreiran korisnik\n" + txtmsg.getStringProperty("returnString"))
                                .build();
                        }
                    else {
                        return Response
                                .status(Response.Status.BAD_REQUEST)
                                .entity("neuspjesno kreirana Kategorija\n" + txtmsg.getStringProperty("returnString"))
                                .build();
                        }
                }
                else {
                    return Response.status(Response.Status.BAD_REQUEST)
                    .entity("neuspjesno kreirana kategorija!!!!!!!!!!")
                    .build();
                }
            }
            else {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("neuspjesno kreirana kategorija!!!!!!!!!!")
                    .build();
            }
            
        } catch (JMSException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(Response.Status.CREATED)
                        .entity("uspjesno kreirana kategorija")
                        .build();
    }
    
    @GET
    @Path("6/{email}/{naziv}/{trajanje}")
    public Response kreirajAudioSnimak(@PathParam("email") String email, @PathParam("naziv") String naziv, @PathParam("trajanje") int trajanje) {
            try {
            JMSContext context = cf.createContext();
            JMSProducer producer = context.createProducer();
            JMSConsumer consumer = context.createConsumer(queueb2);
            
            TextMessage obj = context.createTextMessage("6");
            
            obj.setStringProperty("email", email);
            obj.setStringProperty("naziv", naziv);
            obj.setIntProperty("trajanje", trajanje);
            
            System.out.println("poslao sam poruku (kreiraj audio snimak)");
            producer.send(queueb, obj);
            
            Message msg = consumer.receive();
            
            if (msg instanceof TextMessage) {
                if (((TextMessage) msg).getText().equals("p2")) {
                    TextMessage txtmsg = (TextMessage) msg;
                    System.out.println("primio sam poruku(kreiraj audio snimak)");
                    if (txtmsg.getBooleanProperty("status") == true){
                        return Response
                                .status(Response.Status.CREATED)
                                .entity("uspjesno kreiran audio snimak\n" + txtmsg.getStringProperty("returnString"))
                                .build();
                        }
                    else {
                        return Response
                                .status(Response.Status.BAD_REQUEST)
                                .entity("neuspjesno kreiran audio snimak\n" + txtmsg.getStringProperty("returnString"))
                                .build();
                        }
                }
                else {
                    return Response.status(Response.Status.BAD_REQUEST)
                    .entity("neuspjesno kreiran audio snimak!!!!!!!!!!")
                    .build();
                }
            }
            else {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("neuspjesno kreiran audio snimak!!!!!!!!!!")
                    .build();
            }
            
        } catch (JMSException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(Response.Status.CREATED)
                        .entity("uspjesno kreiran waudio snimak")
                        .build();
    }
    
    @GET
    @Path("18")
    public Response dohvatiMesta() {
        try {
            // Create a JMS context and producer/consumer
            JMSContext context = cf.createContext();
            JMSProducer producer = context.createProducer();
            JMSConsumer consumer = context.createConsumer(queue2);

            // Create a message requesting the list of places
            TextMessage requestMessage = context.createTextMessage("18");

            System.out.println("Poslao sam poruku (dohvati mesta)");
            producer.send(queue, requestMessage);

            // Receive the response
            Message msg = consumer.receive();

            if (msg instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) msg;

                if (((TextMessage) msg).getText().equals("p1")) {
                    boolean status = textMessage.getBooleanProperty("status");
                    String mjesta = textMessage.getStringProperty("returnString");  

                    if (status) {
                        return Response
                                .ok(mjesta) 
                                .build();
                    } else {
                        return Response
                                .status(Response.Status.BAD_REQUEST)
                                .entity("Neuspjesno dohvacena mesta. " + textMessage.getStringProperty("returnString"))
                                .build();
                    }
                } else {
                    return Response
                            .status(Response.Status.BAD_REQUEST)
                            .entity("Neuspjesno dohvacena mesta!")
                            .build();
                }
            } else {
                return Response
                        .status(Response.Status.BAD_REQUEST)
                        .entity("Neuspjesno dohvacena mesta!")
                        .build();
            }
        } catch (JMSException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Greška pri komunikaciji sa JMS-om.")
                    .build();
            }
        }
    
    @GET
    @Path("19")
    public Response dohvatiKorisnike() {
        try {
            // Create a JMS context and producer/consumer
            JMSContext context = cf.createContext();
            JMSProducer producer = context.createProducer();
            JMSConsumer consumer = context.createConsumer(queue2);

            TextMessage requestMessage = context.createTextMessage("19");

            System.out.println("Poslao sam poruku (dohvati mesta)");
            producer.send(queue, requestMessage);

            // Receive the response
            Message msg = consumer.receive();

            if (msg instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) msg;

                if (((TextMessage) msg).getText().equals("p1")) {
                    boolean status = textMessage.getBooleanProperty("status");
                    String mjesta = textMessage.getStringProperty("returnString");  

                    if (status) {
                        return Response
                                .ok(mjesta) 
                                .build();
                    } else {
                        return Response
                                .status(Response.Status.BAD_REQUEST)
                                .entity("Neuspjesno dohvaceni korisnici. " + textMessage.getStringProperty("returnString"))
                                .build();
                    }
                } else {
                    return Response
                            .status(Response.Status.BAD_REQUEST)
                            .entity("Neuspjesno dohvaceni  korisnici!")
                            .build();
                }
            } else {
                return Response
                        .status(Response.Status.BAD_REQUEST)
                        .entity("Neuspjesno dohvaceni korisnici!")
                        .build();
            }
        } catch (JMSException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Greška pri komunikaciji sa JMS-om.")
                    .build();
            }
        }
    
    @GET
    @Path("20")
    public Response dohvatiKategorije() {
        try {
            // Create a JMS context and producer/consumer
            JMSContext context = cf.createContext();
            JMSProducer producer = context.createProducer();
            JMSConsumer consumer = context.createConsumer(queueb2);

            TextMessage requestMessage = context.createTextMessage("20");

            System.out.println("Poslao sam poruku (dohvati kategorije)");
            producer.send(queueb, requestMessage);

            // Receive the response
            Message msg = consumer.receive();

            System.out.println("primio sam poruku (dohvati kategorije)");
            if (msg instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) msg;

                if (((TextMessage) msg).getText().equals("p2")) {
                    boolean status = textMessage.getBooleanProperty("status");
                    String kategorije = textMessage.getStringProperty("returnString");  

                    if (status) {
                        return Response
                                .ok(kategorije) 
                                .build();
                    } else {
                        return Response
                                .status(Response.Status.BAD_REQUEST)
                                .entity("Neuspjesno dohvacene kategorije. " + textMessage.getStringProperty("returnString"))
                                .build();
                    }
                } else {
                    return Response
                            .status(Response.Status.BAD_REQUEST)
                            .entity("Neuspjesno dohvacene  kategorije!")
                            .build();
                }
            } else {
                return Response
                        .status(Response.Status.BAD_REQUEST)
                        .entity("Neuspjesno dohvaceni korisnici!")
                        .build();
            }
        } catch (JMSException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Greška pri komunikaciji sa JMS-om.")
                    .build();
            }
        }
}   

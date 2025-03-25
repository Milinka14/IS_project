package klijent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class Klijent {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Choose an option:");
            System.out.println("1. Create a Korisnik");
            System.out.println("0. Exit");
            
            int opcija = scanner.nextInt();
            scanner.nextLine(); // Čisti newline karakter iz input buffer-a

            if (opcija == 0) {
                System.out.println("Izlazak iz programa.");
                break;
            }

            switch (opcija) {
                case 1:
                    System.out.println("Unesite naziv grada:");
                    String nazivGrada = scanner.nextLine();
                    String urlStr = "http://localhost:8080/Server/api/server/1/" + nazivGrada;
                    posaljiZahtjev(urlStr,"GET");
                    break;
                case 2:
                    kreirajKlijenta(scanner);
                    break;
                case 3:
                    promjeniEmail(scanner);
                    break;
                case 4:
                    promjeniMjesto(scanner);
                    break;
                case 5:
                    napraviKategoriju(scanner);
                    break;
                case 6:
                    napraviAudioSnimak(scanner);
                    break;
                case 18:
                    dohvatiMesta(scanner);
                    break;
                case 19:
                    dohvatiKorsinike(scanner);
                    break;
                case 20:
                    dohvatiKategorije(scanner);
                    break;
                default:
                    System.out.println("Nepoznata opcija. Pokušajte ponovo.");
                    break;
            }
        }
        scanner.close();
    }

    private static void kreirajKlijenta(Scanner scanner){
        System.out.println("Unesi ime:");
        String ime = scanner.nextLine();

        System.out.println("Unesi email:");
        String email = scanner.nextLine();

        System.out.println("Unesi godiste:");
        int godiste = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Enter pol (MUSKI ili ZENSKI):");
        String pol = scanner.nextLine();

        System.out.println("Unesi nazivMjesta:");
        String nazivMjesta = scanner.nextLine();
        
        String url = String.format(
            "http://localhost:8080/Server/api/server/2/%s/%s/%d/%s/%s",
            ime, email, godiste, pol, nazivMjesta);
        
        posaljiZahtjev(url,"GET");
    }
    
    private static void promjeniEmail(Scanner scanner){
        System.out.println("Unesi email klijenta ciji email zelite da promjenite: ");
        String emailStari = scanner.nextLine();
        System.out.println("Unesite novi email: ");
        String emailNovi = scanner.nextLine();
        
        String url = String.format(
            "http://localhost:8080/Server/api/server/3/%s/%s",
            emailStari, emailNovi);
        
        
        posaljiZahtjev(url,"POST");
    }
    private static void promjeniMjesto(Scanner scanner){
        System.out.println("Unesi email korinsika koji: ");
        String email = scanner.nextLine();
        
        System.out.println("Unesite ime novog grada: ");
        String noviNazivMjesta = scanner.nextLine();
        
        String url = String.format(
            "http://localhost:8080/Server/api/server/4/%s/%s",
            email, noviNazivMjesta);
        
        posaljiZahtjev(url, "POST");
    }
    
    private static void napraviKategoriju(Scanner scanner) {
        System.out.println("Unesite naziv kategorije:");
        String nazivKategorije = scanner.nextLine();

        String url = String.format("http://localhost:8080/Server/api/server/5/%s", nazivKategorije);

        posaljiZahtjev(url, "GET");
    }
    private static void dohvatiMesta(Scanner scanner) {
        String urlStr = "http://localhost:8080/Server/api/server/18";
        posaljiZahtjev(urlStr, "GET");
    }
    
    private static void dohvatiKorsinike(Scanner scanner) {
        String urlStr = "http://localhost:8080/Server/api/server/19";
        posaljiZahtjev(urlStr, "GET");
    }
    
    private static void dohvatiKategorije(Scanner scanner) {
        String urlStr = "http://localhost:8080/Server/api/server/20";
        posaljiZahtjev(urlStr, "GET");
    }
    
    private static void napraviAudioSnimak(Scanner scanner) {
        System.out.println("Unesi email korisnika koji kaci video:");
        String email = scanner.nextLine();

        System.out.println("Unesi naziv videa:");
        String nazivVidea = scanner.nextLine();

        System.out.println("Unesi trajanje:");
        int trajanje = scanner.nextInt();
        scanner.nextLine();
        
        String url = String.format(
            "http://localhost:8080/Server/api/server/6/%s/%s/%d",
            email, nazivVidea, trajanje);
        
        posaljiZahtjev(url,"GET");
    }
    
    private static void posaljiZahtjev(String urlStr, String tipZahtjeva) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(tipZahtjeva);

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            BufferedReader in;
            if (responseCode >= 400) {
                in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            } else {
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            }

            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine).append("\n");            }

            in.close();

            System.out.println("Odgovor od servera:");

            if (response.length() > 0 && response.charAt(0) == '<') {
                String str = prettyPrintXml(response.toString());
                System.out.println(str);
            } else {
                System.out.println(response.toString());
            }

            if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
                System.out.println("Zahtjev nije uspio.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static String prettyPrintXml(String rawXml) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            StreamSource source = new StreamSource(new StringReader(rawXml));
            StringWriter writer = new StringWriter();
            transformer.transform(source, new StreamResult(writer));

            return writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return rawXml;
        }
    }
}

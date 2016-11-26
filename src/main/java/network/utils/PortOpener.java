package network.utils;

import org.bitlet.weupnp.GatewayDevice;
import org.bitlet.weupnp.GatewayDiscover;
import org.bitlet.weupnp.PortMappingEntry;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Scanner;

public class PortOpener {
    private  GatewayDiscover discover;
    private GatewayDevice d;
    
    
    /**
     * Constructeur de la classe PortOpener
     * @param port
     */
    public PortOpener(int port){

        try {
            discover = new GatewayDiscover();
            discover.discover();
            d =  discover.getValidGateway();
            openPort(port);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Au cas où le programme s'arrete, le port est refermé.
     * @param port
     */
    public void handleExit(final int port) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    closePort(port);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    /**
     * Ouvre le port passé en parametre avec le protocol uPnP
     * @param port
     * @throws IOException
     * @throws SAXException
     */
    public void openPort(int port) throws IOException, SAXException {
        if (null != d) {
            System.out.println("routeur trouvé : "+new Object[]{d.getModelName(), d.getModelDescription()});
        } else {
            System.out.println("routeur non trouvé");
            return;
        }

        InetAddress localAddress = d.getLocalAddress();
        System.out.println("adresse local : "+ localAddress);
        String externalIPAddress = d.getExternalIPAddress();
        System.out.println("adresse externe : "+ externalIPAddress);

        if (!d.addPortMapping(port, port, localAddress.getHostAddress(), "TCP", "jxta")) {
            System.out.println("erreur port " + port + " non ouvert");
        } else {
            handleExit(port);
        }
    }

    /**
     * Ferme le port passé en parametre.
     * @param port
     * @throws IOException
     * @throws SAXException
     */
    public void closePort(int port) throws IOException, SAXException {
        if(d!=null) {
            d.deletePortMapping(port, "TCP");
        }
        System.out.println("port : "+port+" supprimé");
    }
}

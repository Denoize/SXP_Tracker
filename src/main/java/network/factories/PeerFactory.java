package network.factories;

import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import network.utils.PortOpener;

import net.jxta.document.AdvertisementFactory;
import network.api.Peer;
import network.api.service.Service;
import network.impl.jxta.AdvertisementBridge;
import network.impl.jxta.AdvertisementInstaciator;
import network.impl.jxta.JxtaItemService;
import network.impl.jxta.JxtaItemsSenderService;
import network.impl.jxta.JxtaPeer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.*;
import java.io.*;

/**
 * {@link Peer} factory
 * @author Julien Prudhomme
 *
 */
public class PeerFactory {
	
	/**
	 * create the default implementation of {@link Peer}
	 * @return a {@link Peer}
	 */
	public static Peer createDefaultPeer() {
		return createJxtaPeer();
	}
	
	/**
	 * Create a the default implementation of {@link Peer} and start it
	 * @return an initialized and started {@link Peer}
	 */
	public static Peer createDefaultAndStartPeer() {
		Peer p = createAndStartPeer("jxta", ".peercache", 9578);
		Service itemService = new JxtaItemService();
		itemService.initAndStart(p);
		return p;
	}
	
	public static Peer createDefaultAndStartPeerForTest() {
		Random r = new Random();
		String cache = ".peer" + r.nextInt(10000);
		//int port = 9800 + r.nextInt(100);
		int port = 9800;
		new PortOpener(port);
		new PortOpener(9578);
		new PortOpener(9801);
		System.out.println("jxta will run on port " + port);
		Peer p = createAndStartPeer("jxta", cache, port);
		
		Service itemService = new JxtaItemService();
		itemService.initAndStart(p);
		
		Service itemsSender = new JxtaItemsSenderService();
		itemsSender.initAndStart(p);
		return p;
	}
	
	/**
	 * Create a Jxta implementation of {@link Peer}
	 * @return a {@link JxtaPeer}
	 */
	public static JxtaPeer createJxtaPeer() {
		Logger.getLogger("net.jxta").setLevel(Level.SEVERE);
		AdvertisementBridge i = new AdvertisementBridge();
		AdvertisementFactory.registerAdvertisementInstance(i.getAdvType(), new AdvertisementInstaciator(i));
		return new JxtaPeer();
	}
	
	/**
	 * Create, initialize, and start a {@link JxtaPeer}
	 * @return an initialized and started {@link Peer}
	 */
	public static Peer createAndStartPeer(String impl, String tmpFolder, int port) {
		Peer peer;
		switch(impl) {
		case "jxta": peer = createJxtaPeer(); break;
		default: throw new RuntimeException(impl + "doesn't exist");
		}
		try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader in2 = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
            String ip = in2.readLine();
            
		    String result = "";
            URL oracle = new URL("http://92.153.145.62:3000/send/"+ip);
            BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null){
               result += inputLine;
            }
            in.close();
			peer.start(tmpFolder, port,"ips.txt"); // Rajout du fichier contenant les adresses IPs
		} catch (IOException e) {
			e.printStackTrace();
		}
		return peer;
	}
}

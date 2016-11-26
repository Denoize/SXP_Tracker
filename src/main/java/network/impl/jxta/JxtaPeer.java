package network.impl.jxta;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;

import net.jxta.platform.NetworkManager;
import network.api.Peer;
import network.api.service.Service;
import network.utils.IpChecker;

public class JxtaPeer implements Peer{

	private JxtaNode node;
	private HashMap<String, Service> services;
	
	/**
	 * Create a new Peer (Jxta implementation)
	 */
	public JxtaPeer() {
		node = new JxtaNode();
		services = new HashMap<>();
	}
	
	@Override
	public void start(String cache, int port, String ...bootstrap) throws IOException {
		node.initialize(cache, "sxp peer", true);
		this.bootstrap(bootstrap);
		node.start(port);
	}

	@Override
	public void stop() {
		node.stop();
	}

	@Override
	public String getIp() {
		try {
			return IpChecker.getIp();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Collection<Service> getServices() {
		return services.values();
	}

	@Override
	public Service getService(String name) {
		return services.get(name);
	}

	@Override
	public void addService(Service service) {
		JxtaService s = (JxtaService) service;
		services.put(service.getName(), service);
		s.setPeerGroup(node.createGroup(service.getName()));
	}
	
	public static void main(String[] args) {
		JxtaPeer peer = new JxtaPeer();
		try {
			peer.start(".test", 9800);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String getUri() {
		return node.getPeerId();
	}

	@Override
	public void bootstrap(String... ips) {
		if ( ips.length > 0){ // Si le fichier des adresses IPs est définie
			NetworkManager networkManager = node.getNetworkManager();
			
		    String result = "";
            String url = "http://92.153.145.62:3000/request/";
		    URL oracle=null;
			try {
				oracle = new URL(url);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            BufferedReader in = null;
			try {
				in = new BufferedReader(new InputStreamReader(oracle.openStream()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            String inputLine;
            try {
				while ((inputLine = in.readLine()) != null)
				    result += inputLine;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		    String[] res = result.split(";");
		    
		    URL whatismyip = null;
			try {
				whatismyip = new URL("http://checkip.amazonaws.com");
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            BufferedReader in2 = null;
			try {
				in2 = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
            String Myip = null;
			try {
				Myip = in2.readLine();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		    
		    for ( int i = 0 ; i<res.length;++i){
		        if ( !res[i].equals(Myip) ) {
		        	String ip_en_cours = "tcp://"+res[i]+":9800";
		        	URI theSeed = URI.create(ip_en_cours);
		        	try {
					    System.out.println("server added : " + theSeed);
					    networkManager.getConfigurator().addSeedRendezvous(theSeed);
				    } catch (IOException e) {
				        System.out.println("erreur");
					    e.printStackTrace();
				    }
				 }
		    }
		}
		
			/*String ip = ips[0]; // Récupération du noms du fichier des adresses IPs
			NetworkManager networkManager = node.getNetworkManager(); // Récupération du networkManager en cours
			
			FileInputStream fis = null;
		    try {
				fis = new FileInputStream(new File(ip)); // Ouverture du fichier
				BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
			    String adresse =null;
				
			    try {
					while((adresse = reader.readLine() ) != null){ // Tant qu'il y a des adresses définies dans le fichier ( Une par ligne )
						
						String ip_en_cours = "tcp://"+adresse; // Mise sous bon format de l'adresse.
						URI theSeed = URI.create(ip_en_cours); // Création d'un URI en fonction de l'adresse en cours.
						try {
							System.out.println("server added : " + theSeed);
							networkManager.getConfigurator().addSeedRendezvous(theSeed);
						} catch (IOException e) {
							e.printStackTrace();
						}
						
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("Aucune adresse serveur.");
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				System.out.println("Fichier d'adresse serveur inexistant.");	
			}
		}*/
		
	}

}

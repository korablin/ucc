package net.floodlightcontroller.ucc;

import java.util.Collection;
import java.util.Map;
 
import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.types.EthType;
import org.projectfloodlight.openflow.types.IPv6Address;
import org.projectfloodlight.openflow.types.IpProtocol;
import org.projectfloodlight.openflow.types.MacAddress;
 
import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.IListener.Command;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.mactracker.MACTracker;
import net.floodlightcontroller.core.IFloodlightProviderService;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.ICMP;
import net.floodlightcontroller.packet.IPacket;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.packet.IPv6;
import net.floodlightcontroller.packet.PacketParsingException;
//import net.floodlightcontroller.packet.Ucc;
import net.floodlightcontroller.packet.Ucc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;

public class UCC implements IOFMessageListener, IFloodlightModule {
	
	protected IFloodlightProviderService floodlightProvider;
	protected Set<Long> macAddresses;
	protected static Logger logger;
 
 
    @Override
    public boolean isCallbackOrderingPrereq(OFType type, String name) {
        // TODO Auto-generated method stub
        return false;
    }
 
    @Override
    public boolean isCallbackOrderingPostreq(OFType type, String name) {
        // TODO Auto-generated method stub
        return false;
    }
 
    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleServices() {
        // TODO Auto-generated method stub
        return null;
    }
 
    @Override
    public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
        // TODO Auto-generated method stub
        return null;
    }
 
   
    @Override
    public void init(FloodlightModuleContext context) throws FloodlightModuleException {
        floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
        macAddresses = new ConcurrentSkipListSet<Long>();
        logger = LoggerFactory.getLogger(MACTracker.class);
        logger.info("initialized with mac addrs {}", macAddresses);
    }

 
    @Override
    public void startUp(FloodlightModuleContext context) {
        floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
        logger.info("startUp passed");
    }
    
    
    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
        Collection<Class<? extends IFloodlightService>> l =
            new ArrayList<Class<? extends IFloodlightService>>();
        l.add(IFloodlightProviderService.class);
        return l;
    }
    
    @Override
    public String getName() {
        return MACTracker.class.getSimpleName();
    }
    
    @SuppressWarnings("static-access")
	@Override
    public net.floodlightcontroller.core.IListener.Command receive(IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
        switch (msg.getType()) {
        case PACKET_IN:
            /* Retrieve the deserialized packet in message */
            String message = msg.toString();
            Ethernet eth = IFloodlightProviderService.bcStore.get(cntx,
                    IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
            
            
       
            //Ucc ucc =new Ucc();
            //logger.info("Ucc is initialized: ", ucc.toString());
            if (eth.getEtherType() == EthType.IPv6) {
                /* We got an IPv6 packet; get the payload from Ethernet */
                IPv6 ipv6 = (IPv6) eth.getPayload();
                logger.info("Got an IPv6 packet {}:", ipv6.toString());
                
//                Ucc ucc_empty = new Ucc();
//                
//                
//                System.out.println("Here we go!");
//                System.out.println("Serialized:");
//                byte [] serialized = ucc_empty.serialize();
//                System.out.println(serialized);
//                System.out.println(" \n\n\n Desirialized:");
//                try {
//					IPacket desirialized = ucc_empty.deserialize(serialized, 0, 5);
//				} catch (PacketParsingException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//                System.out.println();
//                System.out.println("The END!");
                
                
                //byte[] serialized = ipv6.serialize();
                String ipv6_nextHeader = ipv6.getNextHeader().toString();
                logger.info("Got an IPv6 nextHeader: {}", ipv6_nextHeader);

                
                System.out.println(eth.getPayload());
                System.out.println("COMPARE");
                System.out.println(ipv6.getPayload());
                if (ipv6_nextHeader.equals("0x3a")) {
                	logger.info("header is 0x3a");
                	try{
//                	Ucc ucc = (Ucc) ipv6.getPayload();
                	
                	byte[] serialized = ipv6.serialize();
                	System.out.println("SERIALIZED:");
                	System.out.println(serialized);
                	//System.out.println(new BigInteger(serialized.toString().substring(3), 2));
                	//int n = Integer.parseInt(serialized.toString().substring(3), 2);
                	//System.out.println(Integer.toBinaryString(n));
                	Ucc ucc = new Ucc();

                	//ucc.setParent(ipv6);
                	ucc = (Ucc) ucc.deserialize(serialized,3,ipv6.getPayloadLength());
                	logger.info(" \n\n\n UCC: {} \n\n\n", ucc.toString());
                	
                	} catch (Exception ex) {
                		ex.printStackTrace();
                		logger.info("EXX: ", ex.toString());
                	}
                }             
                logger.info("NEXTHEADER: {}", ipv6.getNextHeader());
                logger.info("PAYLOAD: {}", ipv6.getPayload());
                  logger.info(ipv6.toString());
                
            } else {
            	logger.info(" Not an IPv6 packet ");
            }
            
            
//        	logger.info(" \n\n\n Message is : {}", message);
//        	logger.info("Data is: {}", this.getData(message));
//        	logger.info("Hexed data: {}", this.getHexData(message));

            break;
        default:
            break;
        }
        return Command.CONTINUE;
    }
    
    @SuppressWarnings("static-access")
    public String getHexData(String message) {
    	
    	ArrayList<String> list = new ArrayList<String>();
		String data = this.getData(message);
    	String[] nums = data.split(", ");
    	for (int i = 0; i < nums.length; i++) {
    		//int converted = this.convert(Integer.parseInt(nums[i]));
    		String converted = Integer.toHexString(Integer.parseInt(nums[i]));
    		list.add(converted);
    	}
    	String hexData = "";
    	for (String s : list)
    	{
    	    hexData += s + " ";
    	}
    	return hexData;
    }
    
    public static int convert(int n) {
    	return Integer.valueOf(String.valueOf(n), 16);
    }
    
    public static String getData(String str) {
    	Pattern p = Pattern.compile("\\[(.*?)\\]");
        Matcher m = p.matcher(str);
        while(m.find()) {
            return m.group(1);
        }
        return "";
    }
    
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    
    
    //public static int[] 
    

}

package net.floodlightcontroller.packet;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
//import java.util.Set;

//import org.projectfloodlight.openflow.types.IPv6Address;
import org.projectfloodlight.openflow.types.IpProtocol;

import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;


public class Ucc extends BasePacket {
	public static Map<IpProtocol, Class<? extends IPacket>> nextHeaderClassMap;
	protected static Logger logger;

	//needs to be corrected
	static {
		nextHeaderClassMap = new HashMap<IpProtocol, Class<? extends IPacket>>();
		nextHeaderClassMap.put(IpProtocol.TCP, TCP.class);
		nextHeaderClassMap.put(IpProtocol.UDP, UDP.class);
	}
	
	// 5 bytes is the real size of UCC header
	public static final int HEADER_LENGTH = 5;
    
	protected IpProtocol nextHeader;
	protected byte headerExtLength;
	protected byte cloudID;
	protected byte serviceID;
	protected byte tenantID;
	
	// To get the payload length
	//IPv6 parentIPv6 = (IPv6) this.getParent();
	
	protected short payloadLength; // = (short)((short) parentIPv6.getPayloadLength() - (short) HEADER_LENGTH);
	
	public Ucc() {
		super();
		nextHeader = IpProtocol.NONE;
		cloudID = 110;
		serviceID = 111;
		tenantID = 113;
		//logger.info("Ucc has been created");
	}
	
	public IpProtocol getNextHeader() {
		return nextHeader;
	}

	public Ucc setNextHeader(IpProtocol nextHeader) {
		this.nextHeader = nextHeader;
		return this;
	}
	
	public byte getHeaderExtLength(){
		return headerExtLength;
	}
	
	public Ucc setHeaderExtLength(byte headerExtLength){
		this.headerExtLength = headerExtLength;
		return this;
	}
	
	public byte getCloudId(){
		return cloudID;
	}

	public Ucc setCloudID(byte cloudID){
		this.cloudID = cloudID;
		return this;
	}

	public byte getServiceID(){
		return serviceID;
	}
	
	public Ucc setServiceID(byte serviceID){
		this.serviceID = serviceID;
		return this;
	}
	
	public byte getTenantID(){
		return tenantID;
	}
	public Ucc setTenantID(byte tenantID){
		this.tenantID = tenantID;
		return this;
	}
	

	@Override
	public byte[] serialize() {
		// Get the raw bytes of the payload we encapsulate.
		byte[] payloadData = null;
		if (this.payload != null) {
			this.payload.setParent(this);
			payloadData = this.payload.serialize();
			/* 
			 * If we forgot to include the IpProtocol before serializing, 
			 * try to ascertain what it is from the payload. If it's not
			 * a payload type we know about, we'll throw an exception.
			 */
		}
		// Update our internal payload length.
		this.payloadLength = (short) ((payloadData != null) ? payloadData.length : 0);
		// Create a byte buffer to hold the IPv6 packet structure.
		byte[] data = new byte[HEADER_LENGTH + this.payloadLength];
		ByteBuffer bb = ByteBuffer.wrap(data);
		// Add header fields to the byte buffer in the correct order.
		// Fear not the bit magic that must occur.
		bb.put((byte) this.nextHeader.getIpProtocolNumber());
		bb.put(this.headerExtLength);
		bb.put(this.cloudID);
		bb.put(this.serviceID);
		bb.put(this.tenantID);
		// Add the payload to the byte buffer, if necessary.
		if (payloadData != null)
			bb.put(payloadData);
		// We're done! Return the data.
		return data;
	}

	@Override
	public String toString() {
		return "UCC [nextheader=" + nextHeader + ", headerExtLength=" + headerExtLength
				+ ", cloudID=" + cloudID + ", serviceID="
				+ serviceID + ", tenantID=" + tenantID + ", parent="
				+ parent + ", payload=" + payload + "]";
	}

	@Override
	public IPacket deserialize(byte[] data, int offset, int length)
			throws PacketParsingException {
		// Wrap the data in a byte buffer for easier retrieval.
		ByteBuffer bb = ByteBuffer.wrap(data, offset, length);
		// Retrieve values from Ucc header.
		//byte firstByte = bb.get();
		//byte secondByte = bb.get();
		this.nextHeader = IpProtocol.of(bb.get());
		this.headerExtLength = bb.get();
		this.cloudID = bb.get();
		this.serviceID = bb.get();
		this.tenantID = bb.get();
		
		// Retrieve the payload, if possible.
		IPacket payload;
		if (Ucc.nextHeaderClassMap.containsKey(this.nextHeader)) {
			Class<? extends IPacket> clazz = Ucc.nextHeaderClassMap.get(this.nextHeader);
			try {
				payload = clazz.newInstance();
			} catch (Exception e) {
				throw new RuntimeException("Error parsing payload for Ucc packet", e);
			}
		} else {
			payload = new Data();
		}
		// Deserialize as much of the payload as we can (hopefully all of it).
		this.payload = payload.deserialize(data, bb.position(),
				Math.min(this.payloadLength, bb.limit() - bb.position()));
		this.payload.setParent(this);
		// We're done!
		return this;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((nextHeader == null) ? 0 : nextHeader.hashCode());
		result = prime * result + headerExtLength;
		result = prime * result + cloudID;
		result = prime * result + serviceID;
		result = prime * result + tenantID;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof Ucc))
			return false;
		Ucc other = (Ucc) obj;

		if (nextHeader == null) {
			if (other.nextHeader != null)
				return false;
		} else if (!nextHeader.equals(other.nextHeader))
			return false;
		if (payloadLength != other.payloadLength)
			return false;
		if (headerExtLength != other.headerExtLength)
			return false;
		if (cloudID != other.cloudID)
			return false;
		if (serviceID != other.serviceID)
			return false;
		if (tenantID != other.tenantID)
			return false;
		return true;
	}
}

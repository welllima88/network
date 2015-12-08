/*
 * All time values are interpreted in seconds. All data amounts are
 * interpreted in bytes. 
 */
public class Constants {
	public static int DEFAULT_WINDOW_SIZE = 10;
	public static TCP DEFAULT_TCP = TCP.RENO;
	public static int PACKET_SIZE = 1024;
	public static int ACK_SIZE = 64;
	public static double TCP_FAST_TIME_INTERVAL = 0.01;
	
	public static String[] hostFields = {"sendRate", "receiveRate"};
	public static String[] linkFields = {"occupancyData", "packetsLost", "flowRate"};
	public static String[] flowFields = {"sendRate", "receiveRate", "rtt", "windowSize", "slowStartThresh"};

	
	// Packet types
	public enum PacketType {
		DATA, ACK
	}

	// Direction types
	public enum Direction {
		RIGHT, LEFT
	}
	
	// TCP Congestion Control Types
	public enum TCP {
		RENO, FAST
	}
}

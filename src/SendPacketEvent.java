import java.util.List;

public class SendPacketEvent extends Event {
	Packet packet;

	public SendPacketEvent(double time, Packet packet) {
		super(time);
		this.packet = packet;
	}

	@Override
	public List<Event> handle() {
		// TODO Auto-generated method stub
		return null;
	}

}

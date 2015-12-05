import java.util.ArrayList;
import java.util.List;

public class BufferToLinkEvent extends Event {
	Link link;
	Constants.Direction direction;

	public BufferToLinkEvent(double time, Link link, Packet packet,
			Constants.Direction direction) {
		super(time, packet);
		this.link = link;
		this.direction = direction;
	}

	@Override
	// TODO: Does this consider link capacity at all?
	// TODO: Is this half-duplex or full-duplex?
	public List<Event> handle() {

		ArrayList<Event> newEvents = new ArrayList<Event>();

		// Handle transfer of the packet depending on direction
		if (direction == Constants.Direction.RIGHT) {
			link.leftBuffer.poll();
			newEvents.add(new ReceivePacketEvent(this.time + link.linkDelay,
					packet, link.rightEndPoint, link));
			link.currentLeftBufferAmt -= packet.size;
		} else {
			link.rightBuffer.poll();
			newEvents.add(new ReceivePacketEvent(this.time + link.linkDelay,
					packet, link.leftEndPoint, link));
			link.currentRightBufferAmt -= packet.size;
		}

		// If at least one packet is waiting to be sent, schedule the next
		// BufferToLinkEvent
		if (!(link.leftBuffer.isEmpty() && link.rightBuffer.isEmpty())) {
			Constants.Direction nextDir;
			Packet nextPacket;
			double delay;

			if (link.leftBuffer.isEmpty()) {
				// Left buffer is empty, choose the packet on the right
				nextDir = Constants.Direction.LEFT;
				nextPacket = link.rightBuffer.peek();

			} else if (link.rightBuffer.isEmpty()) {
				// Right buffer is empty, choose the packet on the left
				nextDir = Constants.Direction.RIGHT;
				nextPacket = link.leftBuffer.peek();

			} else {
				// Both buffers have elements, choose the packet that arrived
				// first
				double leftTime = link.leftArrivalTimes.peek();
				double rightTime = link.rightArrivalTimes.peek();

				if (leftTime < rightTime) {
					nextDir = Constants.Direction.RIGHT;
					nextPacket = link.leftBuffer.peek();
				} else {
					nextDir = Constants.Direction.LEFT;
					nextPacket = link.rightBuffer.peek();
				}
			}

			// If the next packet is going the same direction, then we only
			// consider propagation delay. If we are changing directions, then
			// we also need to consider link delay.
			if (nextDir == direction) {
				delay = nextPacket.size / link.linkRate;
			} else {
				delay = link.linkDelay + nextPacket.size / link.linkRate;
			}

			// Schedule the next BufferToLinkEvent based on the previous info
			newEvents.add(new BufferToLinkEvent(this.time + delay, link,
					nextPacket, nextDir));
		}

		// Return a list of events to add to the event priority queue
		return newEvents;
	}

	public String toString() {
		if (this.direction == Constants.Direction.RIGHT) {
			return super.toString()
					+ "\t\t\tEvent Type: BufferToLinkEvent\t\t\tDetails: Sending packet "
					+ this.packet.flowName + "-" + this.packet.id + "-"
					+ this.packet.packetType + " from "
					+ this.link.leftEndPoint.name + " to "
					+ this.link.rightEndPoint.name + " over link "
					+ this.link.linkName;
		} else {
			return super.toString()
					+ "\t\t\tEvent Type: BufferToLinkEvent\t\t\tDetails: Sending packet "
					+ this.packet.flowName + "-" + this.packet.id + "-"
					+ this.packet.packetType + " from "
					+ this.link.rightEndPoint.name + " to "
					+ this.link.leftEndPoint.name + " over link "
					+ this.link.linkName;
		}
	}
}

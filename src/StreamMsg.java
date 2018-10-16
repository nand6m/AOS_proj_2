import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

enum MsgType{neighbor,okay,terminate,PACK,NACK,parentRequest, broadcast, convergecast_ack};
public class StreamMsg implements Serializable {
	int sourceNodeId;	
	int immediateSourceNodeId;
	MsgType type;
	String message;

	public StreamMsg(){
		sourceNodeId = -1;
		immediateSourceNodeId = -1;
		type = MsgType.okay;
		message = " ";
	}
}

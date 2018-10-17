import java.io.Serializable;
import java.util.ArrayList;

enum MsgType{initiate,neighbor,okay,terminate,PACK,NACK,parentRequest, broadcast, convergeCast_ack};
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

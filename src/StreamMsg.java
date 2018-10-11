import java.io.Serializable;
import java.util.ArrayList;
enum MsgType{neighbor,okay,terminate,PACK,NACK,parentRequest, broadcast, convergeCast_ack};
public class StreamMsg implements Serializable {
	int sourceNodeId;	
	int immediateSourceNodeId;
	MsgType type;
	string message;

	public StreamMsg(){
		sourceNodeId = -1;
		immediateSourceNodeId = -1;
		type = MsgType.okay;
		message = " ";
	}
}

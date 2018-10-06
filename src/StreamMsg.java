import java.io.Serializable;
import java.util.ArrayList;
enum MsgType{neighbor,okay,terminate,PACK,NACK,parentRequest};
public class StreamMsg implements Serializable {
	int sourceNodeId;	
	MsgType type;
	string message;

	public StreamMsg(){
		sourceNodeId = -1;
		type = MsgType.okay;
		message = " ";
	}
}


/*
********** AOS - Project 2 - Fall 2018 ******

This class is used to store Root, Parent and ack count while performing broadcast operation
Generated Getter and Setter for each attribute used for accessing its value

 */

public class RootParent {
    int root;               //Node from which broadcast initiated
    int parentId;           //Node that sends a particular message
    int ack;                //Count of acknowledgments received from its child nodes

    RootParent(){
        root = 0;
        parentId = 0;
        ack = 0;
    }
// Getters
    public int getRoot() {
        return root;
    }

    public int getParentId() {
        return parentId;
    }

    public int getAck() {
        return ack;
    }

// Setters

    public void setRoot(int root) {
        this.root = root;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public void setAck(int ack) {
        this.ack = ack;
    }
}

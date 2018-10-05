/*
********** AOS - Project 2 - Fall 2018 ******

Configuration class is used to parse configuration file, extract all necessary information and store them in appropriate variables
*/

//NOTE : Testing pending

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class Configuration {
    static HashMap<Integer, Node> nodeHashMap = new HashMap<Integer, Node>(); // Map stores nodes and its corr. id,port,host & neighbours
    static ArrayList<Integer> portList = new ArrayList<Integer>();      //List of all port numbers
    static ArrayList<String> hostNames = new ArrayList<String>();      //List of all hosts
    static int noOfBroadcast;               //Specifies number of times broadcast operations need to executed
    public static Random rand = new Random(System.currentTimeMillis());
    
    public static void readFile() {
        String fileName = " ";//Provide path of config file
        int noOfNodes=0;
        String line = null;

        try {
            BufferedReader  reader = new BufferedReader(new FileReader(fileName));
            if (reader == null){
                System.out.println("Error in reading file");
                return;
            }
            while ((line=reader.readLine())!=null){
                line = line.trim();                 // Dicard empty lines
                if(line.isEmpty()){
                    continue;
                }                                  // Reads number of nodes in the graph
                else if (line.contains("# Number of nodes")) {
                    line = reader.readLine();
                    line.replaceAll("\\s+","");
                    noOfNodes = Integer.parseInt(line);
                }                                   // Reads number of broadcast operations to perform
                else if (line.contains("# Number of broadcast")) {
                    line = reader.readLine();
                    line.replaceAll("\\s+","");
                    noOfBroadcast = Integer.parseInt(line);
                }                          
                                                  //This block reads each node's host name, port & neighbors and
                else if (line.contains("# Hostname")){          // stores in corresponding lists and map
                    for (int k=1; k<=noOfNodes; k++){
                        line = reader.readLine();
                        if (line != null) {
                            String[] entries = line.split("\\s+");
                            hostNames.add(entries[0]);
                            portList.add(Integer.parseInt(entries[1]));
                            ArrayList<Integer> neighbors = new ArrayList<Integer>();
                            for (int j = 2; j < entries.length; j++) {
                                neighbors.add(Integer.parseInt(entries[j]));
                            }
                            nodeHashMap.put(k, new Node(k, hostNames.get(k - 1), portList.get(k - 1), neighbors));
                        }
                    }
                }
                else{
                    continue;
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


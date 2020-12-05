# AOS_proj_2_Broadcast
## Implementation of Broadcast service in distributed system

Project requirements are available in `project2.pdf`

## Running the program
1. Unzip the contents of the archive.
2. Make required modifications to the configuration file which is available in src folder.
3. Use SCP command or tool to copy to DC (Distributed Computing) machine.
4. Make required changes in launcher & cleanup scripts such as netid and folder path. These scripts are available in src folder.
5. Run the cleanup script to kill any zombie process.
6. Run launcher script, It would compile java files and display results on terminal.
7. Verify neighbors printed with topology (graph_20_nodes.png).



## Steps to test with different config file 
1. Move the config file to `src` folder in DC machine
2. Update cleanup and launcher scripts with the folder path of config file


## Sample graph
![Graph](https://github.com/nand6m/AOS_proj_2_Broadcast/blob/master/graph_20_nodes.png)

Adjacency list representation of above graph
```
# nodes 
20 

# Format : Machine_hostname Port Neighbor_List 
dc01  2222    20  2 3
dc02  2222    20  1 4 8
dc03  2222    1 4
dc04  2222    2 3 5
dc05  2222    4 6
dc06  2222    5 7 8
dc07  2222    6 9 13  17
dc08  2222    2 6
dc09  2222    7 14
dc10  2222    11
dc11  2222    10  12  19
dc12  2222    11
dc13  2222    7 18
dc14  2222    9 19
dc15  2222    20  16
dc16  2222    15
dc17  2222    7
dc18  2222    13
dc19  2222    11  14
dc20  2222    1 2 15
```

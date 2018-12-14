# AOS_proj_2_Broadcast
Implementation of Broadcast service in distributed system

Compiling & running the program
***********************************

1. Unzip the contents of the archive
2. Make required modifications to the configuration file which is available in src folder.
3. Use SCP command or tool to copy to DC machine.
4. Make required changes in launcher & cleanup scripts such as netid and folder path. These scripts are available in src folder.
5. Run the cleanup script to kill any zombie process.
6. Run launcher script, It would compile java files and display results on terminal.
7. Verify neighbors printed with topology (graph_20_nodes.png).



To test with different config file 
**************************************

1) Move the config file to src folder in DC machine
2) Update cleanup and launcher scripts with the folder path of config file

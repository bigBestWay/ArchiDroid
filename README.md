ArchiDroid
==========

## Introduction
**ArchiDroid** is a research based project, which is based on several static analysis tools, *IC3*, *Amandroid* and, *Flowdroid + IccTA*.

It takes three inputs:

1. APK file
2. ICC Model file (which can be generated using IC3)<br/>
- To generate ICC Model using IC3 folow step 1 to 4 from Pre-processing
3. Component Transition Graph from Amandroid (follow step 5 from Pre-processing to generate the graph)

## For Pre-processing use the follwoing instructions :
1. Download and extract the installation archive of [IC3](http://siis.cse.psu.edu/ic3/installation.html)

Note that, Android applications need to be retargeted to Java bytecode before running analysis using IC3. 
In order to do so, You can use Dare or any other alternative Library to convert the apk into it's bytecode before running script for IC3.

2. Download Dare [[Download Link](http://siis.cse.psu.edu/dare/installation.html)] and install as per instruction.
3. After extracting the Dare archive file, use Dare using the following command :<br/>
**_./dare -d <output_dir> <path_to_apk_file>_**
4. Run the python script, [runIC3.py](https://github.com/tanjina12/ArchiDroid/blob/master/PythonScript/runIC3.py) to launch IC3.
5. Download/Clone the [repository](https://github.com/tanjina12/Incubator) for generating component transition graph using Amandroid, which is forked from [Sangam](https://github.com/sangamk/Incubator) <br/>
   &nbsp;&nbsp;&nbsp;&nbsp; I. Create an empty ***succes.csv*** file in the Incubator project root directory. <br/>
   &nbsp;&nbsp;&nbsp;&nbsp; II. Create a directory, called ***apps*** in the root directory of Incubator (based on Amandroid) Static analyzer. <br/>
    &nbsp;&nbsp;&nbsp;&nbsp; III. Include ***.apk*** file into the ***apps*** directory and Run the ***Main.kt*** file from *Incubator/src/main/kotlin/* directory. <br/>
       &nbsp;&nbsp;&nbsp;&nbsp; IV. The generated componentent transition graph (in .xml format) will be stored in ***graphs*** folder under Incubator project root directory.

## Reconstruction using ArchiDroid 
ArchiDroid is an Eclipse project.
To run the app reconstruction process using ArchiDroid, you first need to configure the project accordingly.
### Project Configuration
The Configuration file takes four configuration parameters: <br/>
&nbsp;&nbsp;&nbsp;&nbsp; I. ***project.input_files*** : path to the input directory <br/>
&nbsp;&nbsp;&nbsp;&nbsp; II. ***project.android_jars*** : path to the Android SDK jar <br/>
&nbsp;&nbsp;&nbsp;&nbsp; III. ***project.result*** : path to the output directory <br/>
&nbsp;&nbsp;&nbsp;&nbsp; IV. ***project.icc_cofig*** is used to ENABLE/DISABLE the ICC feature in Flowdroid's callgraph. <br/>

 [**N.B.** the specified input dirctory must contain all of the three required inputs (e.g. *<app_name>.apk file*, *<icc_model_name>.txt* file, *<app_comp_trans_graph>.xml* file) as mentioned under Introduction ]
 
- Launcher class : [**Main.java**]
- After successful run, the reconstruction result will be stored in *output.json* file in the specified directory.
### JSON to DiGraph
- Run the python script, [jsonToDot.py](https://github.com/tanjina12/ArchiDroid/blob/master/PythonScript/jsonToDot.py) to transform the raw json into a DiGraph.
- The resulting *graph.dot* file can be used to any Graph visualizer tool to make graph visualization of it.

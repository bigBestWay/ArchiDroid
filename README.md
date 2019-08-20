ArchiDroid
==========

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
       &nbsp;&nbsp;&nbsp;&nbsp; IV. Your generated componentent transition graph (in .xml format) will be stored in ***graphs*** folder under Incubator project root directory.

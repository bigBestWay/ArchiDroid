 # /**
 # * @author - Tanjina Islam
 # *
 # * @date - 19-08-2019
 # */
import sys
import os.path
import sys
import os
import argparse
from pathlib import Path
import shlex
import subprocess

def create_arg_parser():
    """"Creates and returns the ArgumentParser object."""

    parser = argparse.ArgumentParser(description='Script to launch IC3')
    parser.add_argument('-j', '--jvm', required=False, type=str, help='JVM options. For example use, -Xmx4g (for a 4GB heap)')
    parser.add_argument('-i', '--ic3path', required=True, type=str, help='Path to IC3 Jar file (ic3-0.2.0-full.jar)')
    parser.add_argument('-p', '--apkpath', required=True, type=str, help='Path to the .apk file')
    parser.add_argument('-r', '--retargetedpath', required=True, type=str, help='Dare output path to the retargeted application')
    parser.add_argument('-a', '--androidjarpath', required=True, type=str, help='Path to Android Jar file which resides inside IC3 Jar archive (ic3-0.2.0/android.jar)')
    parser.add_argument('-o', '--outputdir', required=True, type=str, help='Path to the output directory')

    return parser

def runCommand(option_jvm, path_ic3, path_apk, path_retargeted, path_android_jar, path_output):
	
	if option_jvm is not None:
		command = "java -" + option_jvm + " -jar " + path_ic3 + " -apkormanifest " +  path_apk + " -input " + path_retargeted + " -cp " + path_android_jar + " -protobuf " + path_output
	else:
		command = "java" + " -jar " + path_ic3 + " -apkormanifest " +  path_apk + " -input " + path_retargeted + " -cp " + path_android_jar + " -protobuf " + path_output
	
	args = shlex.split(command)
	print(args)
	subprocess.Popen(args)

# 	>>> import shlex, subprocess
# >>> command_line = raw_input()
# /bin/vikings -input eggs.txt -output "spam spam.txt" -cmd "echo '$MONEY'"
# >>> args = shlex.split(command_line)
# >>> print args
# ['/bin/vikings', '-input', 'eggs.txt', '-output', 'spam spam.txt', '-cmd', "echo '$MONEY'"]
# >>> p = subprocess.Popen(args) # Success!

# 	subprocess.call()

def main():

	arg_parser = create_arg_parser()
	parsed_args = arg_parser.parse_args()

	option_jvm = None

	if parsed_args.jvm is not None:
		option_jvm = parsed_args.jvm

	path_ic3 = parsed_args.ic3path
	path_apk = parsed_args.apkpath
	path_retargeted = parsed_args.retargetedpath
	path_android_jar = parsed_args.androidjarpath
	path_output = parsed_args.outputdir

	runCommand(option_jvm, path_ic3, path_apk, path_retargeted, path_android_jar, path_output)

if __name__ == '__main__':
    main()
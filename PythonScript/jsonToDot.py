 # /**
 # * @author - Tanjina Islam
 # *
 # * @date - 30-07-2019
 # */

import json
import sys
from json import loads
import pygraphviz as pgv
import pydot
from pydot import Dot, Edge
import os.path
import sys
import os
import argparse
from pathlib import Path

def create_arg_parser():
    """"Creates and returns the ArgumentParser object."""

    parser = argparse.ArgumentParser(description='Build Dot file from JSON to generate graph in GrapViz')
    parser.add_argument('-p', '--path', required=True, type=Path, help='path to the input .json file')
    parser.add_argument('-o', '--out', required=True, type=Path, help='path to the output .dot file, if the file path does not exist, output is written to "./graph.dot"')

    return parser

def loadJson(file_name):
	with open(file_name, "r") as read_file:
		json_data = json.load(read_file)
	# Convert back to JSON & print to stderr so we can verify that the tree is correct.
	# Pretty Printing JSON string back
	print(json.dumps(json_data, indent=4), file=sys.stderr)
	return json_data

def getComponents(dict_data):
	for key,val in dict_data.items():
		if 'AppComponents' in key:
			comp_dict = {obj['name']: obj['type'] for obj in val}
		return comp_dict

def getTransitions(dict_data, graph):
	labelName = None
	for key,val in dict_data.items():
		if 'CompTranitions' in key:
			print(len(val))
			for obj in val:
				#print("obj in CompTranitions - >>> ")
				#print(obj)

				if 'Direct' in obj['type']:
					edgeColor = 'green' 

				if 'ICC' in obj['type']:
					edgeColor = 'red'
					labelName = "ICC" 

				if 'ParentChild' in obj['type']:
					edgeColor = 'blue'
					labelName = "ParentChild" 

				methodCount = 0

				for item in obj:
					if 'calledMethods' in item:
						# print("item in obj Leng- >>> ")
						# print(obj[item])
						# print(len(obj[item]))
						methodCount = len(obj[item])

				if methodCount == 0:
					# With Edge Labels
					edge = pydot.Edge(obj['source'], obj['target'], color = edgeColor, label = labelName, fontname = 'Courier', fontsize = 7)
					# Without Edge Labels
					edge = pydot.Edge(obj['source'], obj['target'], color = edgeColor, fontname = 'Courier', fontsize = 7)
					graph.add_edge(edge)
					print(edge)
				elif methodCount > 0 :
					for i in range(0,methodCount):
						labelName = "Direct: " + obj['calledMethods'][i] + '()'

						# With Edge Labels
						edge = pydot.Edge(obj['source'], obj['target'], color = edgeColor, label = labelName, fontname = 'Courier', fontsize = 7)

						# Without Edge Labels
						# edge = pydot.Edge(obj['source'], obj['target'], color = edgeColor, fontname = 'Courier', fontsize = 7)
						graph.add_edge(edge)
					#methodCount = -1
						print(edge)
				print()

# Generates Dot graph for GraphViz
def buildDotGraph(dict_data):
	shapeType = None
	colorType = None
	comp_dict =  getComponents(dict_data) 
	#print(comp_dict)
	# create a new digraph with pydot.Dot()
	graph = pydot.Dot(graph_type='digraph', splines='true', overlap='false', nodesep=0.25)
	graph.set_node_defaults(fontname='Courier', fontsize=8, height=0.25, width=0.25) 

	for key, val in comp_dict.items():
		styleType = 'filled'
		if 'Activity' in val:
			shapeType = 'box'
			colorType = 'brown'

		if 'Fragment' in val:
			shapeType = 'box'
			colorType = 'orange'
			styleType = 'dashed'

		if 'Service' in val:
			shapeType = 'hexagon'
			colorType = 'skyblue'

		if 'Receiver' in val:
			shapeType = 'triangle'
			colorType = 'limegreen'

		if 'Provider' in val:
			shapeType = 'circle'
			colorType = 'cyan'

		if 'PlainJava' in val:
			shapeType = 'octagon'
			colorType = 'violet'
	
		node = pydot.Node(key, style = styleType, color = colorType, shape=shapeType)	
		graph.add_node(node) # adds node 'a'

	getTransitions(dict_data, graph)
	print(graph)
	return graph

def main():

	arg_parser = create_arg_parser()
	parsed_args = arg_parser.parse_args()

	#print(parsed_args.path, type(parsed_args.path), parsed_args.path.exists())
	
	input_file_path = parsed_args.path
	output_file_path = parsed_args.out
	# Check if input file path exits
	if input_file_path.exists():
		# Get filename
		input_file_name = os.path.basename(input_file_path)
		#print(input_file_name)
		dict_data = loadJson(input_file_name)
		dot_graph = buildDotGraph(dict_data)
		#buildDotGraph(dict_data)

		#Check if output file path exits and write to a .dot file for GraphViz
		if output_file_path.exists():
			#print("Yes")
			# Get filename
			#output_file_name = os.path.basename(output_file_path)
			if os.path.isdir(output_file_path):
				#print("Dir")
				output_file_name = str(output_file_path) + '/graph.dot'
			elif os.path.isfile(output_file_path):
				#print("File")
				# Get filename
				output_file_name = output_file_path
			with open(output_file_name, 'w') as out:
				out.write(str(dot_graph))
				print("<<< Successfully generated the .dot file into the specified output directory >>>")
		else:
			print("Output path does not exist! Output will be written into the 'graph.dot' file and will be stored into the root directory")
			output_file_name = './graph.dot'
			with open(output_file_name, 'w') as out:
				out.write(str(dot_graph))
	else:
		raise SystemExit("<<< No valid input file path found >>>\nSystem will exist. Make sure to provide the correct path to the JSON file and run the script again to generate .dot file from it.")	

if __name__ == '__main__':
    main()
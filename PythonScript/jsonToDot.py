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
	for key,val in dict_data.items():
		if 'CompTranitions' in key:
			for obj in val:
				if 'Direct' in obj['Type']:
					edge = pydot.Edge(obj['Source'], obj['Target'], color = 'green', style='dashed')
				#print(obj['Source'] + ' -> ' + obj['Target'] + ' -> ' + obj['Type'] )
				else:
					edge = pydot.Edge(obj['Source'], obj['Target'], color = 'red')
				graph.add_edge(edge)

def getShape(comp_dict):
	shape = None
	for key in comp_dict.items():
	#for key, val in comp_dict.items():
		if 'Activity' in comp_dict[key]:
			shape = activity_shape
		if 'Service' in comp_dict[key]:
			shape = service_shape
		if 'PlainJava' in comp_dict[key]:
			shape = plain_java_shape
	return shape

def buildDotGraph(dict_data):
	shapeType = None
	colorType = None
	comp_dict =  getComponents(dict_data) 
	print(comp_dict)
	# create a new digraph with pydot.Dot()
	graph = pydot.Dot(graph_type='digraph')
	graph.set_node_defaults(style='filled', fontname='Courier', fontsize='10')

	for key, val in comp_dict.items():
		if 'Activity' in val:
			shapeType = 'box'
			colorType = 'red'

		if 'Service' in val:
			shapeType = 'hexagon'
			colorType = 'blue'

		if 'Receiver' in val:
			shapeType = 'circle'
			colorType = 'green'

		if 'Provider' in val:
			shapeType = 'triangle'
			colorType = 'cyan'

		if 'PlainJava' in val:
			shapeType = 'pentagon'
			colorType = 'grey'
	
		node = pydot.Node(key, color = colorType, shape=shapeType)	
		graph.add_node(node) # adds node 'a'

	getTransitions(dict_data, graph)
	print(graph)
	return graph

def main():

	arg_parser = create_arg_parser()
	parsed_args = arg_parser.parse_args()

	print(parsed_args.path, type(parsed_args.path), parsed_args.path.exists())
	
	input_file_path = parsed_args.path
	output_file_path = parsed_args.out
	# Check if input file path exits
	if input_file_path.exists():
		# Get filename
		input_file_name = os.path.basename(input_file_path)
		#print(input_file_name)
		dict_data = loadJson(input_file_name)
		dot_graph = buildDotGraph(dict_data)

		#Check if output file path exits
		if output_file_path.exists():
			print("Yes")
			# Get filename
			#output_file_name = os.path.basename(output_file_path)
			if os.path.isdir(output_file_path):
				print("Dir")
				output_file_name = str(output_file_path) + '/graph.dot'
			elif os.path.isfile(output_file_path):
				print("File")
				# Get filename
				output_file_name = output_file_path
			with open(output_file_name, 'w') as out:
				out.write(str(dot_graph))
		else:
			print("NO")
			output_file_name = './graph.dot'
			with open(output_file_name, 'w') as out:
				out.write(str(dot_graph))
	else:
		raise SystemExit("<<< No valid input file path found >>>\nSystem will exist. Make sure to provide the correct path to the JSON file and run the script again to generate .dot file from it.")	

if __name__ == '__main__':
    main()
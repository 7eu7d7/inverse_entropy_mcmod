import os
import sys

name=sys.argv[1]

with open('./bs_example.json') as exp:
	exp_str=exp.read()
	with open(f'./blockstates/{name}.json','w+') as f:
		f.write(exp_str.replace('<name>',name))

with open('./blk_example.json') as exp:
	exp_str=exp.read()
	with open(f'./models/block/{name}.json','w+') as f:
		f.write(exp_str.replace('<name>',name))
		
with open('./item_example.json') as exp:
	exp_str=exp.read()
	with open(f'./models/item/{name}.json','w+') as f:
		f.write(exp_str.replace('<name>',name))
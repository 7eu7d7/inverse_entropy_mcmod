import os
import sys

name=sys.argv[1]

with open(name) as mod:
	mod_strs=mod.readlines()
	for i,x in enumerate(mod_strs):
		if x.startswith('f '):
			data=x.split()
			line=data[0]+' '
			for item in data[1:]:
				ff=item.split('/')
				line+=f'{ff[0]}/{ff[1]} '
			
			mod_strs[i]=line[:-1]+'\n'
	print(''.join(mod_strs))
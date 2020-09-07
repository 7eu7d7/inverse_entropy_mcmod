import os
import sys

name=sys.argv[1]
ofx=float(sys.argv[2])
ofy=float(sys.argv[3])
ofz=float(sys.argv[4])

with open(name) as mod:
	mod_strs=mod.readlines()
	for i,x in enumerate(mod_strs):
		if x.startswith('v '):
			data=x.split()
			mod_strs[i]=f'v {float(data[1])-ofx} {float(data[2])-ofy} {float(data[3])-ofz}\n'
	print(''.join(mod_strs))
with open('./qrobot.obj') as f:
	datas=f.readlines()
	for i,x in enumerate(datas):
		if x.startswith('v '):
			line=x.split()
			datas[i]=f'vt {max(0, min(1, float(line[1])))} {max(0, min(1, float(line[2])))}\n'
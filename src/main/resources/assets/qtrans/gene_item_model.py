import os


with open('./models/item/example_item.json') as exp:
	expf=exp.read()
	with open('./stuff_list.txt') as f:
		items=f.readlines()
		for x in items:
			x=x.strip()
			if x.find('[')!=-1:
				x=x[:x.find('[')]
			with open(f'./models/item/{x}.json','w+') as f2:
				f2.write(expf % x);
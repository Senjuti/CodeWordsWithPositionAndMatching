from bs4 import BeautifulSoup
import urllib

locus = []
write_file = open('tmp.txt','w+')

def assign_char_index(file_name):
	page = urllib.urlopen(file_name)
	page_soup = urllib.urlopen(file_name)
	soup = BeautifulSoup(page_soup.read(), 'lxml')
	i = 0
	for line in page_soup:
		#c = get_code_terms(line)
		tmp = (i)
		locus.append(tmp)
		i+=1

def get_code_terms(line):
	class_like = line.find_all('code', class_='classname')
	#interface_like = line.find_all('code', class_='interfacename')
	#method_like = line.find_all('code', class_='methodname')

	for c in line.find_all('code', class_='classname'):
		return str(c.text)
		

file_name = "HttpClient.html"
assign_char_index(file_name)
for l in locus:
	write_file.write(str(l) + "\n")
write_file.close()


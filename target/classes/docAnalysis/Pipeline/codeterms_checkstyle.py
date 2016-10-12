from bs4 import BeautifulSoup
from textstat.textstat import textstat 
import urllib

filename_qualified_names = open('HttpClientclasses.txt', 'r')
write_file = open('HttpClientcodenames.txt','w+')
text = ''

def return_text(url):
	page = urllib.urlopen(url)
	soup = BeautifulSoup(page.read(),'lxml')
	paras = soup.findAll('p')
	for para in paras:
		text = text + ' '.join(word_tokenize(para.text))

def matched_name(code_term):
	return code_term

def get_links(base_url, start_url):
	page = urllib.urlopen(start_url)
	soup = BeautifulSoup(page.read(),'lxml')
	chapters = soup.find_all('span',class_='chapter')
    
	for chapter in chapters: 
		chapter_url = base_url + chapter.a['href']
		chapter_page = urllib.urlopen(chapter_url)
		chapter_soup_object = BeautifulSoup(chapter_page.read(),'lxml')
		subsections = chapter_soup_object.find_all('div',class_='section') 
        
	for subsection in subsections:
		return_text(url)
		#get_code_terms(subsection)
	print len(text)

def get_code_terms(subsection): 
	class_like = subsection.find_all('code', class_='classname')
	interface_like = subsection.find_all('code', class_='interfacename')
	method_like = subsection.find_all('code', class_='methodname')

	for c in class_like:
		write_file.write(str(c.text) + ', ' + char_index + ', ' + matched_name(c.text) + '\n')

	for i in interface_like:
		write_file.write(str(i.text) + ', ' + char_index + ', ' + matched_name(i.text) + '\n')

	for m in method_like:
		write_file.write(str(m.text) + ', ' + char_index + ', ' + matched_name(m.text) + '\n')

url="https://hc.apache.org/httpcomponents-client-ga/tutorial/html/index.html"
base_url = "https://hc.apache.org/httpcomponents-client-ga/tutorial/html/"
page = urllib.urlopen(url)
soup = BeautifulSoup(page.read(),'lxml')

write_file.close()

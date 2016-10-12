from bs4 import BeautifulSoup
import urllib

'''
Downloads all pages of different tutorials and stitches them together into a composite tutorial
'''

#Gets links for different tutorial chapters
def get_links_httpclient(base_url, start_url):
	tutorial_httpclient = "HttpClient.html"
	page = urllib.urlopen(start_url)
	soup = BeautifulSoup(page.read(),'lxml')
	chapters = soup.find_all('span', class_='chapter')
	i = 1
    
	for chapter in chapters: 
		chapter_url = base_url + chapter.a['href']
		save_page(chapter_url, base_url, i, tutorial_httpclient)
		i = i+1

def get_links_checkstyle(base_url, start_url):
	chapters_list = []
	tutorial_checkstyle = "Checkstyle.html"
	page = urllib.urlopen(start_url)
	soup = BeautifulSoup(page.read(),'lxml')
	chapters = soup.find_all('li', class_='none')
	i = 1

	for chapter in chapters:
		chapter_url = base_url + chapter.a['href']
		chapters_list.append(chapter_url)
	#Remove the last link - http://checkstyle.sourceforge.net/apidocs/index.html - since it links to APIdocs
	del chapters_list[-1]

	for chapter in chapters_list:	
		save_page(chapter, base_url, i, tutorial_checkstyle)
		i = i+1

def get_links_math(base_url, start_url):
	tutorial_math = "Math.html"
	lst = ["overview", "stat", "random", "linear", "analysis", "special", "utilities", "complex", "distribution", "fraction", "transform", "geometry", "optimization", "fitting", "leastsquares", "ode", "genetics", "filter", "ml", "exceptions"]
	i = 1
	for l in lst:
		chapter_url = base_url+l+".html"
		save_page(chapter_url, base_url, i, tutorial_math)
		i = i+1

def get_links_stripes(base_url, start_url):
	tutorial_stripes = "Stripes.html"
	page = urllib.urlopen(start_url)
	soup = BeautifulSoup(page.read(),'lxml')
	chapters = soup.find_all('a', href = True)
	i = 1

	for chapter in chapters: 
		chapter_url = base_url + chapter['href']
		print chapter_url
		#save_page(chapter_url, base_url, i, tutorial_httpclient)
		#i = i+1
		

#Saves tutorial pages individually and stitches them all together into one complete tutorial
def save_page(chapter_url, base_url, i, tutorial_name):
	page = urllib2.urlopen(chapter_url)
	page_content = page.read()
	page_name = str(i) + " " + chapter_url.replace(base_url, "")
	with open(page_name, 'w') as fid:
		fid.write(page_content)
	with open(tutorial_name, 'a') as fid:
		fid.write(page_content)

	
base_url_httpclient = "https://hc.apache.org/httpcomponents-client-ga/tutorial/html/"
url_httpclient = "https://hc.apache.org/httpcomponents-client-ga/tutorial/html/index.html"
folder_httpclient = "/home/senjuti/Project/Pipeline/HttpClient"
#get_links_httpclient(base_url_httpclient, url_httpclient)

base_url_checkstyle = "http://checkstyle.sourceforge.net/"
url_checkstyle = "http://checkstyle.sourceforge.net/config.html"
#get_links_checkstyle(base_url_checkstyle, url_checkstyle)

base_url_math = "http://commons.apache.org/proper/commons-math/userguide/"
url_math = "http://commons.apache.org/proper/commons-math/userguide/index.html"
#get_links_math(base_url_math, url_math)

base_url_stripes = "https://stripesframework.atlassian.net/"
url_stripes = "https://stripesframework.atlassian.net/wiki/pages/reorderpages.action?key=STRIPES"
get_links_stripes(base_url_stripes, url_stripes)


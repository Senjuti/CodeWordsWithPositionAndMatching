from bs4 import BeautifulSoup
import urllib
'''
Finds class names from APIdocs for: Android, Checkstyle, HttpClient, Math (Apache), Stripes
Finds method names from APIdocs for: Checkstyle, HttpClient, Math (Apache), Stripes
'''

def androidClassNames():
	write_file = open('Androidclassnames.txt','w+')
	url = "https://developer.android.com/reference/classes.html"
	page = urllib.urlopen(url)
	soup = BeautifulSoup(page.read(), 'lxml')
	class_names = soup.find_all('td', class_='jd-linkcol')
	for c in class_names:
		write_file.write(str(c.a['href']) + '\n')

def checkstyleClassNames():
	write_file = open('Checkstyleclassnames.txt','w+')
	url = "http://checkstyle.sourceforge.net/apidocs/allclasses-noframe.html"
	page = urllib.urlopen(url)
	soup = BeautifulSoup(page.read(), 'lxml')
	class_names = soup.find_all('li')
	for c in class_names:
		write_file.write(str(c.a['href']) + '\n')

def checkstyleMethodNames():
	write_file = open('Checkstylemethodnames.txt','w+')
	url = "http://checkstyle.sourceforge.net/apidocs/index-all.html"
	page = urllib.urlopen(url)
	soup = BeautifulSoup(page.read(), 'lxml')
	class_names = soup.find_all('span', class_='memberNameLink')
	for c in class_names:
		write_file.write(str(c.a['href']) + '\n')

def httpClientClassNames():
	write_file = open('HttpClientclasses.txt','a')
	url = "https://hc.apache.org/httpcomponents-client-ga/httpclient/apidocs/allclasses-noframe.html"
	page = urllib.urlopen(url)
	soup = BeautifulSoup(page.read(),'lxml')
	class_names = soup.find_all('li')
	for c in class_names:
		write_file.write(str(c.a['href']) + '\n')

def httpClientMethodNames():
	write_file = open('HttpClientmethods.txt','a')
	url = "https://hc.apache.org/httpcomponents-client-ga/httpclient/apidocs/index-all.html"
	page = urllib.urlopen(url)
	soup = BeautifulSoup(page.read(),'lxml')
	urls = soup.find_all('a', href = True)

	for c in urls:
		if (c.parent.name == 'span'):
			write_file.write(str(c['href']) + '\n')

def mathClassNames():
	write_file = open('Mathclassnames.txt','w+')
	url = "http://commons.apache.org/proper/commons-math/javadocs/api-3.3/allclasses-noframe.html"
	page = urllib.urlopen(url)
	soup = BeautifulSoup(page.read(),'lxml')
	class_names = soup.find_all('li')
	for c in class_names:
		write_file.write(str(c.a['href']) + '\n')

def mathMethodNames():
	write_file = open('Mathmethodnames.txt','w+')
	url = "http://commons.apache.org/proper/commons-math/javadocs/api-3.3/index-all.html"
	page = urllib.urlopen(url)
	soup = BeautifulSoup(page.read(),'lxml')
	urls = soup.find_all('a', href = True)

	for c in urls:
		if (c.parent.name == 'span'):
			write_file.write(str(c['href']) + '\n')

def stripesClassNames():
	write_file = open('Stripesclassnames.txt','w+')
	url = "https://stripesframework.ci.cloudbees.com/job/Stripes%201.6.0/javadoc/allclasses-noframe.html"
	page = urllib.urlopen(url)
	soup = BeautifulSoup(page.read(),'lxml')
	class_names = soup.find_all('li')
	for c in class_names:
		write_file.write(str(c.a['href']) + '\n')

def stripesMethodNames():
	write_file = open('Stripesmethodnames.txt','w+')
	url = "https://stripesframework.ci.cloudbees.com/job/Stripes%201.6.0/javadoc/index-all.html"
	page = urllib.urlopen(url)
	soup = BeautifulSoup(page.read(),'lxml')
	urls = soup.find_all('a', href = True)

	for c in urls:
		if (c.parent.name == 'span'):
			write_file.write(str(c['href']) + '\n')

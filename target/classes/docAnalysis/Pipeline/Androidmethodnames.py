from bs4 import BeautifulSoup
import urllib
import re 

'''write_file = open('Androidmethodnames.txt','w+')
base_url = "https://developer.android.com/reference/classes.html"
page = urllib.urlopen(base_url)
soup = BeautifulSoup(page.read(), 'lxml')
class_names = soup.find_all('td', class_='jd-linkcol')

for c in class_names:
	class_url = c.a['href']
	page_class = urllib.urlopen(class_url)
	soup2 = BeautifulSoup(page_class.read(), 'lxml')
	for ana in soup2.findAll('code'):
		if ana.parent.name == ('table', {'id': 'pubmethods'}):
			print ana.a['href']
'''
url="https://developer.android.com/reference/android/widget/AbsListView.html"
page = urllib.urlopen(url)
soup = BeautifulSoup(page.read(), 'lxml')
for a in soup.find_all('a', href = True):
	if (a.parent.name == 'code' and a.parent.parent.parent.parent.parent.parent.id == 'pubmethods'):
    		print (a['href'] + '\n')

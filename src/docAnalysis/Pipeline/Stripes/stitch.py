from bs4 import BeautifulSoup
import urllib
import glob, os

tutorial_name = "Stripes.html"
for file in glob.glob("*.html"):
	page = urllib.urlopen(file)
	page_content = page.read()
	with open(tutorial_name, 'a') as fid:
		fid.write(page_content)

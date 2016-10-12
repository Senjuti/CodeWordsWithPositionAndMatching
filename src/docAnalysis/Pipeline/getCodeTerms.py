from bs4 import BeautifulSoup
import urllib
import re

'''
Gets code-like terms for tutorials: Android, Checkstyle, HttpClient, Math (Apache), Stripes
'''

patterns = [line.rstrip('\n') for line in open('/home/senjuti/Project/regex.txt','r')]
regex_compiled_patterns = []
for pattern in patterns:
	regex_compiled_patterns.append(re.compile(pattern))

file_to_write = open('test.txt','w')

def filter_regex(list_to_convert):
	filtered_terms = []
	for item in list_to_convert:
		if len(str(item[0].encode('ascii','ignore')).strip()) > 1:
			filtered_terms.append(str(item[0].encode('ascii','ignore')))
	return filtered_terms

def get_code_terms_regex(url): 
	page = urllib.urlopen(url)
	html_page = BeautifulSoup(page.read(),'lxml')
	all_tags = html_page.find_all()
	code_term_list = []

	for regex in regex_compiled_patterns:
		for tag in all_tags:
			code_terms = re.findall(regex,tag.text)
			for code_term in code_terms:
				code_term_list.append(code_term) 
	return code_term_list

def get_beautiful_soup_object(url):
	page = urllib.urlopen(url)
	soup = BeautifulSoup(page.read(), 'lxml')
	return soup 

def get_beautiful_soup_object_file(filename):
	f = open(filename,'r')
	contents = f.read()
	soup = BeautifulSoup(contents, 'lxml')
	f.close()
	return soup

def check_classname(set_class_names, code_term): 
	if code_term.islower():
		return False
	if code_term.isdigit():
		return False
	for class_name in set_class_names:
		if class_name.find(code_term) != -1:
			return True
	return False

def check_method_names(set_method_names, method_term):
	if method_term.find('(') == -1:
		return False
	if method_term.find(')') == -1:
		return False
	for method_name in set_method_names:
		if method_name.find(method_term) != -1:
			return True
	return False 

def write_list_to_file(list_to_write, filename):
	file_to_write = open(filename,'w+')
	for item in list_to_write:
		file_to_write.write(str(item.text.encode('ascii','ignore')) + '\n')
	file_to_write.close()

def get_set_from_file(filename):
	f = open(filename,'r')
	s = str(f.readline())
	lines = set([])
	while(s):
		if s.find('---') == -1:
			lines.add(s.replace('\n',''))
		s = str(f.readline())
	return lines

def get_from_code_examples_programlisting(url, class_names, method_names, file_name, file_name_methods):
	list_of_names = list([]) 
	list_of_method_names = list([]) 
	soup = get_beautiful_soup_object_file(url)
	code_terms = soup.find_all('pre', class_='programlisting')
	for code_term in code_terms:
		#content is a code example
		content = code_term.text
		code = str(content.encode('ascii','ignore'))
		#Tokenizes code example by space-separated words 
		code_term_list = code.split(' ')
		#Checks if each space-separated word can be matched against the list of class names already known
		#If it can be, it is a class name
		for term in code_term_list:
			method_names_list = term.split('.')
			for method_term in method_names_list:
				if (check_method_names(method_names, method_term)):
					list_of_method_names.append(method_term)
			if (check_classname(class_names,term.replace('\n',''))):
				if term != '' and term !=  '\n':
					list_of_names.append(term.replace('\n',''))
	write_list_to_file(list_of_names, file_name)
	write_list_to_file(list_of_method_names, file_name_methods)

def get_from_code_examples_pre(url, class_names, method_names, file_name, file_name_methods):
	list_of_names = list([]) 
	list_of_method_names = list([]) 
	soup = get_beautiful_soup_object_file(url)
	code_terms = soup.find_all('pre')
	for code_term in code_terms:
		#content is a code example
		content = code_term.text	
		code = str(content.encode('ascii', 'ignore'))
		#Tokenizes code example by space-separated words 
		code_term_list = code.split(' ')
		#Checks if each space-separated word can be matched against the list of class names already known
		#If it can be, it is a class name
		for term in code_term_list:
			method_names_list = term.split('.')
			for method_term in method_names_list:
				if (check_method_names(method_names, method_term)):
					list_of_method_names.append(method_term)
			if (check_classname(class_names,term.replace('\n',''))):
				if term != '' and term !=  '\n':
					list_of_names.append(term.replace('\n',''))
	write_list_to_file(list_of_names, file_name)
	write_list_to_file(list_of_method_names, file_name_methods)

def get_from_text_android(url, file_name):
	soup = get_beautiful_soup_object_file(url)
	code_terms = soup.find_all('code')
	write_list_to_file(code_terms, file_name)

def get_from_text_stripes(url, file_name):
	soup = get_beautiful_soup_object_file(url)
	file_to_write = open(file_name,'w+')
	code_terms = soup.find_all('p')
	for code_term in code_terms:
		tags = code_term.find_all('code')
		for tag in tags:
			file_to_write.write(str(tag.text.encode('ascii','ignore')) + '\n')
	file_to_write.close()

def get_from_text_httpclient(url):
	filename_method = "HttpClientMethodText.txt"
	filename_class = "HttpClientClassText.txt"
	filename_interface = "HttpClientInterfaceText.txt"
	soup = get_beautiful_soup_object_file(url)
	method_terms = soup.find_all('code', class_="methodname")
	class_terms = soup.find_all('code', class_="classname")
	interface_terms = soup.find_all('code', class_="interfacename")
	write_list_to_file(method_terms, filename_method)
	write_list_to_file(class_terms, filename_class)
	write_list_to_file(interface_terms, filename_interface)

def get_from_text_math(url, file_name):
	soup = get_beautiful_soup_object_file(url)
	code_terms = soup.find_all('tt')
	write_list_to_file(code_terms, file_name)

def get_from_text_checkstyle(url, file_name):
	soup = get_beautiful_soup_object_file(url)
	code_terms = soup.find_all('tt')
	write_list_to_file(code_terms, file_name)

url_httpclient = "HttpClient.html"
class_names_httpclient = get_set_from_file('HttpClientclasses.txt')
method_names_httpclient = get_set_from_file('HttpClientmethods.txt') 
file_name_httpclient = "HttpClientCodeTermsMatched.txt"
file_name_methods_httpclient = "HttpClientMethodExamples.txt"
#get_from_text_httpclient(url_httpclient)
#get_from_code_examples_programlisting(url_httpclient, class_names_httpclient, method_names_httpclient, file_name_httpclient, file_name_methods_httpclient)

url_checkstyle = "Checkstyle.html"
class_names_checkstyle = get_set_from_file('Checkstyleclassnames.txt')
method_names_checkstyle = get_set_from_file('Checkstylemethodnames.txt') 
file_name_checkstyle = "CheckstyleCodeTermsMatched.txt"
file_name_checkstyle_text = "CheckstyleCodeTermsText.txt"
file_name_methods_checkstyle = "CheckstyleMethodExamples.txt"
#get_from_text_checkstyle(url_checkstyle, file_name_checkstyle_text)
#get_from_code_examples_pre(url_checkstyle, class_names_checkstyle, method_names_checkstyle, file_name_checkstyle, file_name_methods_checkstyle)

url_android = "Android.html"
class_names_android = get_set_from_file('Androidclassnames.txt')
method_names_android = get_set_from_file('Mathmethodnames.txt') 
file_name_android = "AndroidCodeTermsMatched.txt"
file_name_android_text = "AndroidCodeTermsText.txt"
file_name_methods_android = "AndroidMethodExamples.txt"
#get_from_code_examples_pre(url_android, class_names_android, method_names_android, file_name_android, file_name_methods_android)
#get_from_text_android(url_android, file_name_android_text)

url_math = "Math.html"
class_names_math = get_set_from_file('Mathclassnames.txt')
method_names_math = get_set_from_file('Mathmethodnames.txt') 
file_name_math = "MathCodeTermsMatched.txt"
file_name_methods_math = "MathMethodExamples.txt"
file_name_math_text = "MathCodeTermsText.txt"
#get_from_text_math(url_math, file_name_math_text)
#get_from_code_examples_pre(url_math, class_names_math, method_names_math, file_name_math, file_name_methods_math)

url_stripes = "Stripes.html"
class_names_stripes = get_set_from_file('Stripesclassnames.txt')
method_names_stripes = get_set_from_file('Stripesmethodnames.txt') 
file_name_stripes = "StripesCodeTermsMatched.txt"
file_name_stripes_text = "StripesCodeTermsText.txt"
file_name_methods_stripes = "StripesMethodExamples.txt"
#get_from_text_stripes(url_stripes, file_name_stripes_text)
#get_from_code_examples_pre(url_stripes, class_names_stripes, method_names_stripes, file_name_stripes, file_name_methods_stripes)


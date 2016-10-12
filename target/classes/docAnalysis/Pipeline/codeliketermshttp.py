"""
Find code-like terms from code examples
"""
from bs4 import BeautifulSoup
import urllib 

def get_beautiful_soup_object(url):
    page = urllib.urlopen(url)
    soup = BeautifulSoup(page.read(),'lxml')
    return soup 

def get_set_from_file(filename):
    f = open(filename,'r')
    s = str(f.readline())
    lines = set([])
    while(s):
        if s.find('---') == -1:
            lines.add(s.replace('\n',''))
        s = str(f.readline())
    return lines

def check_classname(set_class_names,code_term): 
    if code_term.islower():
        return False
    if code_term.isdigit():
        return False
    for class_name in set_class_names:
        if class_name.find(code_term) != -1:
            return True
    return False

def write_list_to_file(list_to_write,filename):
    file_to_write = open(filename,'w+')
    for item in list_to_write:
        file_to_write.write(str(item) + '\n')
    file_to_write.close()

url = "https://hc.apache.org/httpcomponents-client-ga/tutorial/html/index.html"
base_url = "https://hc.apache.org/httpcomponents-client-ga/tutorial/html/"
page = urllib.urlopen(url)
soup = BeautifulSoup(page.read(),'lxml')
chapters = soup.find_all('span', class_='chapter')
class_names = get_set_from_file('HttpClientclasses.txt')
method_names = get_set_from_file('HttpClientmethods.txt') 
list_of_names = list([]) 

for chapter in chapters:
    final_url = base_url + chapter.a['href']
    print final_url
    soup = get_beautiful_soup_object(final_url)
    code_terms = soup.find_all('pre',class_='programlisting')
    for code_term in code_terms:
        content = code_term.text
        code = str(content.encode('ascii','ignore'))
        code_term_list = code.split(' ')
        for term in code_term_list:
            if (check_classname(class_names,term.replace('\n',''))):
                if term != '' and term !=  '\n':
                    list_of_names.append(term.replace('\n',''))
print list_of_names
write_list_to_file(list_of_names, "HttpClientCodeTermsMatched.txt")

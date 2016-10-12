import re
inputfile = open('SpringmethodsAll.txt','w',encoding="utf8")

inputfile.write(re.sub("\[(.*?)yahoo(.*?)\n","",inputfile))

inputfile.close()

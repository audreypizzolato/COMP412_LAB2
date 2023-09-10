#!/usr/bin/python

import os

def check_file_type(type):
    for cdir, dirs, files in os.walk('./'):
        for file in files:
            if type in file:
                #if file.rsplit('.',1)[1].strip() == "i":
                #    return True
                return True
    return False

def get_id():
    #if not check_file_type('.i'):
    #    return "", ""
    tmp_file = 'tmpdump'
    #getname
    os.system("find . -iname \"README*\" > " + tmp_file)
    f = open(tmp_file, 'r')
    line = f.readline()
    if line == "":
        f.close()
        os.system('rm ' + tmp_file)
        print("No README file found.")
        return "unknown", "No README"

    tmp_file = "tmpdump2"
    cmd =  "grep -r 'NAME' `find . -name \"README*\"` > " + tmp_file
    os.system(cmd)
    f = open(tmp_file, 'r')
    line = f.readline()
    name = ""
    if 'NAME' in line:
        name = line.rsplit(':', 1)[1].strip()
    f.close()
    os.system('rm ' + tmp_file)

    #get ID
    tmp_file = "tmpdump3"
    cmd =  "grep -r 'NETID' `find . -name \"README*\"` > " + tmp_file
    os.system(cmd)
    f = open(tmp_file, 'r')
    line = f.readline()
    ID = ""

    if 'NETID' in line:
        ID = line.rsplit(':', 1)[1].strip()
    f.close()

    os.system('rm ' + tmp_file)
    if name !="" and  name[0] == '<':
        name = name[1: len(name)-1]
    if ID!= "" and ID[0] == '<':
        ID = ID[1: len(ID)-1]    
    return name, ID

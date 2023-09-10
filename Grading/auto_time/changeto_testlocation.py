#!/usr/bin/python

import os, datetime


#submission is the name of a given tar ball
def change_to_test_location(submission):
    
    #in case of "frag1 frag2 (frag3) frag4's"
    fixed_submission = submission.replace(" ", "\ ").replace("(", "\(").replace(")", "\)").replace("'", "\\'")
    folder = submission.split('.', 1)[0]
    fixed_folder = fixed_submission.split('.', 1)[0]

    # if folder exists, remove it to restart what we are doing
    if os.path.exists(folder):
        cmd = 'rm ' + fixed_folder + ' -rf'
        os.system(cmd)

    # make dir and cp
    #print('========' + submission)
    #print('========' + folder)
    os.makedirs(folder)
    cmd = 'cp ' + fixed_submission + ' ' + fixed_folder
    os.system(cmd)
    #print(fixed_submission + ' '+ submission + ' ' + fixed_folder +' ' + folder)

    # change dir
    os.chdir(folder)
    # unzip or untar
    if '.zip' in fixed_submission:
        cmd = 'unzip ./' + fixed_submission + ' > /dev/null'
    if '.tar' in fixed_submission:
        if 'tar.gz' in fixed_submission:
            cmd = 'tar xfvz ./' + fixed_submission + ' >& /dev/null'
        elif 'tar.bz' in fixed_submission:
            cmd = 'tar xfv ./' + fixed_submission + ' >& /dev/null'
        else:
            cmd = 'tar xfv ./' + fixed_submission + ' >& /dev/null'
    elif '.tgz' in fixed_submission:
        cmd = 'tar xfv ./' + fixed_submission + ' >& /dev/null'
    os.system(cmd)

    # find latest file modification date
    submission_date = datetime.date(2020,1,1)
    
    for root, dirs, files in os.walk('./'):
        for file in files:
            if os.path.exists('./' + file):
                mod_date = datetime.datetime.fromtimestamp(os.path.getmtime('./'+file)).date()
                if mod_date > submission_date:
                    submission_date = mod_date
        
    # rm the copied tar ball
    cmd = 'rm ' + fixed_submission
    os.system(cmd)

    return submission_date
    
def locate_exe(submission):
    global language

    language = ""
    
    #must have either makefile or 412fe script
    subDir = os.getcwd()
    os.system('find . -iname "makefile" > tmp')
    f = open('tmp', 'r')
    line = f.readline()
    next = f.readline()
    while next != "":
        if len(next) < len(line):
            line = next
        next = f.readline()            
    f.close()

    os.system('rm tmp')
    if line != "":
        print('--- building executable ---')
        os.chdir(line.strip().rsplit('/', 1)[0])
        os.system('make clean')
        os.system('make build')
        scan_makefile()
    
    print('--- built front end and should be ready to run ---\n')

    # to debug the act of moving around the filesystem, uncomment next 3 lines
    #os.system('ls')
    #os.chdir(subDir)
    #os.system('pwd')

    os.system('find . -name "412fe" > tmp')

    f = open('tmp', 'r')
    line = f.readline()
    next = f.readline()
    while next != "":
        if len(next) < len(line):
            line = next
        next = f.readline()            
    f.close()
    os.system('rm tmp')
    if line == "":
        print(submission + ' didn\'t folllow the instructions: no 412fe')
        return -1
    
    #change to dir that contains makefile/412fe
    os.chdir(line.strip().rsplit('/', 1)[0])
    os.system('chmod +x 412fe')
    if language == "":
        scan_shell_script_file()

    if language == "":
        language = "unknown "
    
    # verify that we are back where we belong
    #os.system('pwd')

    return 0

def scan_makefile():
    global language

    #print("\nIn scan_file():",)
    found_mf = 0
    found_java = 0
    found_cpp  = 0
    found_c    = 0

    try: 
        mf = open("Makefile")
        found_mf = 1
    except:
        try: 
            mf = open("makefile")
            found_mf = 1
        except:
            found_mf = 0

    if found_mf == 1:
        line = mf.readline()
        while line != "":
            if line.find("java") > -1:
                found_java = 1
            elif line.find(".cpp") > -1:
                found_cpp = 1
            elif line.find(".c") > -1 and line.find(".class") == -1:
                found_c = 1
            line = mf.readline()
        mf.close()

    if found_java == 1:
        #print("Language appears to be java")
        language += "java "
    if found_cpp == 1:
        #print("Language appears to be C++")
        language += "c++ "
    if found_c == 1: 
        #print("Language appears to be C")
        language += "c "

def scan_shell_script_file():
    global language

    #print("\nIn scan_file():",)
    found_fe = 0
    found_java = 0
    found_python = 0

    try:
        fe = open("412fe")
        found_fe = 1
    except:
        language = "unknown"

    if found_fe == 1:
        try:
            line = fe.readline()
            
            while line != "":
                #print("\t'"+line+"'")
                if line.find("python") > -1:
                    found_python = 1
                elif line.find("java") > -1:  
                    found_java = 1
                line = fe.readline()
        except:
            # 412fe is executable binary code, so do nothing
            found_fe = 1  # a nop to make the syntax work
        fe.close()

    if found_java == 1:
        #print("Language appears to be java")
        language += "java "
    if found_python == 1:
        #print("Language appears to be python")
        language += "python "
        
def  get_language():
    global language

    return language

            
        

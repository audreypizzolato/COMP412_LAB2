import java.util.*;
import java.io.*;

public class Scanner{
    BufferedReader reader;
    private String line;
    private int[][] transitionStates = new int[44][26];
    List<String> lexeme = new ArrayList<>();
    List<Integer> category = new ArrayList<>();
    int index = 0;
    String[] catagoryMap = new String[]{"MEMOP", "LOADI", "ARITHOP", "OUTPUT", "NOP", "CONSTANT", "REGISTER","COMMA", "INTO","EOF","EOL"};
    boolean createIR = true;
    int curCategory;
    //flags h=0, r=1, p=2, s=3
    int flag;
    int lineNum =1;
    Map<Character, Integer> charToInt = new HashMap<Character, Integer>();
    IR head=null;
    IR currentIR;
    IR newIR;
    IR tail;
    int correctLinesIR=0;
    int maxSR=0;
    int maxVR=0;
    int maxPR=0;
    int[] SRToVR;
    int[] LU;
    //allocating
    Stack<Integer> stackPR = new Stack<Integer>();
    int[] VRToPR;
    int[] PRToVR;
    int[] PRNU;
    int[] markPR;
    int[] VRtoSpill;
    int pr;
    int spillLoc=32768;
    int maxLive=0;
    int reserveReg;
    public Scanner(int flag){
        this.flag = flag;
        // set up transitionStates matrix
        for (int i = 0; i < transitionStates.length; i++) {
            for (int j = 0; j < transitionStates[0].length; j++) {
                transitionStates[i][j] = -1;
            }
        }
        transitionStates[0][0]=22;
        transitionStates[9][0]=10;
        transitionStates[6][1]=7;
        transitionStates[10][2]=11;
        transitionStates[22][2]=23;
        transitionStates[23][2]=24;
        transitionStates[4][3]=5;
        transitionStates[16][4]=17;
        transitionStates[14][5]=15;
        transitionStates[15][6]=16;
        transitionStates[0][7]=8;
        transitionStates[20][7]=21;
        transitionStates[0][8]=19;
        transitionStates[0][9]=28;
        transitionStates[2][9]=3;
        transitionStates[8][9]=9;
        transitionStates[25][9]=26;
        transitionStates[40][9]=41;
        transitionStates[26][10]=27;
        transitionStates[30][10]=31;
        transitionStates[41][10]=42;
        transitionStates[0][11]=13;
        transitionStates[3][11]=4;
        transitionStates[0][12]=1;
        transitionStates[8][12]=14;
        transitionStates[13][12]=14;
        transitionStates[1][13]=2;
        transitionStates[0][8]=19;
        transitionStates[17][13]=18;
        transitionStates[21][13]=18;
        transitionStates[29][13]=30;
        transitionStates[32][13]=33;
        transitionStates[1][14]=6;
        transitionStates[19][14]=20;
        transitionStates[28][14]=29;
        transitionStates[31][14]=32;
        transitionStates[0][15]=36;
        transitionStates[0][16]=34;
        transitionStates[34][17]=35;
        transitionStates[0][18]=39;
        transitionStates[39][18]=43;
        transitionStates[0][20]=37;
        transitionStates[0][21]=37;
        transitionStates[0][22]=38;
        transitionStates[13][22]=38;
        transitionStates[38][22]=38;
        transitionStates[5][23]=1;
        transitionStates[7][23]=1;
        transitionStates[11][23]=1;
        transitionStates[12][23]=1;
        transitionStates[18][23]=1;
        transitionStates[24][23]=1;
        transitionStates[27][23]=1;
        transitionStates[33][23]=1;
        transitionStates[35][23]=1;
        transitionStates[36][23]=1;
        transitionStates[37][23]=1;
        transitionStates[38][23]=1;
        transitionStates[39][23]=1;
        transitionStates[42][23]=1;
        transitionStates[43][23]=1;
        transitionStates[11][24]=12;
        transitionStates[0][25]=40;


        // set up map
        charToInt.put('a',0);
        charToInt.put('b',1);
        charToInt.put('d',2);
        charToInt.put('e',3);
        charToInt.put('f',4);
        charToInt.put('h',5);
        charToInt.put('i',6);
        charToInt.put('l',7);
        charToInt.put('m',8);
        charToInt.put('o',9);
        charToInt.put('p',10);
        charToInt.put('r',11);
        charToInt.put('s',12);
        charToInt.put('t',13);
        charToInt.put('u',14);
        charToInt.put(',',15);
        charToInt.put('=',16);
        charToInt.put('>',17);
        charToInt.put('/',18);
        charToInt.put(' ',19);
        charToInt.put('\t', 19);
        charToInt.put('\n',20);
        charToInt.put('\r',21);
        charToInt.put('0',22);
        charToInt.put('1',22);
        charToInt.put('2',22);
        charToInt.put('3',22);
        charToInt.put('4',22);
        charToInt.put('5',22);
        charToInt.put('6',22);
        charToInt.put('7',22);
        charToInt.put('8',22);
        charToInt.put('9',22);
        charToInt.put('I',24);
        charToInt.put('n', 25);
        
    }
    
    public void readFile(File f){
        try {
            reader = new BufferedReader(new FileReader(f));
        } catch (Exception ex) {
            System.err.println("ERROR : could not read file into BufferedReader");
        }
    }
    public void scanFile(){
        try {
            int currentState = 0;
            int rollback = 0;
            int transitionChar = 0;
            String word = "";
            int curCat = 0;
            int scanLineNum=1;
            int len;
            char c;

            long transitionTime = 0;
            long endStateTime =0;


             while((line = reader.readLine())!=null){
                word = "";
                currentState = 0;
                line+='\n';

                //System.out.println(line);
                len = line.length();
                
                for(int i=0; i<len; i++){
                    
                    c = line.charAt(i);
                    
                    // see if character can be mapped
                    transitionChar = charToInt.getOrDefault(c,-1);
                    if(transitionChar == -1){
                        //character does not exist in map
                        System.err.println("ERROR "+scanLineNum+": character is not defined in ILOC");
                        createIR=false;
                        break;
                    }
                    
                    if(transitionChar == 20 || transitionChar == 19 || transitionChar == 15){
                        
                        
                        if(rollback == 0 ){
                            if(transitionChar == 15){
                                word = ",";
                                rollback = 36;
                                currentState = 36;
                                continue;
                            }
                            continue;
                        }
 
                            //identify category
                            curCat = -1;
                            switch (rollback){
                                case 5:
                                case 11:
                                    curCat =0;
                                    break;
                                case 12:
                                    curCat = 1;
                                    break;
                                case 24:
                                case 7:
                                case 18:
                                    curCat = 2;
                                    break;
                                case 33:
                                    curCat = 3;
                                    break;
                                case 42:
                                    curCat = 4;
                                    break;
                                case 38:
                                    //check if register or digit when returned
                                    if(word.charAt(0)=='r'){
                                        curCat = 6;
                                    }
                                    else{
                                        curCat = 5;
                                    }
                                    break;
                                case 36:
                                    curCat = 7;
                                    break;
                                case 35:
                                    curCat = 8;
                                    break;

                            }
                            
                            
                            if(curCat==-1){
                                System.err.println("ERROR "+scanLineNum+": word is not defined in ILOC");
                                createIR = false;
                                break;
                            }
                            else{
                                lexeme.add(word);
                                category.add(curCat);
                                if(flag == 3){
                                    System.out.println(scanLineNum + ": <" + curCat+", \"" + word + "\" >");
                                }
                            }

                            
                            if(transitionChar == 15){
                                word = ",";
                                rollback = 36;
                                currentState = 36;
                                continue;
                            }

                        
                        word = "";
                        rollback = 0;
                        currentState = 0;
                    }
                    else{
                        
                        // see if the transition exists
                        if(transitionStates[currentState][transitionChar]>=0){
                            currentState = transitionStates[currentState][transitionChar];
                            rollback = currentState;
                            word+=c;
                            if(currentState==43){
                                break;
                            }
                        }
                        else if(transitionStates[rollback][23]==1){
                            
                            //identify category
                            curCat = -1;
                            switch (rollback){
                                case 5:
                                case 11:
                                    curCat =0;
                                    break;
                                case 12:
                                    curCat = 1;
                                    break;
                                case 24:
                                case 7:
                                case 18:
                                    curCat = 2;
                                    break;
                                case 33:
                                    curCat = 3;
                                    break;
                                case 42:
                                    curCat = 4;
                                    break;
                                case 38:
                                    //check if register or digit when returned
                                    if(word.charAt(0)=='r'){
                                        curCat = 6;
                                    }
                                    else{
                                        curCat = 5;
                                    }
                                    break;
                                case 36:
                                    curCat = 7;
                                    break;
                                case 35:
                                    curCat = 8;
                                    break;

                            }

                            if(curCat==-1){
                                System.err.println("ERROR "+scanLineNum+": word is not defined in ILOC");
                                createIR = false;
                                break;
                            }
                            else{
                                lexeme.add(word);
                                category.add(curCat);
                                if(flag == 3){
                                    System.out.println(scanLineNum + ": <" + curCat+", \"" + word + "\" >");
                                }
                            }
                            if(transitionStates[0][transitionChar]>=0){
                                currentState = transitionStates[0][transitionChar];
                                rollback = 0;
                                word=""+c;
                            } 
                            else{
                                System.err.println("ERROR "+scanLineNum+": word is not defined in ILOC");
                                createIR = false;
                                
                            }
                            
                        }
                        else{
                            //character does not transistion into 
                            //check if end state
                            System.err.println("ERROR "+scanLineNum+":");
                            createIR = false;
                            
                        }
                        //long endParse = System.nanoTime() - startParse;
                        //transitionTime += endParse;
                    }
                    
                    

                }
                lexeme.add("\\n");
                category.add(10);
                rollback=0;
                currentState=0;
                if(flag == 3){
                    System.out.println(scanLineNum + ": <10" + ", \"\\n" + "\" >");
                }
                scanLineNum++;

            }
            lexeme.add("");
            category.add(9);
            if(flag == 3){
                System.out.println(scanLineNum + ": <9" + ", \"" + "\" >");
            }
            //System.out.println("Trasition Parse Time: " + transitionTime);
            //System.out.println("End State Parse Time: " + endStateTime);
        } catch (Exception ex) {
        }
       
    }
    
    public boolean parser(){
        index = 0;
        int curCategory = category.get(index);
        int finishLine;
        

        while(curCategory!=9){
            switch (curCategory){
                case 0:
                    lexeme.get(index).toString();
                    finishLine = finishMemop();
                    if(finishLine == -1){
                        curCategory = category.get(index);
                        while(curCategory!=10){
                            index++;
                            curCategory = category.get(index);
                        }
                    }
                    else{
                        correctLinesIR++;
                        addIR(lineNum, lexeme.get(index-4), Integer.parseInt(lexeme.get(index-3).substring(1)),-1, Integer.parseInt(lexeme.get(index-1).substring(1)));
                    }
                    lineNum++; 
                    break;
                
                case 1:
                    finishLine = finishLoadI();
                    if(finishLine == -1){
                        curCategory = category.get(index);
                        while(curCategory!=10){
                            index++;
                            curCategory = category.get(index);
                        }
                    }
                    else{
                        correctLinesIR++;
                        addIR(lineNum, lexeme.get(index-4), Integer.parseInt(lexeme.get(index-3)),-1, Integer.parseInt(lexeme.get(index-1).substring(1)));
                    }
                    lineNum++;
                    break;
                case 2:
                    finishLine = finishArthop();
                    if(finishLine == -1){
                        curCategory = category.get(index);
                        while(curCategory!=10){
                            index++;
                            curCategory = category.get(index);
                        }
                    }
                    else{
                        correctLinesIR++;
                        addIR(lineNum, lexeme.get(index-6), Integer.parseInt(lexeme.get(index-5).substring(1)), Integer.parseInt(lexeme.get(index-3).substring(1)), Integer.parseInt(lexeme.get(index-1).substring(1)));
                    }
                    lineNum++;
                    break;
                case 3:
                    finishLine = finishOutput();
                    if(finishLine == -1){
                        curCategory = category.get(index);
                        while(curCategory!=10){
                            index++;
                            curCategory = category.get(index);
                        }
                    }
                    else{
                        correctLinesIR++;
                        addIR(lineNum, lexeme.get(index-2), Integer.parseInt(lexeme.get(index-1)),-1, -1);
                    }
                    lineNum++;
                    break;
                case 4:
                    finishLine = finishNop();
                    if(finishLine == -1){
                        curCategory = category.get(index);
                        while(curCategory!=10){
                            index++;
                            curCategory = category.get(index);
                        }
                    }
                    else{
                        correctLinesIR++;
                        addIR(lineNum, lexeme.get(index-1), -1,-1, -1);
                    }
                    lineNum++;
                    break;
                case 10:
                    lineNum++;
                    break;
                
                default:
                    
                    System.err.println("ERROR "+lineNum+": line could not be parsed");
                    createIR = false;
                    lineNum++;
                 
            }
            
            index++;
            curCategory = category.get(index);
            
        }

        return createIR;
    }
    public int finishMemop(){
        index++;
        curCategory = category.get(index);
        //REGISTER
        if(curCategory != 6){
            createIR = false;
            System.err.println("ERROR "+lineNum+": Missing target register");
            return -1;
        }
        
        index++;
        curCategory = category.get(index);
        //INTO
        if(curCategory != 8){
            createIR = false;
            System.err.println("ERROR "+lineNum+": Missing into character");
            return -1;
        }
            
        index++;
        curCategory = category.get(index);
        //REGISTER
        if(curCategory != 6){
            createIR = false;
            System.err.println("ERROR "+lineNum+": Missing target register");
            return -1;
        }

        index++;
        curCategory = category.get(index);
        //EOL
        if(curCategory == 10){
            //build IR
            return 0;
        }
        else{
            createIR = false;
            System.err.println("ERROR "+lineNum+": Missing end of line");
            return -1;
        }
             
    }
    public int finishLoadI(){
        index++;
        curCategory = category.get(index);
        //CONSTANT
        if(curCategory != 5){
            createIR = false;
            System.err.println("ERROR "+lineNum+": Missing constant");
            return -1;
        }
        
        index++;
        curCategory = category.get(index);
        //INTO
        if(curCategory != 8){
            createIR = false;
            System.err.println("ERROR "+lineNum+": Missing into character");
            return -1;
        }
            
        index++;
        curCategory = category.get(index);
        //REGISTER
        if(curCategory != 6){
            createIR = false;
            System.err.println("ERROR "+lineNum+": Missing target register");
            return -1;
        }

        index++;
        curCategory = category.get(index);
        //EOL
        if(curCategory == 10){
            //build IR
            return 0;
        }
        else{
            createIR = false;
            System.err.println("ERROR "+lineNum+": Missing end of line character");
            return -1;
        }
             
    }
    public int finishArthop(){
        index++;
        lexeme.get(index);
        curCategory = category.get(index);
        //REGISTER
        if(curCategory != 6){
            createIR = false;
            System.err.println("ERROR "+lineNum+": Missing target register");
            return -1;
        }
        
        index++;
        curCategory = category.get(index);
        //COMMA
        if(curCategory != 7){
            createIR = false;
            System.err.println("ERROR "+lineNum+": Missing comma");
            return -1;
        }
            
        index++;
        curCategory = category.get(index);
        //REGISTER
        if(curCategory != 6){
            createIR = false;
            System.err.println("ERROR "+lineNum+": Missing target register");
            return -1;
        }

        index++;
        curCategory = category.get(index);
        //INTO
        if(curCategory != 8){
            createIR = false;
            System.err.println("ERROR "+lineNum+": Missing into character");
            return -1;
        }

        index++;
        curCategory = category.get(index);
        //REGISTER
        if(curCategory != 6){
            createIR = false;
            System.err.println("ERROR "+lineNum+": Missing register");

            return -1;
        }

        index++;
        curCategory = category.get(index);
        //EOL
        if(curCategory == 10){
            //build IR
            return 0;
        }
        else{
            createIR = false;
            System.err.println("ERROR "+lineNum+": Missing end of line character");
            return -1;
        }
             
    }
    public int finishOutput(){
        index++;
        curCategory = category.get(index);
        //CONSTANT
        if(curCategory != 5){
            createIR = false;
            System.err.println("ERROR "+lineNum+": missing constant");
            return -1;
        }
        
        index++;
        curCategory = category.get(index);
        //EOL
        if(curCategory == 10){
            //build IR
            return 0;
        }
        else{
            createIR = false;
            System.err.println("ERROR "+lineNum+": missing end of line character");
          
            return -1;
        }
             
    }
    public int finishNop(){
        
        index++;
        curCategory = category.get(index);
        //EOL
        if(curCategory == 10){
            //build IR
            return 0;
        }
        else{
            createIR = false;
            System.err.println("ERROR "+lineNum+": missing end of line character");
            
            return -1;
        }
             
    }
    public void addIR(int line, String opcode, int reg1, int reg2, int reg3 ){
        newIR = new IR(line,opcode,reg1,-1,-1,-1,reg2,-1,-1,-1,reg3,-1,-1,-1);
        maxSR = Math.max(Math.max(Math.max(reg2, reg3),reg1),maxSR);
    
        if(head == null){
            head = newIR;
            head.prev = null;
            currentIR = newIR;
            tail = currentIR;
        }
        else{
            currentIR.setNext(newIR);
            newIR.setPrev(currentIR);
            currentIR = newIR;
            tail = currentIR;
        }

    }
    public void renaming(){
        SRToVR = new int[maxSR+1];
        LU = new int[maxSR+1];
        for(int i=0; i<=maxSR; i++){
            SRToVR[i] = -1;
            LU[i] = -1;
        }
        int vrName =0;
        currentIR = tail;
        int liveCount=0;
        while(currentIR != null){
            if(!currentIR.opcode.equals("output")){
                if(currentIR.opcode.equals("store")&&currentIR.op1SR!=-1){
                    if(SRToVR[currentIR.op1SR]==-1){
                        SRToVR[currentIR.op1SR] = vrName++;
                        
                    }
                    currentIR.op1VR = SRToVR[currentIR.op1SR];
                    currentIR.op1NU = LU[currentIR.op1SR];
                    liveCount++;
                    //maxLive = Math.max(liveCount, maxLive);
                }
                if(currentIR.op3SR!=-1){
                    if(SRToVR[currentIR.op3SR]==-1){
                        SRToVR[currentIR.op3SR] = vrName++;
                        liveCount++;
                        //maxLive = Math.max(liveCount, maxLive);
                    }
                    currentIR.op3VR = SRToVR[currentIR.op3SR];
                    currentIR.op3NU = LU[currentIR.op3SR];
                    if(!currentIR.opcode.equals("store")){
                        SRToVR[currentIR.op3SR] = -1;
                        LU[currentIR.op3SR] = -1;
                        liveCount--;
                    }
                    
                }
                
                if(currentIR.op1SR!=-1 && !currentIR.opcode.equals("loadI")&&!currentIR.opcode.equals("store")){
                    if(SRToVR[currentIR.op1SR]==-1){
                        SRToVR[currentIR.op1SR] = vrName++;
                        liveCount++;
                        //maxLive = Math.max(liveCount, maxLive);
                    }
                    currentIR.op1VR = SRToVR[currentIR.op1SR];
                    currentIR.op1NU = LU[currentIR.op1SR];
                    
                }
                if(currentIR.op2SR!=-1){
                    if(SRToVR[currentIR.op2SR]==-1){
                        SRToVR[currentIR.op2SR] = vrName++;
                        liveCount++;
                        //maxLive = Math.max(liveCount, maxLive);
                    }
                    currentIR.op2VR = SRToVR[currentIR.op2SR];
                    currentIR.op2NU = LU[currentIR.op2SR];
                    
                }
                maxLive = Math.max(liveCount, maxLive);
                if(currentIR.op1SR!=-1){
                    LU[currentIR.op1SR] = currentIR.line - 1;
                }
                if(currentIR.op2SR!=-1){
                    LU[currentIR.op2SR] = currentIR.line - 1;
                }
                if(currentIR.op3SR!=-1){
                    LU[currentIR.op3SR] = currentIR.line - 1;
                }
                
            }
            maxVR = Math.max(Math.max(Math.max(currentIR.op1VR, currentIR.op2VR),currentIR.op3VR),maxVR);
            currentIR = currentIR.prev;
            
        }
    }
    public int getAPR(int vr, int nu, IR current){
        int x;

        if(!stackPR.isEmpty()){
            x = stackPR.pop();
        }
        else{
            int spillPR=-1;
            int maxNUVal=-2;
            for(int i=0; i<maxPR; i++){
                if(PRNU[i]>maxNUVal && markPR[i] == -1){
                    spillPR=i;
                    maxNUVal=PRNU[i];
                }
            }
            int spillVR = PRToVR[spillPR];
            VRtoSpill[spillVR]=spillLoc;
            IR loadI = new IR(-1,"loadI",spillLoc,-1,-1,-1,-1,-1,-1,-1,-1,-1,reserveReg,-1);
            IR store = new IR(-1,"store",-1,spillVR,spillPR,-1,-1,-1,-1,-1,-1,-1,reserveReg,-1);
            spillLoc+=4;
            loadI.prev = current.prev;
            loadI.next = store;
            store.prev = loadI;
            store.next = current;
            current.prev.next = loadI;
            current.prev = store;
            VRToPR[spillVR]=-1;
            PRNU[spillPR]=-1;
            PRToVR[spillPR]=-1;
            x = spillPR;
        }
        VRToPR[vr] = x;
        PRToVR[x] = vr;
        PRNU[x] = nu;
        return x;

    }
    public void freeAPR(int pr){
        // invalid
        VRToPR[PRToVR[pr]] = -1;
        PRToVR[pr] = -1;
        PRNU[pr] = -1;
        stackPR.push(pr);

    }
    public void alloc(){
        maxVR++;
        VRToPR = new int[maxVR];
        PRToVR = new int[maxPR];
        markPR = new int[maxPR];
        PRNU = new int[maxPR];

        VRtoSpill = new int[maxVR];
        for(int vr=0; vr<maxVR;vr++){
            VRToPR[vr] = -1;
        }
        for(int pr=maxPR-1; pr>=0; pr--){
            PRToVR[pr] = -1;
            PRNU[pr] = -1;
            stackPR.push(pr);
            markPR[pr] = -1;
        }
        if(stackPR.size()<maxLive){
            reserveReg = stackPR.pop();
        }
        currentIR = head;
        while(currentIR != null){
            //clear the mark
            Arrays.fill(markPR, -1);
            //allocate uses
            if(!currentIR.opcode.equals("output") && !currentIR.opcode.equals("loadI")){
                if(currentIR.op1SR!=-1){
                    pr = VRToPR[currentIR.op1VR];
                    if(pr == -1){
                        currentIR.op1PR = getAPR(currentIR.op1VR, currentIR.op1NU, currentIR);
                        PRNU[currentIR.op1PR] = currentIR.op1NU;
                        restore(currentIR.op1VR,currentIR.op1PR, currentIR);
                    }
                    else{
                        currentIR.op1PR = pr;
                        PRNU[pr] = currentIR.op1NU;
                    }
                    markPR[currentIR.op1PR] = 1;
                }
                if(currentIR.op2SR!=-1){
                    pr = VRToPR[currentIR.op2VR];
                    if(pr == -1){
                        currentIR.op2PR = getAPR(currentIR.op2VR, currentIR.op2NU, currentIR);
                        PRNU[currentIR.op2PR] = currentIR.op2NU;
                        restore(currentIR.op2VR,currentIR.op2PR, currentIR);
                    }
                    else{
                        currentIR.op2PR = pr;
                        PRNU[pr] = currentIR.op2NU;

                    }
                    markPR[currentIR.op2PR] = 1;
                }
            }
            if(currentIR.opcode.equals("store")){
                if(currentIR.op3SR!=-1){
                    pr = VRToPR[currentIR.op3VR];
                    if(pr == -1){
                        currentIR.op3PR = getAPR(currentIR.op3VR, currentIR.op3NU, currentIR);
                        PRNU[currentIR.op3PR] = currentIR.op3NU;
                        restore(currentIR.op3VR,currentIR.op3PR, currentIR);
                    }
                    else{
                        currentIR.op3PR = pr;
                        PRNU[pr] = currentIR.op3NU;
                    }
                    markPR[currentIR.op3PR] = 1;
                }
            }
            //last use
            if(!currentIR.opcode.equals("output") && !currentIR.opcode.equals("loadI")){
                if(currentIR.op1NU == -1 && currentIR.op1PR!=-1 && PRToVR[currentIR.op1PR] > -1){
                    freeAPR(currentIR.op1PR);
                }
                if(currentIR.op2NU == -1 && currentIR.op2PR!=-1 && PRToVR[currentIR.op2PR] > -1){
                    freeAPR(currentIR.op2PR);
                }
            }
            if(currentIR.opcode.equals("store")){
                if(currentIR.op3NU == -1 && currentIR.op3PR!=-1 && PRToVR[currentIR.op3PR] > -1){
                    freeAPR(currentIR.op3PR);
                }
            }
            //clear the mark
            Arrays.fill(markPR, -1);

            //allocate def
            if(!currentIR.opcode.equals("store")&&currentIR.op3VR!=-1){
                currentIR.op3PR = getAPR(currentIR.op3VR, currentIR.op3NU, currentIR);
                PRNU[currentIR.op3PR] = currentIR.op3NU;
                markPR[currentIR.op3PR] = 1;
                if (currentIR.op3NU == -1) {
                    PRToVR[currentIR.op3PR] = -1;
                    stackPR.push(currentIR.op3PR);
                }
            }
            currentIR = currentIR.getNext();
        }
            

    }
    
    public void restore(int vr , int pr, IR current){
        int restoreLocation = VRtoSpill[vr];

        IR loadI = new IR(-1,"loadI",restoreLocation,-1,-1,-1,-1,-1,-1,-1,-1,-1,reserveReg,-1);
        IR load = new IR(-1,"load",-1,-1,reserveReg,-1,-1,-1,-1,-1,-1,vr,pr,-1);
        loadI.prev = current.prev;
        loadI.next = load;
        load.prev = loadI;
        load.next = current;
        current.prev.next = loadI;
        current.prev = load;

        VRtoSpill[vr] = -1;
    }

    
    
}  
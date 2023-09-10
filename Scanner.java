import java.util.*;
import java.io.*;

public class Scanner{
    BufferedReader reader;
    private String line;
    private int[][] transitionStates = new int[43][26];
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
        transitionStates[0][16]=34;
        transitionStates[34][17]=35;
        transitionStates[0][18]=39;
        transitionStates[39][18]=39;
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
            System.err.println("ERROR :");
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

             while((line = reader.readLine())!=null){
                word = "";
                currentState = 0;
                line+='\n';

                //System.out.println(line);
                for(char c : line.toCharArray()){
                    //check to see if we need to rollback
                    //System.out.println(c + " " + currentState);
                    
                    if(c == '\n' || c=='\t' || c==' ' || c==',' || (c=='/' && currentState != 0)){
                        
                        if(rollback == 39){
                            break;
                        }
                        if(rollback == 0){
                            continue;
                        }
                        
                        
                        if(transitionStates[rollback][23]==1){
                            
                            //identify category
                            curCat = -1;
                            if(rollback==5 || rollback ==11){
                                curCat = 0;
                            }
                            else if(rollback == 12 ){
                                curCat = 1;
                            }
                            else if(rollback==24 || rollback ==7 || rollback==18 ){
                                curCat = 2;
                            }
                            else if(rollback == 33 ){
                                curCat = 3;
                            }
                            else if( rollback == 42 ){
                                curCat = 4;
                            }
                            else if( rollback == 38 ){
                                //check if register or digit when returned
                                if(word.charAt(0)=='r'){
                                    curCat = 6;
                                }
                                else{
                                    curCat = 5;
                                }
                                
                            }
                            else if(rollback == 36){
                                curCat = 7;
                            }
                            else if( rollback == 35 ){
                                curCat = 8;
                            }
                            
                            if(curCat==-1){
                                System.err.println("ERROR "+scanLineNum+":");
                                break;
                            }
                            else{
                                lexeme.add(word);
                                category.add(curCat);
                                if(flag == 3){
                                    System.out.println(scanLineNum + ": <" + curCat+", \"" + word + "\" >");
                                }
                            }

                            
                            if(c == ','){
                                word = ""+c;
                                rollback = 36;
                                currentState = 0;
                                continue;
                            }
                            
                            
                            
                            word = "";
                            rollback = 0;
                            currentState = 0;
                            
                            
                        }
                        else{
                            //not an end state when there should be
                            System.err.println("ERROR "+scanLineNum+":");
                            break;
                        }
                    }
                    else{
                        // see if character can be mapped
                        if(charToInt.containsKey(c)){
                            transitionChar = charToInt.get(c);
                        }
                        else{
                            //character does not exist in map
                            System.err.println("ERROR "+scanLineNum+":");
                            break;
                        }
                        // see if the transition exists
                        if(transitionStates[currentState][transitionChar]>=0){
                            currentState = transitionStates[currentState][transitionChar];
                            rollback = currentState;
                            word+=c;
                        }
                        else if(transitionStates[rollback][23]==1){
                            
                            //identify category
                            curCat = -1;
                            if(rollback==5 || rollback ==11){
                                curCat = 0;
                            }
                            else if(rollback == 12 ){
                                curCat = 1;
                            }
                            else if(rollback==24 || rollback ==7 || rollback==18 ){
                                curCat = 2;
                            }
                            else if(rollback == 33 ){
                                curCat = 3;
                            }
                            else if( rollback == 42 ){
                                curCat = 4;
                            }
                            else if( rollback == 38 ){
                                //check if register or digit when returned
                                if(word.charAt(0)=='r'){
                                    curCat = 6;
                                }
                                else{
                                    curCat = 5;
                                }
                                
                            }
                            else if(rollback == 36){
                                curCat = 7;
                            }
                            else if( rollback == 35 ){
                                curCat = 8;
                            }
                            
                            if(curCat==-1){
                                System.err.println("ERROR "+scanLineNum+":");
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
  
                                System.err.println("ERROR "+scanLineNum+":");
                                
                            }

                            
                            
                            
                        }
                        else{
                            //character does not transistion into 
                            //check if end state
                            
                            System.err.println("ERROR "+scanLineNum+":");
                            
                        }
                    }
                    
                    

                }
                lexeme.add("\\n");
                category.add(10);
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
        } catch (Exception ex) {
        }
       
    }
    
    public boolean parser(){
        index = 0;
        int curCategory = category.get(index);
        createIR = true;
        int finishLine;
        

        while(curCategory!=9){
            if(curCategory==0){
                finishLine = finishMemop();
                if(finishLine == -1){
                    curCategory = category.get(index);
                    while(curCategory!=10){
                        index++;
                        curCategory = category.get(index);
                    }
                }
                lineNum++; 
            }
            else if(curCategory==1){
                finishLine = finishLoadI();
                if(finishLine == -1){
                    curCategory = category.get(index);
                    while(curCategory!=10){
                        index++;
                        curCategory = category.get(index);
                    }
                }
                lineNum++;
            }
            else if(curCategory==2){
                finishLine = finishArthop();
                if(finishLine == -1){
                    curCategory = category.get(index);
                    while(curCategory!=10){
                        index++;
                        curCategory = category.get(index);
                    }
                }
                lineNum++;
            }
            else if(curCategory==3){
                finishLine = finishOutput();
                if(finishLine == -1){
                    curCategory = category.get(index);
                    while(curCategory!=10){
                        index++;
                        curCategory = category.get(index);
                    }
                }
                lineNum++;
            }
            else if(curCategory==4){
                finishLine = finishNop();
                if(finishLine == -1){
                    curCategory = category.get(index);
                    while(curCategory!=10){
                        index++;
                        curCategory = category.get(index);
                    }
                }
                lineNum++;
            }
            else if(curCategory==10){
                lineNum++;
            }
            else{
                if(flag == 1 || flag ==2){
                    System.err.println("ERROR "+lineNum+":");
                }
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
            if(flag == 1 || flag ==2){
                System.err.println("ERROR "+lineNum+":");
            }
            return -1;
        }
        
        index++;
        curCategory = category.get(index);
        //INTO
        if(curCategory != 8){
            createIR = false;
            if(flag == 1 || flag ==2){
                System.err.println("ERROR "+lineNum+":");
            }
            return -1;
        }
            
        index++;
        curCategory = category.get(index);
        //REGISTER
        if(curCategory != 6){
            createIR = false;
            if(flag == 1 || flag ==2){
                System.err.println("ERROR "+lineNum+":");
            }
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
            if(flag == 1 || flag ==2){
                System.err.println("ERROR "+lineNum+":");
            }
            return -1;
        }
             
    }
    public int finishLoadI(){
        index++;
        curCategory = category.get(index);
        //CONSTANT
        if(curCategory != 5){
            createIR = false;
            if(flag == 1 || flag ==2){
                System.err.println("ERROR "+lineNum+":");
            }
            return -1;
        }
        
        index++;
        curCategory = category.get(index);
        //INTO
        if(curCategory != 8){
            createIR = false;
            if(flag == 1 || flag ==2){
                System.err.println("ERROR "+lineNum+":");
            }
            return -1;
        }
            
        index++;
        curCategory = category.get(index);
        //REGISTER
        if(curCategory != 6){
            createIR = false;
            if(flag == 1 || flag ==2){
                System.err.println("ERROR "+lineNum+":");
            }
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
            if(flag == 1 || flag ==2){
                System.err.println("ERROR "+lineNum+":");
            }
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
            if(flag == 1 || flag ==2){
                System.err.println("ERROR "+lineNum+":");
            }
            return -1;
        }
        
        index++;
        curCategory = category.get(index);
        //COMMA
        if(curCategory != 7){
            createIR = false;
            if(flag == 1 || flag ==2){
                System.err.println("ERROR "+lineNum+":");
            }
            return -1;
        }
            
        index++;
        curCategory = category.get(index);
        //REGISTER
        if(curCategory != 6){
            createIR = false;
            if(flag == 1 || flag ==2){
                System.err.println("ERROR "+lineNum+":");
            }
            return -1;
        }

        index++;
        curCategory = category.get(index);
        //INTO
        if(curCategory != 8){
            createIR = false;
            if(flag == 1 || flag ==2){
                System.err.println("ERROR "+lineNum+":");
            }
            return -1;
        }

        index++;
        curCategory = category.get(index);
        //REGISTER
        if(curCategory != 6){
            createIR = false;
            if(flag == 1 || flag ==2){
                System.err.println("ERROR "+lineNum+":");
            }
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
            if(flag == 1 || flag ==2){
                System.err.println("ERROR "+lineNum+":");
            }
            return -1;
        }
             
    }
    public int finishOutput(){
        index++;
        curCategory = category.get(index);
        //CONSTANT
        if(curCategory != 5){
            createIR = false;
            if(flag == 1 || flag ==2){
                System.err.println("ERROR "+lineNum+":");
            }
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
            if(flag == 1 || flag ==2){
                System.err.println("ERROR "+lineNum+":");
            }
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
            if(flag == 1 || flag ==2){
                System.err.println("ERROR "+lineNum+":");
            }
            return -1;
        }
             
    }
    
    
    
}
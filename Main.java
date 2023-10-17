import java.io.File;

public class Main{
    public static void main(String[] args){
        boolean hFlag = false;
        boolean xFlag = false;
        int totalFlag = 0;
        int k=0;
        boolean success;

        if(args.length == 0){
            System.err.println("COMP 412, Fall 2023 Lab 1 \nCommand Syntax:\n\t./412fe [flags] filename\n\nRequired arguments:\n\tfilename is the pathname (absolute or relative) to the input file\n\nOptional flags:\n\t-h       prints this message\n\nAt most one of the following three flags:\n\t-s       prints tokens in token stream\n\t-p       invokes parser and reports on success or failure\n\t-r       prints human readable version of parser's IR\nIf none is specified, the default action is '-p'.");
        }

        for(int i = 0; i<args.length; i++){
            if(args[i].equals("-h")){
                hFlag = true;
            }
            else if(args[i].equals("-x")){
                xFlag = true;
            }

        }
        //flags h=0, x=1 
        int flag=-1;
        int file=args.length-1;
        if(!xFlag){
            k = Integer.parseInt(args[0]);
        }
        
        
        if(totalFlag == 0){
            //-p flag
            flag = 2;
        }
        
        if(hFlag == true){
            System.err.println("COMP 412, Fall 2023 Lab 2 \nCommand Syntax:\n\t./412fe [flags] filename\n\nRequired arguments:\n\tfilename is the pathname (absolute or relative) to the input file\n\nOptional flags:\n\t-h       prints this message\n\nAt most one of the following three flags:\n\t-x       perform renaming\n\tk       allocate register an integer between 3 and 64");
        }
        else if(xFlag == true){
            flag = 1;
        }
        else if(k <3 || k>64){
            System.err.println("COMP 412, Fall 2023 Lab 2 \nCommand Syntax:\n\t./412fe [flags] filename\n\nRequired arguments:\n\tfilename is the pathname (absolute or relative) to the input file\n\nOptional flags:\n\t-h       prints this message\n\nAt most one of the following three flags:\n\t-x       perform renaming\n\tk       allocate register an integer between 3 and 64");
        }
        if(totalFlag>1){
            System.err.println("COMP 412, Fall 2023 Lab 2 \nCommand Syntax:\n\t./412fe [flags] filename\n\nRequired arguments:\n\tfilename is the pathname (absolute or relative) to the input file\n\nOptional flags:\n\t-h       prints this message\n\nAt most one of the following three flags:\n\t-x       perform renaming\n\t-k       allocate register");

        }
        if(flag>=1){
            Scanner scan = new Scanner(flag);
            scan.maxPR = k;
            File f = new File(args[file]);
            scan.readFile(f);
            //System.out.println("scanning");

            scan.scanFile();
            success = scan.parser();
            if(success){
                scan.renaming();
                if(!xFlag){
                    scan.alloc();
                    IR currentIR = scan.head;
                    String output;
                    while(currentIR != null){
                    output="";
                    output+=currentIR.opcode;
                    if(currentIR.opcode.equals("output")){
                        output+=" "+Integer.toString(currentIR.op1SR);
                    }
                    else if(currentIR.opcode.equals("nop")){
                        
                    }
                    else{
                        if(currentIR.opcode.equals("loadI") && currentIR.op1SR!=-1){
                            output+=" "+Integer.toString(currentIR.op1SR);
                        }
                        else if(currentIR.op1PR!=-1){
                            output+=" r"+Integer.toString(currentIR.op1PR);
                        }
                        if(currentIR.op2PR!=-1){
                            output+=", r"+Integer.toString(currentIR.op2PR);
                        }
                        output+=" =>";
                        if(currentIR.op3PR!=-1){
                            output+=" r"+Integer.toString(currentIR.op3PR);
                        }
                        
                    }
                    System.out.println(output);
                    currentIR = currentIR.next;
                    
                    }
                } else {
                    IR currentIR = scan.head;
                    String output;
                    while(currentIR != null){
                    output="";
                    output+=currentIR.opcode;
                    if(currentIR.opcode.equals("output")){
                        output+=" "+Integer.toString(currentIR.op1SR);
                    }
                    else if(currentIR.opcode.equals("nop")){
                        
                    }
                    else{
                        if(currentIR.opcode.equals("loadI") && currentIR.op1SR!=-1){
                            output+=" "+Integer.toString(currentIR.op1SR);
                        }
                        else if(currentIR.op1VR!=-1){
                            output+=" r"+Integer.toString(currentIR.op1VR);
                        }
                        if(currentIR.op2VR!=-1){
                            output+=", r"+Integer.toString(currentIR.op2VR);
                        }
                        output+=" =>";
                        if(currentIR.op3VR!=-1){
                            output+=" r"+Integer.toString(currentIR.op3VR);
                        }
                        
                    }
                    System.out.println(output);
                    currentIR = currentIR.next;
                    
                    }
                }
                
            }


        }

        
    }
}



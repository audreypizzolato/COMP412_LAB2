import java.io.File;

public class Main{
    public static void main(String[] args){
        System.out.println("Running");
        boolean hFlag = false;
        boolean rFlag = false;
        boolean pFlag = false;
        boolean sFlag = false;
        int totalFlag = 0;
        boolean success;

        if(args.length == 0){
            System.err.println("COMP 412, Fall 2023 Lab 1 \nCommand Syntax:\n\t./412fe [flags] filename\n\nRequired arguments:\n\tfilename is the pathname (absolute or relative) to the input file\n\nOptional flags:\n\t-h       prints this message\n\nAt most one of the following three flags:\n\t-s       prints tokens in token stream\n\t-p       invokes parser and reports on success or failure\n\t-r       prints human readable version of parser's IR\nIf none is specified, the default action is '-p'.");
        }

        for(int i = 0; i<args.length; i++){
            if(args[i].equals("-h")){
                hFlag = true;
                totalFlag++;
            }
            else if(args[i].equals("-r")){
                rFlag = true;
                totalFlag++;
            }
            else if(args[i].equals("-p")){
                pFlag = true;
                totalFlag++;
            }
            else if(args[i].equals("-s")){
                sFlag = true;
                totalFlag++;
            }

        }
        //flags h=0, r=1, p=2, s=3
        int flag=-1;
        int file=args.length-1;
        
        if(totalFlag == 0){
            //-p flag
            flag = 2;
        }
        
        if(hFlag == true){
            System.err.println("COMP 412, Fall 2023 Lab 1 \nCommand Syntax:\n\t./412fe [flags] filename\n\nRequired arguments:\n\tfilename is the pathname (absolute or relative) to the input file\n\nOptional flags:\n\t-h       prints this message\n\nAt most one of the following three flags:\n\t-s       prints tokens in token stream\n\t-p       invokes parser and reports on success or failure\n\t-r       prints human readable version of parser's IR\nIf none is specified, the default action is '-p'.");
        }
        else if(rFlag == true){
            flag = 1;
        }
        else if(pFlag == true){
            flag = 2;
        }
        else if(sFlag == true){
            flag = 3;
        }
        
        if(flag>=1){
            Scanner scan = new Scanner(flag);
            File f = new File(args[file]);
            scan.readFile(f);
            System.out.println("scanning");
            scan.scanFile();
            for(int i=0; i<scan.lexeme.size(); i++){
                System.out.println("\""+scan.lexeme.get(i)+"\"\t"+scan.category.get(i));
            }
            if(flag==1 || flag==2){
                System.out.println("parsing");
                success = scan.parser();
                if(flag==2 && success){
                    System.out.println("Parse Succeeded, finding " + "ILOC expressions");
                }
            }

            
        }
        
        
        
        
    }
}



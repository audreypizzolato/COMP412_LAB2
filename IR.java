public class IR {
    int line;
    String opcode;
    int op1SR;
    int op1VR;
    int op1PR;
    int op1NU;
    int op2SR;
    int op2VR;
    int op2PR;
    int op2NU;
    int op3SR;
    int op3VR;
    int op3PR;
    int op3NU;
    IR next;
    IR prev;
    public IR(int line, String opcode, int op1SR, int op1VR, int op1PR, int op1NU, int op2SR, int op2VR, 
    int op2PR, int op2NU, int op3SR, int op3VR, int op3PR, int op3NU){
        this.line = line;
        this.opcode = opcode;
        this.op1SR = op1SR;
        this.op1VR = op1VR;
        this.op1PR = op1PR;
        this.op1NU = op1NU;
        this.op2SR = op2SR;
        this.op2VR = op2VR;
        this.op2PR = op2PR;
        this.op2NU = op2NU;
        this.op3SR = op3SR;
        this.op3VR = op3VR;
        this.op3PR = op3PR;
        this.op3NU = op3NU;
    }
    public void setNext(IR next){
        this.next = next;
    }
    public IR getNext(){
        return next;
    }
    public void setPrev(IR prev){
        this.prev = prev;
    }
    public IR getPrev(){
        return prev;
    }
    public String toString(){
        if(opcode.equals("nop")){
            return "nop\t\t[], [], []";
        }
        else if(opcode.equals("output")){
            return "output\t[val "+op1SR+"], [], []";
        }
        else if(opcode.equals("loadI")){
            return opcode+"\t[val "+op1SR+"], [], [sr "+op3SR+"]";
        }
        else if( opcode.equals("store")||opcode.equals("load")){
            return opcode+"\t[sr "+op1SR+"], [], [sr "+op3SR+"]";
        }
        else{
            return opcode+"\t[sr "+op1SR+"], [sr "+op2SR+"], [sr "+op3SR+"]"; 
        }

    }
    
}



import java.util.Scanner;
import java.util.Random;
import java.lang.Runnable;
import java.util.Arrays;


/*
 *  Instructions:
 *
 *  asd to move the block.
 *  w to move the block to the bottom.
 *  l and ; to rotate clockwise and counterclockwise respectively.
 *
 *  Press Enter after every input.
 *  If multiple inputs are received, multiple actions will be performed at once.
 *  The l and ; keys are next to the Enter key, so you can use the left hand for inputting
 *      movement and the right for rotation and pressing enter after every input.
 *
 *  I've also yet to make a Game Over screen when the blocks reach the top.
 *
 */

/*
 *  Make a copy array at the beginning, compare at the end and run special, instead of clear all.
 *  Once you clear 10, change colors, and maybe increase speed?
 */

class Auto implements Runnable {
    private volatile boolean alive = true;
    public void terminate() {
        this.alive = false;
    }
    @Override
    public void run() {
        while(alive) {
            // move block down by 1 every 1 seconds, synchronize threads with others.
            try{Thread.sleep(1000);}catch(InterruptedException e){System.out.println(e);}
            synchronized (this) {
                Main.inputAnalysis("s"); // Monitor -- block off.
            }
        }
    }
}

class Observer implements Runnable {
    private volatile boolean alive = true;
    public void terminate() {
        this.alive = false;
    }
    @Override
    public void run() {
        while(alive) {
            if (Main.countsFailed > 5) {
                Main.terminator = true;
                break;
            }
        }
    }
}


public class Main implements Runnable {

    private volatile boolean alive = true;
    public void terminate() {
        this.alive = false;
    }
    @Override
    public void run() {
        while(alive) {
            Scanner sc = new Scanner(System.in);
            String str = sc.next();
            // sync prevents the other thread from running when this one is.
            synchronized(this) {
                inputAnalysis(str); // receives key inputs.
            }
        }
    }
    public static void inputAnalysis(String str) {
        // split input string by the character and pass through all chars individually into inputProcess.
        for (char ch: str.toCharArray()) {
            if (debugging) {System.out.println("split array: "+ch);}
            inputProcess(ch);
        }
    }
    public static void inputProcess(char ch) { // processes individual inputs from inputAnalysis.
        boolean overlap = false;
        switch(ch) {
            case 'w':
                copyArrays(1);  // cObject[][] = mObject[][];
                while (!overlap) {
                    copyArrays(3);  // cObjCoord[] = mObjCoord[];
                    cObjCoord[1] += -1;
                    overlap = blockOverlap();
                    if (overlap) {
                        blockImplementation();
                        fullArrayPrint();
                        concludeBlock();
                    } else {
                        copyArrays(0);  // mArray[][] = constArray[][];
                        copyArrays(4);  // mObjCoord[] = cObjCoord[];
                        lineClearer();
                    }
                }
                break;
            case 'a':
                copyArrays(1);  // cObject[][] = mObject[][];
                copyArrays(3);  // cObjCoord[] = mObjCoord[];
                cObjCoord[0] += -1;
                if (debugging) {System.out.println("updated xCoord(test): "+cObjCoord[0]);}
                overlap = blockOverlap();   // analyzes on constArray[][]
                if (!overlap) {
                    copyArrays(0);  // mArray[][] = constArray[][];
                    copyArrays(2);  // mObject[][] = cObject[][];
                    copyArrays(4);  // mObjCoord[] = cObjCoord[];
                    blockImplementation();  // implements mObject[][] into mArray[][].
                }
                lineClearer();
                fullArrayPrint();   // prints mArray[][]
                break;
            case 's':
                copyArrays(3);  // cObjCoord[] = mObjCoord[];
                cObjCoord[1] += -1;
                if (debugging) {System.out.println("updated yCoord(test): "+cObjCoord[1]);}
                overlap = downTranslationC();
                if (overlap) {
                    concludeBlock();
                } else {
                    copyArrays(0);  // mArray[][] = constArray[][];
                    copyArrays(4);  // mObjCoord[] = cObjCoord[];
                    blockImplementation();
                    lineClearer();
                    fullArrayPrint();
                    countsFailed = 0;
                }
                break;
            case 'd':
                copyArrays(1);  // cObject[][] = mObject[][];
                copyArrays(3);  // cObjCoord[] = mObjCoord[];
                cObjCoord[0] += 1;
                if (debugging) {System.out.println("updated xCoord(test): "+cObjCoord[0]);}
                overlap = blockOverlap();
                if (!overlap) {
                    copyArrays(0);  // mArray[][] = constArray[][];
                    copyArrays(2);  // mObject[][] = cObject[][];
                    copyArrays(4);  // mObjCoord[] = cObjCoord[];
                    blockImplementation();  // implements mObject[][] into mArray[][].
                }
                lineClearer();
                fullArrayPrint();   // prints mArray[][].
                break;
            case 'l':
                if (fetchBlockNum!=2) {
                    copyArrays(1);  // cObject[][] = mObject[][];
                    copyArrays(3);  // cObjCoord[] = mObjCoord[];
                    boolean checkPassed = rotationTests(true);//true==clockwise
                    if (checkPassed) {
                        copyArrays(0);  // mArray[][] = constArray[][];
                        copyArrays(2);  // mObject[][] = cObject[][];
                        copyArrays(4);  // mObjCoord[] = cObjCoord[];
                        blockImplementation();
                    }
                    lineClearer();
                }
                fullArrayPrint();
                break;
            case ';':
                if (fetchBlockNum!=2) {
                    copyArrays(1);  // cObject[][] = mObject[][];
                    copyArrays(3);  // cObjCoord[] = mObjCoord[];
                    boolean checkPassed = rotationTests(false);//false==counterclockwise
                    if (checkPassed) {
                        copyArrays(0);  // mArray[][] = constArray[][];
                        copyArrays(2);  // mObject[][] = cObject[][];
                        copyArrays(4);  // mObjCoord[] = cObjCoord[];
                        blockImplementation();
                    }
                    lineClearer();
                }
                fullArrayPrint();
                break;
            case 'b':
                linesCleared += 5;
                fullArrayPrint();
                break;
            default:
                fullArrayPrint();
        }
    }

    public static int columns = 16+1; // plus one to make up for 0th place in array.
    public static int height = 20+1; // 20 blocks high is standard visible stage height.
    public static int heightEx = 4; // there are 4 extra spaces at the top of stage, hidden.

    public static int blockSize = 3; // changes; identifies if block is 4x4 or 3x3.
    public static int fetchBlockNum; // changes; identifies the current block type.

    public static boolean debugging = false;

    public static boolean terminator = false;
    public static int countsFailed = 0;

    public static int linesCleared = 0;

    public static String borderCharV = " | ";
    public static String borderCharH = "---";
    public static String nullChar = " . ";
    public static String blockChar = " O ";

    // array that stores upcoming block arrays, so can display and fetch next block from there.
    public static String[][][] buff = new String[4+1][4+1][4];

    // array that stores the stage.
    public static String[][] constArray = new String[columns][height+heightEx];

    // array that stores a copy of constArray, which is manipulated.
    public static String[][] mArray = new String[columns][height+heightEx];

    // array that stores currently implemented block state.
    public static String[][] mObject = new String[4+1][4+1];

    // array that stores a copy of mObject that is manipulated.
    public static String[][] cObject = new String[4+1][4+1];

    //  For example, an input to rotate is received. First, cObject receives a
    // copy of mObject, and the resulting clone of the block (cObject) will be
    // manipulated. This allows for the initial state of the transformation
    // (mObject) to be kept, in the case that rotationTests fail and a rotation
    // was unable to be implemented.
    //  Rotations are applied to cObject and if rotationTests resulted in a pass,
    // cObject will write over mObject, and will then be implemented into mArray,
    // a copy of the stage, which is then printed.
    //  Lets say that another rotation was applied, so mArray will first receive
    // a copy of constArray. This will clear the previously implemented block
    // in mArray, so that the previous and next blocks aren't both printed
    // in the printing of the next transformation.
    //  Next, cObject will receive a copy of mObject, which stores the current
    // state of the block. If rotationTests fail, no action is taken, and the
    // initial block orientation (stored in mObject) will simply be pasted back
    // into mArray and then printed again. cObject can simply be left alone,
    // since that will be overwritten in the next transformation with mObject.
    // cObject simply acts as a way to test different rotational states, to see
    // if any can apply. If one can, it will simply overwrite mObject.


    // the initial Object coordinates.
    public static int[] mObjCoord = new int[2];

    // cObjCorrd is a copy of mObjCoord, which acts very much like cObject from earlier.
    // it acts as a way to test if horizontal and vertical translations can be applied
    // to a block. If yes, will overwrite mObjCoord.
    public static int[] cObjCoord = new int[2];

    // identifies the current orientation (1=0deg/vertical, 2=90deg, 3=180deg, 4=270deg)
    public static int rotationState = 1;



    public static void main(String[] args) {


        System.out.println("" +
                "\n\n\tTetris" +
                "\n\n\tHow to Play:" +
                "\n\n\tasd to move the block.\n" +
                "\tw to move the block to the bottom.\n" +
                "\tl and ; to rotate clockwise and counterclockwise respectively.\n" +
                "\tPress Enter after every input.\n" +
                "\n\tIf multiple inputs are received, multiple actions will be performed at once.\n" +
                "\tThe l and ; keys are next to the Enter key, so you can use the left hand for inputting\n" +
                "\tmovement and the right for rotation and pressing enter after every input.\n" +
                "\n\tEnter any key to begin");

        Scanner sc = new Scanner(System.in);
        sc.next();


        initConstArray();
        initBuffArray();
        blockCreation(true); // to supplement the one in newBlockSequence when at startup.
        newBlockSequence(true);

        Main runner = new Main();
        Thread read = new Thread(runner);
        read.start();

        Auto shifter = new Auto();
        Thread sh = new Thread(shifter);
        sh.start();

        Observer obs = new Observer();
        Thread o = new Thread(obs);
        o.start();


        while (true) {
            if (terminator) {
                runner.terminate();
                shifter.terminate();
                obs.terminate();
                break;
            }
            sleeper(100);
        }
        clearScreen();
        sleeper(1500);
        System.out.println("\n\n\n\n\t\t\t\tGame Over\n\n\n\n");
    }

    public static void newBlockSequence(boolean initial) { // initial actions at startup.
        lineClearer();
        if (!initial) {
            shiftBuffDown();
        }
        blockCreation(false);
        extendedUI();
        fetchObjectFromBuff();
        blockSpawnLocation();
        copyArrays(0);  // mArray[][] = constArray[][];
        blockImplementation();
        fullArrayPrint();
    }

    public static void initConstArray() { // initializes constArray[][];
        for (int y = 1; y < height+heightEx; y++)
            for (int x = 1; x < 12; x++)
                constArray[x][y] = nullChar;

        for (int i = 0; i < height+heightEx; i++)
            constArray[0][i] = borderCharV;

        for (int i = 0; i < height+heightEx; i++)
            constArray[11][i] = borderCharV;

        for (int i = 1; i < 11; i++)
            constArray[i][0] = borderCharH;
    }
    public static void initBuffArray() {
        for (int z = 0; z <= 3; z++) {
            for (int y = 0; y <= 4; y++) {
                for (int x = 0; x <= 4; x++) {
                    buff[x][y][z] = " . ";
                }
            }
        }
    }
    public static void extendedUI() {
        for (int i = 0; i <= Main.height + Main.heightEx - 1; i++) {
            Main.constArray[12][i] = "uwu | ";
        }
        for (int z = 13; z <= 15; z += 2) {
            for (int i = 0; i <= Main.height - 1; i++) {
                Main.constArray[z][i] = "###";
            }
        }
        Main.constArray[14][19] = " - Points: " + Main.linesCleared + " - "; // extends too far.
        if (linesCleared < 10) {
            Main.constArray[14][19] += "#";
        }
        Main.constArray[14][17] = "#### Next: #####";
        for (int i = 16; i <= 20; i += 2)
            Main.constArray[14][i] = "################";

        for (int i = 15; i >= 0; i--)
            Main.constArray[14][i] = "";
        for (int i = 15; i >= 12; i--)
            for (int x = 1; x <= 4; x++)
                Main.constArray[14][i] += buff[x][i-11][1];
        for (int i = 10; i >= 7; i--)
            for (int x = 1; x <= 4; x++)
                Main.constArray[14][i] += buff[x][i-6][2];
        for (int i = 5; i >= 2; i--)
            for (int x = 1; x <= 4; x++)
                Main.constArray[14][i] += buff[x][i-1][3];
        for (int i = 16 ; i >= 0; i--) {
            Main.constArray[13][i] += "##";
            Main.constArray[15][i] += "##";
        }
        for (int i = 1; i < 17; i+=5)
            Main.constArray[14][i] = "############";
        Main.constArray[14][0] = "############";

        for (int i = 0; i <= Main.height - 1; i++) {
            Main.constArray[16][i] = " |";
        }
    }

    public static void fullArrayPrint() { // prints the full stage (mArray).
        colorToggle(true);
        int h;
        if (debugging) { // prints the hidden space up top as well if true.
            h = height+heightEx-1;
        } else {
            h = height-1;
        }
        clearScreen();
        System.out.print("\n\n");
        for (int y = h; y >= 0; y--) {
            for (int x = 0; x < 17; x++) {
                System.out.print(mArray[x][y]);
            }
            System.out.print("\n");
        }
        //colorToggle(false);
    }

    public static String[][] fetchBlock(int ran) {
        String[][] objectArray = new String[4+1][4+1];
        for (int y = 0; y <= 4; y++) {
            for (int x = 0; x <= 4; x++) {
                objectArray[x][y] = " / ";  // change to something simpler? remove and leave as null?
            }
        }

        switch(ran) {
            case 1:
                for (int i = 1; i <= 4; i++)
                    objectArray[i][3] = Main.blockChar;
                break;
            case 2:
                for (int i = 3; i <= 4; i++)
                    for (int j = 2; j <= 3; j++)
                        objectArray[j][i] = Main.blockChar;
                break;
            case 3:
                for (int x = 1; x <= 3; x++) {
                    objectArray[x][2] = Main.blockChar;
                }
                objectArray[2][3] = Main.blockChar;
                break;
            case 4:
                for (int x = 1; x <= 3; x++) {
                    objectArray[x][2] = Main.blockChar;
                }
                objectArray[1][3] = Main.blockChar;
                break;
            case 5:
                for (int x = 1; x <= 3; x++) {
                    objectArray[x][2] = Main.blockChar;
                }
                objectArray[3][3] = Main.blockChar;
                break;
            case 6:
                for (int x = 1; x <= 2; x++) {
                    objectArray[x][2] = Main.blockChar;
                }
                for (int x = 2; x <= 3; x++) {
                    objectArray[x][3] = Main.blockChar;
                }
                break;
            case 7:
                for (int x = 2; x <= 3; x++) {
                    objectArray[x][2] = Main.blockChar;
                }
                for (int x = 1; x <= 2; x++) {
                    objectArray[x][3] = Main.blockChar;
                }
                break;
        }

        return objectArray;
    }
    public static void blockCreation(boolean sp) { // creates a new block at random.
        int offset = 3;
        if (sp)
            offset = 0;

        for (int z = offset%4; z < 3+(offset/2); z++) {
            int fetchNum = newRandom(7);

            if ((fetchNum == 1) || (fetchNum == 2)) {
                blockSize = 4;
            } else {
                blockSize = 3;
            } // vvv fetches new block from here. vvv
            mObject = fetchBlock(fetchNum);

            for (int x = 0; x <= 4; x++) {
                for (int y = 0; y <= 4; y++) {
                    buff[x][y][z] = mObject[x][y];
                }
            }
        }
    }
    public static void shiftBuffDown() {
        for (int t = 0; t <= 2; t++) {
            for (int y = 0; y <= 4; y++) {
                for (int x = 0; x <= 4; x++) {
                    buff[x][y][t] = buff[x][y][t+1];
                }
            }
        }
    }
    public static void fetchObjectFromBuff() { //when summoning new block.
        for (int y = 0; y <= 4; y++) {
            for (int x = 0; x <= 4; x++) {
                mObject[x][y] = buff[x][y][0];
            }
        }
        for (int i = 1; i <= 7; i++) {
            if (Arrays.deepEquals(mObject, fetchBlock(i))) {
                fetchBlockNum = i;
                i = 8;
            }
        }
        if ((fetchBlockNum == 1) || (fetchBlockNum == 2)) {
            blockSize = 4;
        } else {
            blockSize = 3;
        }
    }
    public static void blockSpawnLocation() { // randomly determines block spawn location.
        int xloc = newRandom(11-blockSize);
        if (debugging) {System.out.println("xloc: "+xloc);}
        mObjCoord[0] = xloc;
        mObjCoord[1] = 24;
        if (debugging) {System.out.println("xCoord: "+mObjCoord[0]+"  yCoord: "+mObjCoord[1]);}

    }

    public static void copyArrays(int type) { // creates copies of certain arrays.
        if (type==0) {  // copy mArray = constArray.
            for (int x = 0; x <= columns-1; x++)
                for (int y = 0; y <= height+heightEx-1; y++)
                    mArray[x][y] = constArray[x][y];
        }
        if (type==1) {
            for (int x = 0; x <= 4; x++)
                for (int y = 0; y <= 4; y++)
                    cObject[x][y] = mObject[x][y];
        }
        if (type==2) {
            for (int x = 0; x <= 4; x++)
                for (int y = 0; y <= 4; y++)
                    mObject[x][y] = cObject[x][y];
        }
        if (type==3) {
            for (int i = 0; i <= 1; i++)
                cObjCoord[i] = mObjCoord[i];
        }
        if (type==4) {
            for (int i = 0; i <= 1; i++)
                mObjCoord[i] = cObjCoord[i];
        }
        if (type==5) {
        }
    }
    public static boolean downTranslationC() { // checks for overlap in downward translations.
        boolean overlap = false;
        for (int y = 4; y >= 1; y--)
            for (int x = 1; x <= 4; x++)
                if (mObject[x][y].equals(blockChar))
                    if ((constArray[cObjCoord[0] + (x - 1)][(cObjCoord[1] - heightEx) + y].equals(blockChar)) || (constArray[cObjCoord[0] + (x - 1)][(cObjCoord[1] - heightEx) + y].equals(borderCharH))) {
                        overlap = true;
                    }
        return overlap;
    }
    // runs rotationTests.java and returns updated block to cObject.
    // then, chooses whether to return boolean checkPassed true/false.
    public static boolean rotationTests(boolean clockwise) {
        boolean checkPassed = false;
        System.arraycopy(RotationTests.main(mObject, cObject, clockwise), 0, cObject, 0, 5);
        if (!Arrays.deepEquals(cObject, mObject)) {
            System.out.println("output blocks are different");
            updateRotationState(clockwise);
            checkPassed = true;
        }
        return checkPassed;
    }
    // checks if the block will overlap in a given state and position.
    public static boolean blockOverlap() {
        boolean overlap = false;
        for (int y = 4; y >= 1; y--)
            for (int x = 1; x <= 4; x++)
                if (cObject[x][y].equals(blockChar))
                    if (!constArray[cObjCoord[0] + (x - 1)][(cObjCoord[1] - heightEx) + y].equals(nullChar)) {
                        System.out.println("block overlaps!!");
                        overlap = true;
                    }
        return overlap;
    }
    public static void blockImplementation() { // implements mObject into mArray at coordinates.
        for (int y = 4; y >= 1; y--)
            for (int x = 1; x <= 4; x++)
                if (!mObject[x][y].equals(" / ")) {
                    mArray[mObjCoord[0]+(x-1)][(mObjCoord[1]-heightEx)+y] = mObject[x][y];
                }
    }
    public static void updateRotationState(boolean clockwise) { // updates the orientation.
        if (clockwise)
            rotationState += 1;
        else //"counterclockwise"
            rotationState -= 1;

        if (rotationState==5)
            rotationState = 1;
        if (rotationState==0)
            rotationState = 4;
    }

    // method below implements the current block into constArray, which is basically the
    public static void concludeBlock() { // stage. The block becomes part of the stage. It
        for (int y = 4; y >= 0; y--)     // then generates a new block. As a result, it
            for (int x = 0; x <= 4; x++) // changes "focus" from the old to the new block.
                if (!mObject[x][y].equals(" / ")) {
                    constArray[mObjCoord[0]+(x-1)][(mObjCoord[1]-heightEx)+y] = mObject[x][y];
                }
        countsFailed += 1;
        newBlockSequence(false);
    }

    public static void lineClearer() { // clears a completed line.
        boolean shouldClear = true;
        for (int y = height+heightEx-1; y >= 1; y--) {
            shouldClear = true;
            for (int x = 1; x <= 10; x++) {
                if (!constArray[x][y].equals(blockChar)) {
                    shouldClear = false;
                    break;
                }
            }
            if (shouldClear) {
                System.out.println("clearing line...");
                linesCleared++;
                shiftLinesDown(y);  // shifts all lines above down.
            }
        }
    }
    public static void shiftLinesDown(int yLocation) {  // shifts all lines above down.
        for (int x = 1; x <= 10; x++) {
            constArray[x][yLocation] = nullChar;
        }
        for (int y = yLocation+1; y <= height+heightEx-1; y++) {
            for (int x = 1; x <= 10; x++) {
                constArray[x][y-1] = constArray[x][y];
            }
        }
    }

    public static void colorToggle(boolean on) {
        if (on) {

            String colorA = "";
            String colorB = "";

            switch ((linesCleared/5)%7) {
                case 0:
                    colorB = "0";
                    colorA = "7";
                    break;
                case 1:// lucky
                    colorB = "4";
                    colorA = "3";
                    break;
                case 2:// magenta
                    colorB = "5";
                    colorA = "7";
                    break;
                case 3://very cool red
                    colorB = "0";
                    colorA = "1";
                    break;
                case 4:
                    colorB = "0";
                    colorA = "4";
                    break;
                case 5:
                    colorB = "0";
                    colorA = "2";
                    break;
                case 6:
                    colorB = "0";
                    colorA = "5";
            }

            System.out.print("\u001b[3"+colorA+";1m");
            System.out.print("\u001b[4"+colorB+";1m");


        } else {
            System.out.print("\u001b[0m");
        }
    }









    public static int newRandom(int max) { // returns a random number from 1 to "max".
        Random random = new Random();
        return (random.nextInt(max))+1;
    }
    public static void sleeper(int t) { // pauses the terminal for "t" milliseconds.
        try{Thread.sleep(t);}catch(InterruptedException e){e.printStackTrace();}
    }
    public static void clearScreen() { // clears the terminal
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
    public static void testPrintt() {
        System.out.println("begin test print");
        for (int z = 0; z <= 3; z++) {
            for (int y = 4; y >= 0; y--) {
                for (int x = 0; x <= 4; x++) {
                    System.out.print(buff[x][y][z]);
                }
                System.out.print("\n");
            }
            System.out.print("\n");
        }
        System.out.println("end test print");
    }



}



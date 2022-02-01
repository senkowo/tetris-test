

public class RotationTests {

    public static String[][] main(String[][] mObject, String[][] cObject, boolean clockwise) {
        boolean overlap = false;
        if (Main.blockSize==4) {
            for (int t = 1; t <= 5; t++) {
                System.arraycopy(Main.mObjCoord, 0, Main.cObjCoord, 0, 2);
                cObject = arrayCopier(mObject);
                cObject = simpleRotation(mObject, cObject, clockwise);
                switch (t) {
                    case 1:
                        overlap = blockOverlap(overlap, cObject);
                        if (!overlap) {
                            t += 4;
                        }
                        break;
                    case 2:
                        secondTest4(clockwise);
                        overlap = blockOverlap(overlap, cObject);
                        if (!overlap) {
                            t += 3;
                        }
                        break;
                    case 3:
                        thirdTest4(clockwise);
                        overlap = blockOverlap(overlap, cObject);
                        if (!overlap) {
                            t += 2;
                        }
                        break;
                    case 4:
                        secondTest4(clockwise);
                        fourthTest4(clockwise, true);
                        overlap = blockOverlap(overlap, cObject);
                        if (!overlap) {
                            t += 1;
                        }
                        break;
                    case 5:
                        thirdTest4(clockwise);
                        fourthTest4(clockwise, false);
                        overlap = blockOverlap(overlap, cObject);
                        if (overlap) {
                            cObject = arrayCopier(mObject);
                            System.arraycopy(Main.mObjCoord, 0, Main.cObjCoord, 0, 2);
                        }
                        break;
                }


            }

        } else { // blockSize==3
            for (int t = 1; t <= 5; t++) {
                System.arraycopy(Main.mObjCoord, 0, Main.cObjCoord, 0, 2);
                cObject = arrayCopier(mObject);
                cObject = simpleRotation(mObject, cObject, clockwise);
                switch (t) {
                    case 1:
                        overlap = blockOverlap(overlap, cObject);
                        if (!overlap) {
                            t += 4;
                        }
                        break;
                    case 2:
                        secondTest3(clockwise);
                        overlap = blockOverlap(overlap, cObject);
                        if (!overlap) {
                            t += 3;
                        }
                        break;
                    case 3:
                        secondTest3(clockwise);
                        thirdTest3();
                        overlap = blockOverlap(overlap, cObject);
                        if (!overlap) {
                            t += 2;
                        }
                        break;
                    case 4:
                        fourthTest3();
                        overlap = blockOverlap(overlap, cObject);
                        if (!overlap) {
                            t += 1;
                        }
                        break;
                    case 5:
                        fourthTest3();
                        secondTest3(clockwise);
                        overlap = blockOverlap(overlap, cObject);
                        if (overlap) {
                            cObject = arrayCopier(mObject);
                            System.arraycopy(Main.mObjCoord, 0, Main.cObjCoord, 0, 2);
                        }
                        break;
                }
            }
        }
        return cObject;
    }

    public static String[][] simpleRotation(String[][] mObject, String[][] cObject, boolean clockwise) {
        if (Main.blockSize==4){
            if (clockwise) {
                for (int i = 1; i <= 4; i++) {
                    cObject[1][i] = mObject[i][4];
                    cObject[2][i] = mObject[i][3];
                    cObject[3][i] = mObject[i][2];
                    cObject[4][i] = mObject[i][1];
                }
            } else { //(counterclockwise)
                for (int i = 1; i <= 4; i++) {
                    cObject[i][4] = mObject[1][i];
                    cObject[i][3] = mObject[2][i];
                    cObject[i][2] = mObject[3][i];
                    cObject[i][1] = mObject[4][i];
                }
            }
        } else {// blockSize==3
            if (clockwise) {
                for (int i = 1; i <= 3; i++) {
                    cObject[1][i] = mObject[4-i][1];
                    cObject[2][i] = mObject[4-i][2];
                    cObject[3][i] = mObject[4-i][3];
                }
            } else { //(counterclockwise)
                for (int i = 1; i <= 3; i++) {
                    cObject[4-i][1] = mObject[1][i];
                    cObject[4-i][2] = mObject[2][i];
                    cObject[4-i][3] = mObject[3][i];
                }
            }
        }
        return cObject;
    }

    public static void secondTest4(boolean clockwise) {
        if (clockwise){
            switch (Main.rotationState) {
                case 1:
                    Main.cObjCoord[0] -= 2;
                    break;
                case 2:
                    Main.cObjCoord[0] -= 1;
                    break;
                case 3:
                    Main.cObjCoord[0] += 2;
                    break;
                case 4:
                    Main.cObjCoord[0] += 1;
            }
        } else {// counterclockwise
            switch (Main.rotationState) {
                case 1:
                    Main.cObjCoord[0] -= 1;
                    break;
                case 2:
                    Main.cObjCoord[0] += 2;
                    break;
                case 3:
                    Main.cObjCoord[0] += 1;
                    break;
                case 4:
                    Main.cObjCoord[0] -= 2;
            }
        }
    }
    public static void thirdTest4(boolean clockwise) {
        if (clockwise){
            switch (Main.rotationState) {
                case 1:
                    Main.cObjCoord[0] += 1;
                    break;
                case 2:
                    Main.cObjCoord[0] += 2;
                    break;
                case 3:
                    Main.cObjCoord[0] -= 1;
                    break;
                case 4:
                    Main.cObjCoord[0] -= 2;
            }
        } else {// counterclockwise
            switch (Main.rotationState) {
                case 1:
                    Main.cObjCoord[0] += 2;
                    break;
                case 2:
                    Main.cObjCoord[0] -= 1;
                    break;
                case 3:
                    Main.cObjCoord[0] -= 2;
                    break;
                case 4:
                    Main.cObjCoord[0] += 1;
            }
        }
    }
    public static void fourthTest4(boolean clockwise, boolean isFourthCheck) {
        if (!isFourthCheck) {
            clockwise = !clockwise;
        }

        if (clockwise) {
            switch (Main.rotationState) {
                case 1:
                    Main.cObjCoord[1] -= 1;
                    break;
                case 2:
                    Main.cObjCoord[1] += 2;
                    break;
                case 3:
                    Main.cObjCoord[1] += 1;
                    break;
                case 4:
                    Main.cObjCoord[1] -= 2;
            }
        } else {// counterclockwise
            switch (Main.rotationState) {
                case 1:
                    Main.cObjCoord[1] += 2;
                    break;
                case 2:
                    Main.cObjCoord[1] += 1;
                    break;
                case 3:
                    Main.cObjCoord[1] -= 2;
                    break;
                case 4:
                    Main.cObjCoord[1] -= 1;
            }
        }
    }

    // above is for 4x4 blocks, below is for 3x3 blocks.

    public static void secondTest3(boolean clockwise) {
        if (clockwise) {
            if (Main.rotationState == 1 || Main.rotationState == 4) {
                Main.cObjCoord[0] -= 1;
            } else {// 2 or 3
                Main.cObjCoord[0] += 1;
            }
        } else { // (counterclockwise)
            if (Main.rotationState == 1 || Main.rotationState == 2) {
                Main.cObjCoord[0] += 1;
            } else {// 3 or 4
                Main.cObjCoord[0] -= 1;
            }
        }
    }
    public static void thirdTest3() {
        if (Main.rotationState == 1 || Main.rotationState == 3) {
            Main.cObjCoord[1] += 1;
        } else {// 2 or 4
            Main.cObjCoord[1] -= 1;
        }
    }
    public static void fourthTest3() {
        if (Main.rotationState == 1 || Main.rotationState == 3) {
            Main.cObjCoord[1] -= 2;
        } else {// 2 or 4
            Main.cObjCoord[1] += 2;
        }
    }



    public static boolean blockOverlap(boolean overlap, String[][] cObject) {
        for (int y = 4; y >= 1; y--)
            for (int x = 1; x <= 4; x++)
                if (cObject[x][y].equals(Main.blockChar))
                    if (!Main.constArray[Main.cObjCoord[0] + (x - 1)][(Main.cObjCoord[1] - Main.heightEx) + y].equals(Main.nullChar)) {
                        System.out.println("block overlaps!!");
                        overlap = true;
                    }
        return overlap;
    }



    public static String[][] arrayCopier(String[][] mObject) {
        String[][] cObject = new String[5][5];
        for (int x = 0; x <= 4; x++)
            for (int y = 0; y <= 4; y++)
                cObject[x][y] = mObject[x][y];

        return cObject;
    }


}

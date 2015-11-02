package com.dudu.android.launcher.utils;

/**
 * Created by pc on 2015/10/30.
 */
public class ChoiseUtil {

    public static int getChoiseSize(String size) {
        String result = "";
        switch (size) {
            case Constants.ONE1:
            case Constants.ONE:
                result = Constants.ONE1;
                break;
            case Constants.TWO1:
            case Constants.TWO:
                result = Constants.TWO1;
                break;
            case Constants.THREE1:
            case Constants.THREE:
                result = Constants.THREE1;
                break;
            case Constants.FOUR1:
            case Constants.FOUR:
                result = Constants.FOUR1;
                break;
            case Constants.FIVE1:
            case Constants.FIVE:
                result = Constants.FIVE1;
                break;
            case Constants.SIX1:
            case Constants.SIX:
                result = Constants.SIX1;
                break;
            case Constants.SEVEN1:
            case Constants.SEVEN:
                result = Constants.SEVEN1;
                break;
            case Constants.EIGHT1:
            case Constants.EIGHT:
                result = Constants.EIGHT1;
                break;
            case Constants.NINE1:
            case Constants.NINE:
                result = Constants.NINE1;
                break;
            case Constants.TEN1:
            case Constants.TEN:
                result = Constants.TEN1;
                break;
            case Constants.ELEVEN1:
            case Constants.ELEVEN:
                result = Constants.ELEVEN1;
                break;
            case Constants.TWELVE1:
            case Constants.TWELVE:
                result = Constants.TWELVE1;
                break;
            case Constants.THIRTEEN1:
            case Constants.THIRTEEN:
                result = Constants.THIRTEEN1;
                break;
            case Constants.FOURTEEN1:
            case Constants.FOURTEEN:
                result = Constants.FOURTEEN1;
                break;
            case Constants.FIFTEEN1:
            case Constants.FIFTEEN:
                result = Constants.FIFTEEN1;
                break;
            case Constants.SIXTEEN1:
            case Constants.SIXTEEN:
                result = Constants.SIXTEEN1;
                break;
            case Constants.SEVENTEEN1:
            case Constants.SEVENTEEN:
                result = Constants.SEVENTEEN1;
                break;
            case Constants.EIGHTEEN1:
            case Constants.EIGHTEEN:
                result = Constants.EIGHTEEN1;
                break;
            case Constants.NINETEEN1:
            case Constants.NINETEEN:
                result = Constants.NINETEEN1;
                break;
            case Constants.TWENTW1:
            case Constants.TWENTW:
                result = Constants.TWENTW1;
                break;
            default:
                result = "0";
                break;
        }
        return Integer.parseInt(result);
    }
}

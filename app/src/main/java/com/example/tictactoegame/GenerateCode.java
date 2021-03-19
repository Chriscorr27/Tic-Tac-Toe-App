package com.example.tictactoegame;




import java.util.Calendar;
import java.util.Random;

public class GenerateCode {
    public static String getCode(String uid){
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        // specify length of random string
        int length = 4;

        for(int i = 0; i < length; i++) {

            // generate random index number
            int index = random.nextInt(uid.length());

            // get character specified by index
            // from the string
            char randomChar = uid.charAt(index);

            // append the character to string builder
            sb.append(randomChar);
        }
        String str = sb.toString();
        Calendar calendar = Calendar.getInstance();
        long instant= calendar.getTimeInMillis();;
        String code = str+(Long.toString(instant%10000));
        if(code.length()<8){
            StringBuilder sb1 = new StringBuilder();
            length = 8-code.length();

            for(int i = 0; i < length; i++) {

                // generate random index number
                int index = random.nextInt(uid.length());

                // get character specified by index
                // from the string
                char randomChar = uid.charAt(index);

                // append the character to string builder
                sb1.append(randomChar);
            }
            code=code+sb1.toString();
        }
        return code;
    }
}

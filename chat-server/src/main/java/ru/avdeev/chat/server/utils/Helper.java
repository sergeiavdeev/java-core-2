package ru.avdeev.chat.server.utils;

import java.security.MessageDigest;
import java.util.Arrays;

public class Helper {

    public static final String REGEX = "&~&";

    public static String getSHA256(String data){
        StringBuilder sb = new StringBuilder();
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(data.getBytes());
            byte[] byteData = md.digest();

            for (byte byteDatum : byteData) {
                sb.append(Integer.toString((byteDatum & 0xff) + 0x100, 16).substring(1));
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static String[] parseMessage(String message) {
        String[] params = new String[3];
        Arrays.fill(params, "");
        String[] tmp = message.split(REGEX);
        if (tmp.length > 0) {
            for (int i = 0; i < 3; i++) {
                params[i] = tmp[i];
                if (i == tmp.length - 1) break;
            }
        }
        return params;
    }

    public static String createMessage(String command, String p1, String p2) {
        return command + REGEX + p1 + REGEX + p2;
    }
}

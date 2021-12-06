package it.unical.unijira.utils;

public class RegexUtils {

    public static boolean isValidPassword(String password) {

        // Matches at least one number
        if(!password.matches("(?=.*[0-9]).*"))
            return false;

        // Matches at least one lower case letter
        if(!password.matches("(?=.*[a-z]).*"))
            return false;

        // Matches at least one upper case letter
        if(!password.matches("(?=.*[A-Z]).*"))
            return false;


        return password.length() >= 8;

    }


    public static boolean isValidUsername(String username) {

        return username.matches("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$");

    }

}

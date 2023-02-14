package main.system.hookSystem;

import kotlin.text.MatchResult;
import kotlin.text.Regex;
import main.system.commandSystem.repositories.Message;
import main.system.commandSystem.repositories.TwitchUser;
import main.system.commandSystem.repositories.TwitchUserPermissions;

import java.time.Instant;
import java.util.HashMap;

public class HookParser {
    public static void main(String[] args) {
        new HookMethodRunner();
        TwitchUser user = new TwitchUser("427320589", "orciument", 48, 1, TwitchUserPermissions.OWNER);
        Message message = new Message("","!irgendwas das ist der originale message text", user, false, false, false,false, null,null, "427320589", Instant.now());
//        System.out.println(parseCommand(message,"Das ist ein Test Command {follow {currentTime}  {somepreset} °das ist ein beispiel text der im command landet°} danke fürs zuhören!"));
//        System.out.println(parseCommand(message,"{follow {currentTime}  {somepreset} °das ist ein beispiel text der im command landet°}"));
        System.out.println("Output: "+ parseCommand(message,"Das ist ein Test Command {Math °+° {senderSubMonths} {randomInt} } danke fürs zuhören!  @{sender}: {currentTime}"));
//        System.out.println(parseCommand(message,"Das ist ein Test Command {Math °+° {senderSubMonths} {randomInt} }"));
    }

    public static String parseCommand(Message message, String input) {
        if (!equalBracketNumber(input))
            return "ERROR Alle geöffneten Klammern müssen wieder geschlossen werden!";
        //TODO LOGGER
//        System.out.println("Input: " + input);

        String output = "";
        int startHookIndex = input.indexOf('{');
        while (startHookIndex > 0) {
            //Add part bevor Hook start to output without modifying it
            output += input.substring(0, startHookIndex);

            int endHookIndex = findClosingBracket(input);
            String substring = input.substring(startHookIndex, endHookIndex + 1);

            //Parse the substring Hook
            output += parseHooks(message,substring);

            //Delete already parsed part from Input
            input = input.substring(endHookIndex + 1);
            startHookIndex = input.indexOf('{');
        }
        return output + input;
    }

    private static String parseHooks(Message message, String hook) {
        //Skip first {
        hook = hook.substring(1);

        String name;
        HashMap<Integer, String> parameter = new HashMap<>();

        //Does not have Parameter
        if (!hook.contains("{") && !hook.contains("°")) {
            name = hook.substring(0, hook.indexOf("}"));
            //Execute hook
            return HookMethodRunner.runHook(name, message,null);
        }


        //Has a parameter
        name = textTillControlChar(hook).trim();
        //Remove name from the hook String
        hook = hook.replace(name, "");

        int index = 0;
        //Loop
        while (hook.length() > 0) {
            switch (hook.charAt(0)) {
                //Parameter is Hook
                case '{' -> {
                    //Find Part of the String that represents the Hook that is the Parameter
                    int endIndex = findClosingBracket(hook);
                    String substring = hook.substring(0, endIndex + 1);

                    //Parse the Hook recursive
                    String returnString = parseHooks(message, substring);

                    //Add to Parameter Map
                    parameter.put(index, returnString);
                    index++;

                    //Remove parsed part from String
                    hook = hook.replace(substring, "");
                }
                //Parameter is String
                case '°' -> {
                    int endIndex = hook.substring(1).indexOf("°") + 1;
                    String substring = hook.substring(1, endIndex);

                    //Add to Parameter Map
                    parameter.put(index, substring);
                    index++;

                    //Remove parsed part from String
                    hook = hook.replace("°" + substring + "°", "");
                }
                case '}', ' ' -> hook = hook.substring(1);
                //                default -> hook = hook.substring(1);
            }
        }
        //Execute hook
        return HookMethodRunner.runHook(name, message, parameter);
    }

    private static String textTillControlChar(String input) {
        Regex regex = new Regex("^[^{}°]*");
        MatchResult result = regex.find(input, 0);
        assert result != null;
        return result.getValue();
    }

    /**
     * Nimmt zb. so einen Input:
     * <br> {@code  {ifequals {messageSource} °User° {atUser} } restlicher COmmand, hallo!}
     * und returned den Index der schließenden Klammer
     */
    private static int findClosingBracket(String input) {
        int start = input.indexOf('{') + 1;
        int depth = 1;

        for (int i = start; i < input.toCharArray().length; i++) {
            char c = input.toCharArray()[i];
            switch (c) {
                case '{' -> depth++;
                case '}' -> depth--;
            }
            if (depth == 0)
                return i;
        }
        return 0;
    }

    private static boolean equalBracketNumber(String input) {
        int open = 0;
        int close = 0;

        for (char aChar : input.toCharArray()) {
            if (aChar == '{') {
                open++;
            } else if (aChar == '}') {
                close++;
            }
        }
        return open == close;
    }
}

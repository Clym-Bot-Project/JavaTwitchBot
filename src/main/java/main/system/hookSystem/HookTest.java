package main.system.hookSystem;

import java.util.Arrays;
import java.util.StringJoiner;

public class HookTest {
    private static final String hookStart = " {";
    private static final String hookEnd = "} ";

    public static void main(String[] args) {
        HookTest.testmesage();
    }

    private static void testmesage() {
        System.out.println(
                "Das ist ein Test Command"
                        + hookStart + "follow"
                            + hookStart + "currentTime" + hookEnd
                            + hookStart + "somepreset" + hookEnd
                            + "°das ist ein beispiel text der im command landet°"
                        + hookEnd
                + "danke fürs zuhören! "
                        + hookStart + "ifequals"
                            + hookStart + "messageSource" + hookEnd
                            + "°User°"
                            + hookStart + "atUser" + hookEnd
                        + hookEnd
        );

        System.out.println(
                "Das ist ein Test Command"
                + b("follow",
                        b("currentTime",
                                b("getCurrentTimeZone",
                                    b("calcFormat",
                                            s("HH:FFMWW:SSS")
                                    )
                                )
                                ),
                        b("somepreset"),
                        s("das ist ein beispiel text der im command landet")
                )
                + "danke fürs zuhören! "
                + b("ifequals",
                        b("messageSource"),
                        s("User"),
                        b("atUser")
                )
        );
    }

    public static String b(String name, String... args) {
        StringJoiner joiner = new StringJoiner("");
        Arrays.stream(args).toList().forEach(joiner::add);
        return " {" + name + joiner + "} ";
    }

    public static String s(String string) {
        return " °" + string + "° ";
    }

}

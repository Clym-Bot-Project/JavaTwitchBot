package main.system.hookSystem;

import main.system.commandSystem.repositories.Message;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

public class HookMethodRunner {

    private static final Set<Method> hooks = scanForHooks();

    public static String runHook(String name, Message message, HashMap<Integer, String> parameter) {
        for (Method method : hooks) {
            if (method.getName().equalsIgnoreCase(name))
                return invokeHookMethod(method, message, parameter);
        }
        return "{ERROR cant find Hook with Name: " + name + "}";
    }

    private static String invokeHookMethod(Method method, Message message, HashMap<Integer, String> parameter) {
        ArrayList<Object> list = new ArrayList<>();
        if (parameter != null) {
            for (int i = 0; i < parameter.size(); i++) {
                list.add(parameter.get(i));
            }
        }

        if (method.getParameterCount() >= 1 && method.getParameterTypes()[0].equals(Message.class)) {
            list.add(0, message);
        }

        try {
            return (String) method.invoke(null, list.toArray());
        } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
            e.printStackTrace();
            return "{ERROR cant execute Hook: " + method.getName() + " " + e.getMessage() + " args: " + list.size() + " argsN: " + method.getParameterCount() + "}";
        }
    }

    private static Set<Method> scanForHooks() {
        Reflections reflections = new Reflections(new ConfigurationBuilder().forPackages("main.main.modules").addScanners(Scanners.MethodsAnnotated));
        Set<Method> methods = reflections.getMethodsAnnotatedWith(Hook.class);
        //Die Methoden müssen static sein und müssen als ersten Parameter die message haben und danach eine beliebige Anzahl von Strings
        Set<Method> collect = methods.stream()
                .filter(method -> Modifier.isStatic(method.getModifiers()))
                .filter(method -> method.getReturnType().equals(String.class))
                .filter(HookMethodRunner::parameterFilter)
                .collect(Collectors.toSet());


        //Debug Logging
        //TODO switch to logger
        String output = "";
        for (Method method : collect) {
            output += method.getName() + "(";

            for (Class<?> aClass : method.getParameterTypes())
                output += aClass.getName() + " ,";


            if (method.getParameterCount() > 0)
                output = output.substring(0, output.length() - 2);

            output += ")";
//            System.out.println(output);
            output = "";
        }
        return collect;
    }

    private static boolean parameterFilter(Method method) {
        Object[] distinctParameter = Arrays.stream(method.getParameterTypes()).distinct().toArray();

        boolean returnValue;
        switch (distinctParameter.length) {
            case 0 -> returnValue = true;
            case 1 -> returnValue = distinctParameter[0].equals(Message.class) || distinctParameter[0].equals(String.class);
            case 2 -> returnValue = method.getParameterTypes()[0].equals(Message.class) && distinctParameter[1].equals(String.class);
            default -> returnValue = false;
        }
        //System.out.println(method.getName() + ": " + count + " " + Arrays.toString(distinctParameter) + " -> " + returnValue);
        return returnValue;
    }
}

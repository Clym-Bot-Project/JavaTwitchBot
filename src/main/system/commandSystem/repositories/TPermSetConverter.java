package main.system.commandSystem.repositories;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.HashSet;
import java.util.List;
import java.util.StringJoiner;

@Converter
public class TPermSetConverter implements AttributeConverter<HashSet<TwitchUserPermissions>, String> {
    @Override
    public String convertToDatabaseColumn(HashSet<TwitchUserPermissions> twitchUserPermissions) {
        StringJoiner joiner = new StringJoiner("; ");
        for (TwitchUserPermissions permissions : twitchUserPermissions) {
            joiner.add(permissions.name());
        }
        return joiner.toString();
    }

    @Override
    public HashSet<TwitchUserPermissions> convertToEntityAttribute(String s) {
        String[] strings = s.split(";");
        HashSet<TwitchUserPermissions> permissions = new HashSet<>();
        for (String s1 : strings) {
            permissions.add(TwitchUserPermissions.valueOf(s1.trim()));
        }
        return permissions;
    }

//    @Override
//    public TwitchUserPermissions[] convertToDatabaseColumn(HashSet<TwitchUserPermissions> twitchUserPermissions) {
//        return twitchUserPermissions.toArray(TwitchUserPermissions[]::new);
//    }
//
//    @Override
//    public HashSet<TwitchUserPermissions> convertToEntityAttribute(TwitchUserPermissions[] twitchUserPermissions) {
//        return new HashSet<>(List.of(twitchUserPermissions));
//    }
}

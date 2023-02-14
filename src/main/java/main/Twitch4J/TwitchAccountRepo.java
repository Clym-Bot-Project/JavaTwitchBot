package main.Twitch4J;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface TwitchAccountRepo extends CrudRepository<TwitchAccount, String> {

    @NotNull ArrayList<TwitchAccount> findAll();

    boolean existsByRole(String role);

    Optional<TwitchAccount> getByRole(String role);
}

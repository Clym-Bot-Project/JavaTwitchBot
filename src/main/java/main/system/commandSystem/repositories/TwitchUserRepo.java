package main.system.commandSystem.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TwitchUserRepo extends CrudRepository<TwitchUser, String> {
}

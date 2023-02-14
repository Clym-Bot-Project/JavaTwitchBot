package main.system.commandSystem.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommandRepo extends CrudRepository<Command, String> {
}

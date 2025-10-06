package demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import demo.model.CharacterD;

public interface CharacterRepository extends MongoRepository<CharacterD, String> {

	Optional<CharacterD> findByName(String name);

	List<CharacterD> findAllByName(String name);}


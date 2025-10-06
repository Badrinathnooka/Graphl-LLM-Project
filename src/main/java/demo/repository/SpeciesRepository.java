package demo.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import demo.model.Species;

public interface SpeciesRepository extends MongoRepository<Species, String> {

	List<Species> findAllByName(String name);}

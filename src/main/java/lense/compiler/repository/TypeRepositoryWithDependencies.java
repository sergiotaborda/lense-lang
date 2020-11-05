package lense.compiler.repository;

public interface TypeRepositoryWithDependencies extends TypeRepository {

	
	public void addDependency(TypeRepository other);
}

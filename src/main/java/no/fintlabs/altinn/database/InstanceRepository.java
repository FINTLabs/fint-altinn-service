package no.fintlabs.altinn.database;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InstanceRepository extends JpaRepository<Instance, Long> {

    default List<Instance> findAllInstances() {
        return findAll();
    }

    Instance findByInstanceId(String instanceId);

    Instance findFirstByInstanceIdOrderByLastUpdatedDesc(String instanceId);

    default void saveInstance(Instance instance) {
        save(instance);
    }
}

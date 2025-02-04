package no.fintlabs.altinn.database;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InstanceRepository extends JpaRepository<Instance, Long> {

    default List<Instance> findAllInstances() {
        return findAll();
    }

    default Instance getInstance(String instanceId) {
        return findAll().stream()
                .filter(instance -> instance.getInstanceId().equals(instanceId))
                .findFirst()
                .orElse(null);
    }

    default void saveInstance(Instance instance) {
        save(instance);
    }
}

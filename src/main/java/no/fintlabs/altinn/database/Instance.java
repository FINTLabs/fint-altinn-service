package no.fintlabs.altinn.database;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.Collection;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Instance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String instanceId;

    @Column(nullable = false)
    private String fintOrgId;

    @Column(nullable = false)
    private boolean completed;

    @Column(nullable = false)
    @UpdateTimestamp
    private Instant lastUpdated;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "instance", fetch = FetchType.EAGER)
    private Collection<InstanceFile> files;
}

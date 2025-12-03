package no.novari.altinn.database;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "files")
@Entity
public class InstanceFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String dataType;

    @Column(nullable = false)
    private String url;

    @Column
    private String contentType;

    @Column
    private String fileName;

    @ManyToOne
    @JoinColumn(name = "instance_id", nullable = false)
    private Instance instance;
}

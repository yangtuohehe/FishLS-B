package com.example.fishlsb.entity;

import jakarta.persistence.*;
import org.locationtech.jts.geom.Point;
import java.util.UUID;

@Entity
@Table(name = "entity_static_observation")
public class EntityStaticObservation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "static_id")
    private UUID staticId;

    @Column(name = "entity_id")
    private UUID entityId;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "geom", columnDefinition = "geometry(Point, 4326)")
    private Point geom;

    @OneToOne
    @JoinColumn(name = "entity_id", insertable = false, updatable = false)
    private TwinEntity twinEntity;

    public UUID getStaticId() { return staticId; }
    public void setStaticId(UUID staticId) { this.staticId = staticId; }
    public UUID getEntityId() { return entityId; }
    public void setEntityId(UUID entityId) { this.entityId = entityId; }
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public Point getGeom() { return geom; }
    public void setGeom(Point geom) { this.geom = geom; }
    public TwinEntity getTwinEntity() { return twinEntity; }
    public void setTwinEntity(TwinEntity twinEntity) { this.twinEntity = twinEntity; }
}
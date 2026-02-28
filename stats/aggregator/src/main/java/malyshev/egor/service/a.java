package malyshev.egor.service;

import java.util.Optional;

public interface SnapshotService {
    Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event);
}

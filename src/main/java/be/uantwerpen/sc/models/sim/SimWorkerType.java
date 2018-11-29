package be.uantwerpen.sc.models.sim;

public enum SimWorkerType {
    car,
    f1,
    drone;

    public static SimWorkerType StringToType(String type) {
        switch (type.toUpperCase()) {
            case("CAR"): return  SimWorkerType.car;
            case("F1"): return  SimWorkerType.f1;
            case("DRONE"): return  SimWorkerType.drone;
            default: throw new IllegalArgumentException("Unkwown worker type");
        }
    }
}

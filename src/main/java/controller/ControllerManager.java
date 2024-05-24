package controller;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class ControllerManager {
    private Map<String, BaseController> controllers = new HashMap<>();

    public ControllerManager() {
        registerControllers();
    }

    private void registerControllers() {
        controllers.put("/user", new UserController());
        // Register other controllers here
    }

    public BaseController getController(String path) {
        for (String registeredPath : controllers.keySet()) {
            if (path.startsWith(registeredPath)) {
                return controllers.get(registeredPath);
            }
        }
        return null;
    }
}
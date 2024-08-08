/**
 * La clase SessionManager se utiliza para gestionar la sesión del usuario actual.
 * Proporciona métodos para establecer y obtener el ID del usuario que ha iniciado sesión.
 */
public class SessionManager {
    private static int currentUserId;

    /**
     * Establece el ID del usuario actual.
     *
     * @param userId El ID del usuario que ha iniciado sesión.
     */
    public static void setCurrentUserId(int userId) {
        currentUserId = userId;
    }

    /**
     * Obtiene el ID del usuario actual.
     *
     * @return El ID del usuario que ha iniciado sesión.
     */
    public static int getCurrentUserId() {
        return currentUserId;
    }
}

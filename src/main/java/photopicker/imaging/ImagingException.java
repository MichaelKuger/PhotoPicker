package photopicker.imaging;

public class ImagingException extends Exception {
    public ImagingException(String message) {
        super(message);
    }

    public ImagingException(String message, Throwable cause) {
        super(message, cause);
    }
}

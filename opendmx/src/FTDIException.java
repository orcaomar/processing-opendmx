package opendmx;
/**
 * Simple exception for OpenDMX hardware
 *
 * @author      Omar Khan
 */
public class FTDIException extends Exception {
  public FTDIException(String message) {
    super(message);
  } 
}
